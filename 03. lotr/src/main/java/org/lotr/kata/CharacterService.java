package org.lotr.kata;

import java.util.HashMap;
import java.util.Map;

public class CharacterService implements CharacterServiceInterface {
    private static CharacterService instance;
    private Map<String, Character> characters = new HashMap<>();
    
    private CharacterService() {
        // Initialize with some default characters
        characters.put("Frodo", new Character("Frodo", "Hobbit", "Experienced", true));
        characters.put("Gandalf", new Character("Gandalf", "Wizard", "Legendary", true));
        characters.put("Aragorn", new Character("Aragorn", "Human", "Veteran", true));
        characters.put("Legolas", new Character("Legolas", "Elf", "Veteran", true));
        characters.put("Gimli", new Character("Gimli", "Dwarf", "Veteran", true));
        characters.put("Boromir", new Character("Boromir", "Human", "Experienced", true));
        characters.put("Sam", new Character("Sam", "Hobbit", "Novice", true));
        characters.put("Merry", new Character("Merry", "Hobbit", "Novice", true));
        characters.put("Pippin", new Character("Pippin", "Hobbit", "Novice", true));
    }
    
    public static CharacterService getInstance() {
        if (instance == null) {
            instance = new CharacterService();
        }
        return instance;
    }
    
    public void addCharacter(String name, String type, String level) {
        characters.put(name, new Character(name, type, level, true));
    }
    
    public boolean isCharacterAvailable(String name) {
        return characters.containsKey(name) && characters.get(name).isAvailable();
    }
    
    public String getCharacterType(String name) {
        if (!characters.containsKey(name)) {
            throw new IllegalArgumentException("Character not found: " + name);
        }
        return characters.get(name).getType();
    }
    
    public String getCharacterLevel(String name) {
        if (!characters.containsKey(name)) {
            throw new IllegalArgumentException("Character not found: " + name);
        }
        return characters.get(name).getLevel();
    }
    
    public void setCharacterAvailability(String name, boolean available) {
        if (!characters.containsKey(name)) {
            throw new IllegalArgumentException("Character not found: " + name);
        }
        characters.get(name).setAvailable(available);
    }
    
    public void promoteCharacter(String name) {
        if (!characters.containsKey(name)) {
            throw new IllegalArgumentException("Character not found: " + name);
        }
        
        Character character = characters.get(name);
        String currentLevel = character.getLevel();
        
        if (currentLevel.equals("Novice")) {
            character.setLevel("Experienced");
        } else if (currentLevel.equals("Experienced")) {
            character.setLevel("Veteran");
        } else if (currentLevel.equals("Veteran")) {
            character.setLevel("Legendary");
        }
        // Legendary is the highest level, no promotion possible
    }
    
    public void completeQuest(String name, boolean success) {
        if (!characters.containsKey(name)) {
            throw new IllegalArgumentException("Character not found: " + name);
        }
        
        // 25% chance to promote a character if the quest was successful
        if (success && Math.random() < 0.25) {
            promoteCharacter(name);
        }
        
        // Update quest count
        Character character = characters.get(name);
        if (success) {
            character.setSuccessfulQuests(character.getSuccessfulQuests() + 1);
        } else {
            character.setFailedQuests(character.getFailedQuests() + 1);
        }
    }
    
    private static class Character {
        private String name;
        private String type;
        private String level;
        private boolean available;
        private int successfulQuests;
        private int failedQuests;
        
        public Character(String name, String type, String level, boolean available) {
            this.name = name;
            this.type = type;
            this.level = level;
            this.available = available;
            this.successfulQuests = 0;
            this.failedQuests = 0;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public String getLevel() {
            return level;
        }
        
        public void setLevel(String level) {
            this.level = level;
        }
        
        public boolean isAvailable() {
            return available;
        }
        
        public void setAvailable(boolean available) {
            this.available = available;
        }
        
        public int getSuccessfulQuests() {
            return successfulQuests;
        }
        
        public void setSuccessfulQuests(int successfulQuests) {
            this.successfulQuests = successfulQuests;
        }
        
        public int getFailedQuests() {
            return failedQuests;
        }
        
        public void setFailedQuests(int failedQuests) {
            this.failedQuests = failedQuests;
        }
    }
}