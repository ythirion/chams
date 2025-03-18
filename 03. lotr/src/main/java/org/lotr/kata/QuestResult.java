package org.lotr.kata;

import java.util.List;

public class QuestResult {
    private String characterName;
    private QuestType questType;
    private boolean success;
    private int rewardAmount;
    private List<String> companions;
    
    public QuestResult(String characterName, QuestType questType, boolean success, int rewardAmount, List<String> companions) {
        this.characterName = characterName;
        this.questType = questType;
        this.success = success;
        this.rewardAmount = rewardAmount;
        this.companions = companions;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public QuestType getQuestType() {
        return questType;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public int getRewardAmount() {
        return rewardAmount;
    }
    
    public List<String> getCompanions() {
        return companions;
    }
}