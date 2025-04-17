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

    public QuestResult startQuest(String characterName, QuestType questType, List<String> companionNames, List<MiddleEarthItem> items) {
        var companions = Companions.from(characterService, characterName, companionNames);
        var middleEarthItems = MiddleEarthItems.from(inventoryManager, items);
        var characterType = characterService.getCharacterType(characterName);

        veryQuest(characterName, questType, middleEarthItems);

        var success = defineSuccess(characterName, questType, companions, middleEarthItems);
        var rewardAmount = calculateRewardAmount(questType, success, characterType, companions, middleEarthItems);

        characterService.completeQuest(characterName, success);
        companions.completeQuest(success);
        middleEarthItems.removeConsumedItems(inventoryManager, questType);

        var result = new QuestResult(characterName, questType, success, rewardAmount, companions);
        questDatabase.saveQuestResult(result);

        return result;
    }

    private int calculateRewardAmount(QuestType questType, boolean success, String characterType, Companions companions, MiddleEarthItems items) {
        return success
                ? calculateReward(questType, characterType, companions, items)
                : 0;
    }

    private boolean defineSuccess(String characterName, QuestType questType, Companions companions, MiddleEarthItems items) {
        var baseSuccessChance = calculateBaseSuccessChance(characterName, questType, companions, items);
        var finalSuccessChance = applyModifiers(baseSuccessChance, characterName, questType, companions);

        return Math.random() < finalSuccessChance;
    }

    private void veryQuest(String characterName, QuestType questType, MiddleEarthItems items) {
        if (questType == QuestType.DESTROY_RING && !items.containsRing()) {
            throw new IllegalStateException("Cannot start DESTROY_RING quest without a ring!");
        }

        if (questType == QuestType.DIPLOMATIC_MISSION
                && characterService.getCharacterType(characterName).equals(HOBBIT)
                && !characterService.getCharacterLevel(characterName).equals("Experienced")) {
            throw new IllegalStateException("Inexperienced Hobbits cannot lead diplomatic missions.");
        }
    }

    private double calculateBaseSuccessChance(String characterName, QuestType questType, Companions companions, List<MiddleEarthItem> items) {
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

        baseChance += companions.getBaseChance();

        // Powerful items help
        for (MiddleEarthItem item : items) {
            if (item.isRing) baseChance += 0.15;
            if (item.isMithril) baseChance += 0.1;
            if (item.isGood) baseChance += 0.05;
            if (item.isC) baseChance -= 0.15; // Cursed items hurt chances
        }

        return Math.clamp(baseChance, 0.1, 0.95); // Clamp between 10% and 95%
    }

    private double applyModifiers(double baseChance, String characterName, QuestType questType, Companions companions) {
        String characterType = characterService.getCharacterType(characterName);

        // Apply character type modifier
        if (CHARACTER_BONUSES.containsKey(characterType) && CHARACTER_BONUSES.get(characterType).containsKey(questType)) {
            double typeBonus = CHARACTER_BONUSES.get(characterType).get(questType);
            baseChance *= typeBonus;
        }

        if (companions.containsElfAndDwarf()) {
            baseChance += 0.05; // Unlikely alliance bonus
        }
        if (companions.containsHobbitAndWizard()) {
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

    private int calculateReward(QuestType questType, String characterType, Companions companions, List<MiddleEarthItem> items) {
        var baseReward = QUEST_BASE_REWARDS.getOrDefault(questType, 100);
        var typeBonus = getTypeBonus(questType, characterType);
        var finalReward = computeReward(companions, items, baseReward, typeBonus);

        return roundToNearest10(finalReward);
    }

    private static double computeReward(Companions companions, List<MiddleEarthItem> items, int baseReward, double typeBonus) {
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