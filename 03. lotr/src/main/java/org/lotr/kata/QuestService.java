package org.lotr.kata;

import java.util.List;

import static org.lotr.kata.Characters.*;

public class QuestService {
    private final InventoryManagerInterface inventoryManager;
    private final QuestDatabaseInterface questDatabase;
    private final CharacterServiceInterface characterService;

    public QuestService(InventoryManagerInterface inventoryManager,
                        QuestDatabaseInterface questDatabase,
                        CharacterServiceInterface characterService) {
        this.inventoryManager = inventoryManager;
        this.questDatabase = questDatabase;
        this.characterService = characterService;
    }

    public QuestResult startQuest(String characterName, QuestType questType, List<String> companions, List<MiddleEarthItem> items) {
        verifyCharacterAndCompanionsAreAvailable(characterName, companions);

        verifyItemsAreInventory(items);

        // Check quest specific requirements
        if (questType == QuestType.DESTROY_RING) {
            boolean hasRing = false;
            for (MiddleEarthItem item : items) {
                if (item.isR) {
                    hasRing = true;
                    break;
                }
            }
            if (!hasRing) {
                throw new IllegalStateException("Cannot start DESTROY_RING quest without a ring!");
            }
        }

        if (questType == QuestType.DIPLOMATIC_MISSION && characterService.getCharacterType(characterName).equals(HOBBIT)
                && !characterService.getCharacterLevel(characterName).equals("Experienced")) {
            throw new IllegalStateException("Inexperienced Hobbits cannot lead diplomatic missions.");
        }

        // Calculate quest success chance
        double baseSuccessChance = calculateBaseSuccessChance(characterName, questType, companions, items);

        // Apply modifiers based on character, companions, items
        double finalSuccessChance = applyModifiers(baseSuccessChance, characterName, questType, companions);

        // Determine if quest succeeds
        boolean success = Math.random() < finalSuccessChance;

        // Apply quest effects
        String characterType = characterService.getCharacterType(characterName);
        int rewardAmount = success
                ? calculateReward(questType, characterType, companions, items)
                : 0;

        // Update character statuses
        characterService.completeQuest(characterName, success);
        for (String companion : companions) {
            characterService.completeQuest(companion, success);
        }

        // Remove consumed items
        for (MiddleEarthItem item : items) {
            if ((questType == QuestType.DESTROY_RING && item.isR) || Math.random() < 0.2) {
                inventoryManager.removeItem(item.n, item.q);
            }
        }

        // Create and save quest result
        QuestResult result = new QuestResult(characterName, questType, success, rewardAmount, companions);
        questDatabase.saveQuestResult(result);

        return result;
    }

    private void verifyItemsAreInventory(List<MiddleEarthItem> items) {
        List<MiddleEarthItem> inventoryItems = inventoryManager.getAllItems();
        if (items.stream().anyMatch(item -> !inventoryItems.contains(item))) {
            throw new IllegalStateException("Item is not available in sufficient quantity.");
        }
    }

    private void verifyCharacterAndCompanionsAreAvailable(String characterName, List<String> companions) {
        if (!characterService.isCharacterAvailable(characterName)) {
            throw new IllegalStateException("Character " + characterName + " is not available for quests.");
        }

        List<String> unavailableCompanions = companions.stream().filter(companion -> !characterService.isCharacterAvailable(companion)).toList();
        if (!unavailableCompanions.isEmpty()) {
            throw new IllegalStateException("Companion " + unavailableCompanions.getFirst() + " is not available for quests.");
        }
    }

    private double calculateBaseSuccessChance(String characterName, QuestType questType, List<String> companions, List<MiddleEarthItem> items) {
        double baseChance = 0.5; // 50% base chance

        // Adjust based on character level
        String level = characterService.getCharacterLevel(characterName);
        baseChance += switch (level) {
            case "Novice" -> -0.2;
            case "Experienced" -> 0.1;
            case "Veteran" -> 0.2;
            case "Legendary" -> 0.3;
            default -> 0;
        };

        // Adjust based on quest type
        if (questType == QuestType.DESTROY_RING) {
            baseChance -= 0.3; // Very difficult
        } else if (questType == QuestType.DEFEAT_ORCS) {
            baseChance += 0.1; // Relatively easy
        } else if (questType == QuestType.FIND_ARTIFACT) {
            baseChance -= 0.1; // Somewhat difficult
        }

        // Adjust based on number of companions
        baseChance += companions.size() * 0.05;

        // Powerful items help
        for (MiddleEarthItem item : items) {
            if (item.isR) baseChance += 0.15;
            if (item.isMithril) baseChance += 0.1;
            if (item.isGood) baseChance += 0.05;
            if (item.isC) baseChance -= 0.15; // Cursed items hurt chances
        }

        return Math.clamp(baseChance, 0.1, 0.95); // Clamp between 10% and 95%
    }

    private double applyModifiers(double baseChance, String characterName, QuestType questType, List<String> companions) {
        String characterType = characterService.getCharacterType(characterName);

        // Apply character type modifier
        if (CHARACTER_BONUSES.containsKey(characterType) && CHARACTER_BONUSES.get(characterType).containsKey(questType)) {
            double typeBonus = CHARACTER_BONUSES.get(characterType).get(questType);
            baseChance *= typeBonus;
        }

        // Check for special combinations
        boolean hasElfAndDwarf = false;
        boolean hasHobbitAndWizard = false;

        if (characterType.equals(ELF)) {
            for (String companion : companions) {
                if (characterService.getCharacterType(companion).equals(DWARF)) {
                    hasElfAndDwarf = true;
                    break;
                }
            }
        } else if (characterType.equals(DWARF)) {
            for (String companion : companions) {
                if (characterService.getCharacterType(companion).equals(ELF)) {
                    hasElfAndDwarf = true;
                    break;
                }
            }
        }

        if (characterType.equals(HOBBIT)) {
            for (String companion : companions) {
                if (characterService.getCharacterType(companion).equals(WIZARD)) {
                    hasHobbitAndWizard = true;
                    break;
                }
            }
        } else if (characterType.equals(WIZARD)) {
            for (String companion : companions) {
                if (characterService.getCharacterType(companion).equals(HOBBIT)) {
                    hasHobbitAndWizard = true;
                    break;
                }
            }
        }

        // Apply special combination bonuses
        if (hasElfAndDwarf) {
            baseChance += 0.05; // Unlikely alliance bonus
        }
        if (hasHobbitAndWizard) {
            baseChance += 0.1; // Fellowship bonus
        }

        // Weather effects (just to add complexity)
        if (questDatabase.getCurrentWeather().equals("Stormy")) {
            baseChance -= 0.1;
        } else if (questDatabase.getCurrentWeather().equals("Clear")) {
            baseChance += 0.05;
        }

        // War time effects
        if (inventoryManager.isWarTime()) {
            if (questType == QuestType.DEFEAT_ORCS) {
                baseChance -= 0.1; // More orcs during war
            } else if (questType == QuestType.DIPLOMATIC_MISSION) {
                baseChance -= 0.2; // Diplomacy harder during war
            }
        }

        return Math.clamp(baseChance, 0.1, 0.95); // Clamp between 10% and 95%
    }

    private int calculateReward(QuestType questType, String characterType, List<String> companions, List<MiddleEarthItem> items) {
        var baseReward = QUEST_BASE_REWARDS.getOrDefault(questType, 100);
        var typeBonus = getTypeBonus(questType, characterType);
        var finalReward = computeReward(companions, items, baseReward, typeBonus);

        return roundToNearest10(finalReward);
    }

    private static double computeReward(List<String> companions, List<MiddleEarthItem> items, int baseReward, double typeBonus) {
        double finalReward = baseReward * typeBonus / (companions.size() + 1);
        return items.stream()
                .mapToDouble(MiddleEarthItem::getRate)
                .reduce(finalReward, (reward, rate) -> reward * rate);
    }

    private static int roundToNearest10(double finalReward) {
        return (int) (Math.round(finalReward / 10) * 10);
    }

    private static double getTypeBonus(QuestType questType, String characterType) {
        return CHARACTER_BONUSES.containsKey(characterType) && CHARACTER_BONUSES.get(characterType).containsKey(questType)
                ? CHARACTER_BONUSES.get(characterType).get(questType)
                : 1.0;
    }
}