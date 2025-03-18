package org.lotr.kata;

import java.util.Arrays;
import java.util.List;

public class LordOfTheRingsApp {
    public static void main(String[] args) {
        // Initialize the application
        MiddleEarthManager manager = MiddleEarthManager.getInstance();
        manager.initializeTestScenario();
        
        // Process a day in Middle Earth
        manager.processMiddleEarthDay();
        
        // Print the current state
        System.out.println(manager.getStateOfMiddleEarth());
        
        // Run a test quest
        List<String> companions = Arrays.asList("Sam", "Merry", "Pippin");
        List<String> items = Arrays.asList("The One Ring", "Elven Bow");
        boolean questSuccess = manager.attemptQuestWithCharacter("Frodo", QuestType.DESTROY_RING, companions, items);
        
        System.out.println("\nQuest to destroy the ring was " + (questSuccess ? "successful!" : "a failure!"));
        
        // Process another day
        manager.processMiddleEarthDay();
        
        // Buy an item
        manager.buyItemForCharacter("Gandalf", "Dwarven Axe", true);
        
        // Final state
        System.out.println("\nFinal state:");
        System.out.println(manager.getStateOfMiddleEarth());
    }
}