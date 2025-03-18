package org.lotr.kata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lotr.kata.QuestService.QuestDatabaseInterface;

public class QuestDatabase implements QuestDatabaseInterface {
    private static QuestDatabase instance;
    private List<QuestResult> questResults = new ArrayList<>();
    private Map<String, List<QuestResult>> questsByCharacter = new HashMap<>();
    private String currentWeather = "Clear"; // Default weather
    
    private QuestDatabase() {
        // Private constructor for singleton
    }
    
    public static QuestDatabase getInstance() {
        if (instance == null) {
            instance = new QuestDatabase();
        }
        return instance;
    }
    
    public void saveQuestResult(QuestResult result) {
        questResults.add(result);
        
        // Update character quest map
        String characterName = result.getCharacterName();
        if (!questsByCharacter.containsKey(characterName)) {
            questsByCharacter.put(characterName, new ArrayList<>());
        }
        questsByCharacter.get(characterName).add(result);
        
        // Also update for companions
        for (String companion : result.getCompanions()) {
            if (!questsByCharacter.containsKey(companion)) {
                questsByCharacter.put(companion, new ArrayList<>());
            }
            questsByCharacter.get(companion).add(result);
        }
    }
    
    public List<QuestResult> getQuestsByCharacter(String characterName) {
        return questsByCharacter.getOrDefault(characterName, new ArrayList<>());
    }
    
    public List<QuestResult> getQuestsByType(QuestType type) {
        List<QuestResult> result = new ArrayList<>();
        for (QuestResult quest : questResults) {
            if (quest.getQuestType() == type) {
                result.add(quest);
            }
        }
        return result;
    }
    
    public List<QuestResult> getSuccessfulQuests() {
        List<QuestResult> result = new ArrayList<>();
        for (QuestResult quest : questResults) {
            if (quest.isSuccess()) {
                result.add(quest);
            }
        }
        return result;
    }
    
    public List<QuestResult> getFailedQuests() {
        List<QuestResult> result = new ArrayList<>();
        for (QuestResult quest : questResults) {
            if (!quest.isSuccess()) {
                result.add(quest);
            }
        }
        return result;
    }
    
    public int getTotalRewardsEarned() {
        int total = 0;
        for (QuestResult quest : questResults) {
            if (quest.isSuccess()) {
                total += quest.getRewardAmount();
            }
        }
        return total;
    }
    
    public String getCurrentWeather() {
        return currentWeather;
    }
    
    public void setCurrentWeather(String weather) {
        if (weather.equals("Clear") || weather.equals("Rainy") || weather.equals("Stormy") || weather.equals("Foggy")) {
            this.currentWeather = weather;
        } else {
            throw new IllegalArgumentException("Invalid weather condition: " + weather);
        }
    }
    
    public String generateQuestReport() {
        StringBuilder report = new StringBuilder();
        report.append("QUEST REPORT\n");
        report.append("------------\n");
        report.append("Total Quests: ").append(questResults.size()).append("\n");
        report.append("Successful: ").append(getSuccessfulQuests().size()).append("\n");
        report.append("Failed: ").append(getFailedQuests().size()).append("\n");
        report.append("Total Rewards: ").append(getTotalRewardsEarned()).append(" gold coins\n\n");
        
        report.append("BY QUEST TYPE:\n");
        for (QuestType type : QuestType.values()) {
            List<QuestResult> typeQuests = getQuestsByType(type);
            int successful = 0;
            for (QuestResult quest : typeQuests) {
                if (quest.isSuccess()) successful++;
            }
            report.append(type).append(": ").append(typeQuests.size())
                  .append(" (").append(successful).append(" successful)\n");
        }
        
        return report.toString();
    }
}