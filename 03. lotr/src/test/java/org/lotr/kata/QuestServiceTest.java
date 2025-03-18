package org.lotr.kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.lotr.kata.QuestService.InventoryManagerInterface;
import org.lotr.kata.QuestService.QuestDatabaseInterface;
import org.lotr.kata.QuestService.CharacterServiceInterface;

class QuestServiceTest {
    private InventoryManagerInterface inventoryManager;
    private QuestDatabaseInterface questDatabase;
    private CharacterServiceInterface characterService;
    private QuestService questService;
    
    @BeforeEach
    void setUp() {
        // Créer des implémentations de test plutôt que d'essayer de mocker les singletons
        inventoryManager = new TestInventoryManager();
        questDatabase = new TestQuestDatabase();
        characterService = new TestCharacterService();
        
        // Create the service with our test implementations
        questService = new QuestService(inventoryManager, questDatabase, characterService);
    }
    
    @Test
    void testStartQuestSuccess() {
        // Given
        String character = "Frodo";
        List<String> companions = Arrays.asList("Sam");
        MiddleEarthItem ring = new MiddleEarthItem("The One Ring", 1, 100, ItemOrigin.MORDOR);
        List<MiddleEarthItem> items = Arrays.asList(ring);
        
        // Add the ring to inventory for the test
        ((TestInventoryManager)inventoryManager).addTestItem(ring);
        
        // When
        QuestResult result = questService.startQuest(character, QuestType.DESTROY_RING, companions, items);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCharacterName()).isEqualTo(character);
        assertThat(result.getQuestType()).isEqualTo(QuestType.DESTROY_RING);
        assertThat(result.getCompanions()).containsExactly("Sam");
        // Can't reliably test success because it involves randomness
        // Would need to modify the code to allow deterministic testing
    }
    
    @Test
    void testStartQuestCharacterNotAvailable() {
        // Given
        String character = "Frodo";
        List<String> companions = Arrays.asList("Sam");
        MiddleEarthItem ring = new MiddleEarthItem("The One Ring", 1, 100, ItemOrigin.MORDOR);
        List<MiddleEarthItem> items = Arrays.asList(ring);
        
        // Make character unavailable
        ((TestCharacterService)characterService).setCharacterAvailable(character, false);
        
        // When/Then
        assertThatThrownBy(() -> 
            questService.startQuest(character, QuestType.DESTROY_RING, companions, items)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("not available for quests");
    }
    
    @Test
    void testStartQuestCompanionNotAvailable() {
        // Given
        String character = "Frodo";
        List<String> companions = Arrays.asList("Sam");
        MiddleEarthItem ring = new MiddleEarthItem("The One Ring", 1, 100, ItemOrigin.MORDOR);
        List<MiddleEarthItem> items = Arrays.asList(ring);
        
        // Make companion unavailable
        ((TestCharacterService)characterService).setCharacterAvailable("Sam", false);
        
        // When/Then
        assertThatThrownBy(() -> 
            questService.startQuest(character, QuestType.DESTROY_RING, companions, items)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("not available for quests");
    }
    
    @Test
    void testStartQuestItemNotAvailable() {
        // Given
        String character = "Frodo";
        List<String> companions = Arrays.asList("Sam");
        MiddleEarthItem ring = new MiddleEarthItem("The One Ring", 1, 100, ItemOrigin.MORDOR);
        List<MiddleEarthItem> items = Arrays.asList(ring);
        
        // Don't add the ring to inventory - it should be empty
        
        // When/Then
        assertThatThrownBy(() -> 
            questService.startQuest(character, QuestType.DESTROY_RING, companions, items)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("not available in sufficient quantity");
    }
    
    @Test
    void testStartDestroyRingQuestWithoutRing() {
        // Given
        String character = "Frodo";
        List<String> companions = Arrays.asList("Sam");
        MiddleEarthItem sword = new MiddleEarthItem("Sword", 1, 50, ItemOrigin.GONDOR); // Not a ring
        List<MiddleEarthItem> items = Arrays.asList(sword);
        
        // Add the sword to inventory
        ((TestInventoryManager)inventoryManager).addTestItem(sword);
        
        // When/Then
        assertThatThrownBy(() -> 
            questService.startQuest(character, QuestType.DESTROY_RING, companions, items)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Cannot start DESTROY_RING quest without a ring");
    }
    
    @Test
    void testStartDiplomaticMissionWithInexperiencedHobbit() {
        // Given
        String character = "Pippin"; // Inexperienced hobbit
        List<String> companions = Arrays.asList("Gandalf");
        MiddleEarthItem scroll = new MiddleEarthItem("Diplomatic Scroll", 1, 30, ItemOrigin.GONDOR);
        List<MiddleEarthItem> items = Arrays.asList(scroll);
        
        // Add scroll to inventory
        ((TestInventoryManager)inventoryManager).addTestItem(scroll);
        
        // Set character as hobbit and inexperienced
        ((TestCharacterService)characterService).setCharacterType(character, "Hobbit");
        ((TestCharacterService)characterService).setCharacterLevel(character, "Novice");
        
        // When/Then
        assertThatThrownBy(() -> 
            questService.startQuest(character, QuestType.DIPLOMATIC_MISSION, companions, items)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Inexperienced Hobbits cannot lead diplomatic missions");
    }
    
    // Test implementations for dependencies
    private static class TestInventoryManager implements InventoryManagerInterface {
        private List<MiddleEarthItem> items = new ArrayList<>();
        private boolean warTime = false;
        
        public void addTestItem(MiddleEarthItem item) {
            items.add(item);
        }
        
        @Override
        public List<MiddleEarthItem> getAllItems() {
            return items;
        }
        
        @Override
        public boolean isWarTime() {
            return warTime;
        }
        
        @Override
        public void setWarTime(boolean warTime) {
            this.warTime = warTime;
        }
        
        @Override
        public void removeItem(String name, int quantity) {
            // Simplified implementation for tests
            items.removeIf(item -> item.n.equals(name));
        }
        
        @Override
        public void addItem(MiddleEarthItem item) {
            items.add(item);
        }
        
        @Override
        public void updateAllItems() {
            // Simplified implementation for tests
            for (MiddleEarthItem item : items) {
                if (item.qual > 0) {
                    item.qual -= 1;
                }
            }
        }
        
        @Override
        public boolean hasRing() {
            // Simplified implementation for tests
            for (MiddleEarthItem item : items) {
                if (item.isR) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String getInventoryReport(boolean includeRings, boolean includeQualityDetails, String sortBy) {
            // Simplified implementation for tests
            return "Test Inventory Report";
        }
    }
    
    private static class TestQuestDatabase implements QuestDatabaseInterface {
        private String weather = "Clear";
        
        @Override
        public String getCurrentWeather() {
            return weather;
        }
        
        @Override
        public void setCurrentWeather(String weather) {
            this.weather = weather;
        }
        
        public void setWeather(String weather) {
            setCurrentWeather(weather);
        }
        
        @Override
        public void saveQuestResult(QuestResult result) {
            // Do nothing for tests
        }
        
        @Override
        public String generateQuestReport() {
            return "Test Quest Report";
        }
    }
    
    private static class TestCharacterService implements CharacterServiceInterface {
        private Map<String, Boolean> characterAvailability = new HashMap<>();
        private Map<String, String> characterTypes = new HashMap<>();
        private Map<String, String> characterLevels = new HashMap<>();
        
        public TestCharacterService() {
            // Default all characters to available
            characterAvailability.put("Frodo", true);
            characterAvailability.put("Sam", true);
            characterAvailability.put("Gandalf", true);
            characterAvailability.put("Pippin", true);
            
            // Default types
            characterTypes.put("Frodo", "Hobbit");
            characterTypes.put("Sam", "Hobbit");
            characterTypes.put("Gandalf", "Wizard");
            characterTypes.put("Pippin", "Hobbit");
            
            // Default levels
            characterLevels.put("Frodo", "Experienced");
            characterLevels.put("Sam", "Experienced");
            characterLevels.put("Gandalf", "Legendary");
            characterLevels.put("Pippin", "Experienced");
        }
        
        public void setCharacterAvailable(String name, boolean available) {
            characterAvailability.put(name, available);
        }
        
        public void setCharacterType(String name, String type) {
            characterTypes.put(name, type);
        }
        
        public void setCharacterLevel(String name, String level) {
            characterLevels.put(name, level);
        }
        
        @Override
        public boolean isCharacterAvailable(String name) {
            return characterAvailability.getOrDefault(name, false);
        }
        
        @Override
        public String getCharacterType(String name) {
            return characterTypes.getOrDefault(name, "Unknown");
        }
        
        @Override
        public String getCharacterLevel(String name) {
            return characterLevels.getOrDefault(name, "Novice");
        }
        
        @Override
        public void completeQuest(String name, boolean success) {
            // Do nothing for tests
        }
    }
}