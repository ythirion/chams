package org.lotr.kata;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestService {
    private static final String WIZARD = "Wizard";
    private static final Map<QuestType, Integer> QUEST_BASE_REWARDS = new EnumMap<>(QuestType.class);
    private static final Map<String, Map<QuestType, Double>> CHARACTER_BONUSES = new HashMap<>();

    public static final String HOBBIT = "Hobbit";

    public static final String HUMAN = "Human";

    public static final String DWARF = "Dwarf";

    public static final String ELF = "Elf";

    static {
        // Initialize base rewards
        QUEST_BASE_REWARDS.put(QuestType.DESTROY_RING, 10000);
        QUEST_BASE_REWARDS.put(QuestType.DEFEAT_ORCS, 500);
        QUEST_BASE_REWARDS.put(QuestType.ESCORT_HOBBIT, 300);
        QUEST_BASE_REWARDS.put(QuestType.FIND_ARTIFACT, 800);
        QUEST_BASE_REWARDS.put(QuestType.DIPLOMATIC_MISSION, 400);
        
        // Initialize character type bonuses
        Map<QuestType, Double> elfBonuses = new EnumMap<>(QuestType.class);
        elfBonuses.put(QuestType.DESTROY_RING, 1.1);
        elfBonuses.put(QuestType.DEFEAT_ORCS, 1.2);
        elfBonuses.put(QuestType.ESCORT_HOBBIT, 1.3);
        elfBonuses.put(QuestType.FIND_ARTIFACT, 1.5);
        elfBonuses.put(QuestType.DIPLOMATIC_MISSION, 1.4);
        CHARACTER_BONUSES.put(ELF, elfBonuses);
        
        Map<QuestType, Double> dwarfBonuses = new EnumMap<>(QuestType.class);
        dwarfBonuses.put(QuestType.DESTROY_RING, 1.0);
        dwarfBonuses.put(QuestType.DEFEAT_ORCS, 1.4);
        dwarfBonuses.put(QuestType.ESCORT_HOBBIT, 1.0);
        dwarfBonuses.put(QuestType.FIND_ARTIFACT, 1.6);
        dwarfBonuses.put(QuestType.DIPLOMATIC_MISSION, 0.8);
        CHARACTER_BONUSES.put(DWARF, dwarfBonuses);
        
        Map<QuestType, Double> humanBonuses = new EnumMap<>(QuestType.class);
        humanBonuses.put(QuestType.DESTROY_RING, 1.0);
        humanBonuses.put(QuestType.DEFEAT_ORCS, 1.3);
        humanBonuses.put(QuestType.ESCORT_HOBBIT, 1.2);
        humanBonuses.put(QuestType.FIND_ARTIFACT, 1.1);
        humanBonuses.put(QuestType.DIPLOMATIC_MISSION, 1.5);
        CHARACTER_BONUSES.put(HUMAN, humanBonuses);
        
        Map<QuestType, Double> hobbitBonuses = new EnumMap<>(QuestType.class);
        hobbitBonuses.put(QuestType.DESTROY_RING, 1.5);
        hobbitBonuses.put(QuestType.DEFEAT_ORCS, 0.7);
        hobbitBonuses.put(QuestType.ESCORT_HOBBIT, 1.0);
        hobbitBonuses.put(QuestType.FIND_ARTIFACT, 1.0);
        hobbitBonuses.put(QuestType.DIPLOMATIC_MISSION, 1.1);
        CHARACTER_BONUSES.put(HOBBIT, hobbitBonuses);
        
        Map<QuestType, Double> wizardBonuses = new EnumMap<>(QuestType.class);
        wizardBonuses.put(QuestType.DESTROY_RING, 1.3);
        wizardBonuses.put(QuestType.DEFEAT_ORCS, 1.4);
        wizardBonuses.put(QuestType.ESCORT_HOBBIT, 1.3);
        wizardBonuses.put(QuestType.FIND_ARTIFACT, 1.6);
        wizardBonuses.put(QuestType.DIPLOMATIC_MISSION, 1.7);
        CHARACTER_BONUSES.put(WIZARD, wizardBonuses);
    }

    private InventoryManagerInterface inventoryManager;
    private QuestDatabaseInterface questDatabase;
    private CharacterServiceInterface characterService;

    // Pour les tests - injection de dépendances
    public QuestService(InventoryManagerInterface inventoryManager, 
                      QuestDatabaseInterface questDatabase, 
                      CharacterServiceInterface characterService) {
        this.inventoryManager = inventoryManager;
        this.questDatabase = questDatabase;
        this.characterService = characterService;
    }
    
    public QuestResult startQuest(String characterName, QuestType questType, List<String> companions, List<MiddleEarthItem> items) {
        if (!characterService.isCharacterAvailable(characterName)) {
            throw new IllegalStateException("Character " + characterName + " is not available for quests.");
        }
        
        // Verify companions are available
        for (String companion : companions) {
            if (!characterService.isCharacterAvailable(companion)) {
                throw new IllegalStateException("Companion " + companion + " is not available for quests.");
            }
        }
        
        // Verify items are in inventory
        for (MiddleEarthItem item : items) {
            boolean found = false;
            for (MiddleEarthItem inventoryItem : inventoryManager.getAllItems()) {
                if (inventoryItem.n.equals(item.n) && inventoryItem.q >= item.q) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalStateException("Item " + item.n + " is not available in sufficient quantity.");
            }
        }
        
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
            if (item.isM) baseChance += 0.1;
            if (item.isG) baseChance += 0.05;
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
        int baseReward = QUEST_BASE_REWARDS.getOrDefault(questType, 100);
        
        // Apply character type bonus
        double typeBonus = 1.0;
        if (CHARACTER_BONUSES.containsKey(characterType) && CHARACTER_BONUSES.get(characterType).containsKey(questType)) {
            typeBonus = CHARACTER_BONUSES.get(characterType).get(questType);
        }
        
        // Calculate final reward
        double finalReward = baseReward * typeBonus;
        
        // Companions reduce individual share
        finalReward = finalReward / (companions.size() + 1);
        
        // Special items can increase reward
        for (MiddleEarthItem item : items) {
            if (item.isG) finalReward *= 1.1;
            if (item.isM) finalReward *= 1.2;
        }
        
        // Round to nearest 10
        return (int) (Math.round(finalReward / 10) * 10);
    }
}