package org.lotr.kata;

public interface QuestDatabaseInterface {
        String getCurrentWeather();
        void saveQuestResult(QuestResult result);
        void setCurrentWeather(String weather);
        String generateQuestReport();
    }