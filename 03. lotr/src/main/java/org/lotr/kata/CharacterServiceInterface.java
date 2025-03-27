package org.lotr.kata;

public interface CharacterServiceInterface {
        boolean isCharacterAvailable(String name);
        String getCharacterType(String name);
        String getCharacterLevel(String name);
        void completeQuest(String name, boolean success);
    }