package org.lotr.kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.lang.reflect.Field;

public class InventoryManagerTest {
    private InventoryManager inventoryManager;
    
    @BeforeEach
    void setUp() throws Exception {
        // Réinitialiser le singleton pour chaque test
        resetSingleton();
        inventoryManager = InventoryManager.getInstance();
    }
    
    // Méthode pour réinitialiser le singleton via reflection
    private void resetSingleton() throws Exception {
        Field instance = InventoryManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }
    
    @Test
    void testAddItem() {
        // Given
        MiddleEarthItem sword = new MiddleEarthItem("Sword", 1, 50, ItemOrigin.GONDOR);
        
        // When
        inventoryManager.addItem(sword);
        
        // Then
        List<MiddleEarthItem> allItems = inventoryManager.getAllItems();
        assertThat(allItems).hasSize(1);
        assertThat(allItems.get(0).n).isEqualTo("Sword");
    }
    
    @Test
    void testRemoveItem() {
        // Given
        MiddleEarthItem sword = new MiddleEarthItem("Sword", 1, 50, ItemOrigin.GONDOR);
        inventoryManager.addItem(sword);
        
        // When
        inventoryManager.removeItem("Sword", 1);
        
        // Then
        List<MiddleEarthItem> allItems = inventoryManager.getAllItems();
        assertThat(allItems).isEmpty();
    }
    
    @Test
    void testGetTotalInventoryValue() {
        // Given
        MiddleEarthItem sword = new MiddleEarthItem("Sword", 2, 50, ItemOrigin.GONDOR);
        MiddleEarthItem bow = new MiddleEarthItem("Bow", 1, 60, ItemOrigin.LOTHLORIEN);
        inventoryManager.addItem(sword);
        inventoryManager.addItem(bow);
        
        // When
        int totalValue = inventoryManager.getTotalInventoryValue();
        
        // Then
        // Le prix est calculé de façon complexe, donc ne comparons pas à une valeur fixe
        assertThat(totalValue).isPositive();
        
        // Mais on peut vérifier que le total est la somme des valeurs individuelles
        int swordValue = sword.p * sword.q;
        int bowValue = bow.p * bow.q;
        assertThat(totalValue).isEqualTo(swordValue + bowValue);
    }
    
    @Test
    void testSellItem() {
        // Given
        MiddleEarthItem sword = new MiddleEarthItem("Sword", 2, 50, ItemOrigin.GONDOR);
        inventoryManager.addItem(sword);
        
        // When
        inventoryManager.sellItem("Sword", 1, false, "Monday");
        
        // Then
        List<MiddleEarthItem> allItems = inventoryManager.getAllItems();
        assertThat(allItems).hasSize(1);
        assertThat(allItems.get(0).q).isEqualTo(1);
    }
    
    @Test
    void testSellItemDuringWartime() {
        // Given
        MiddleEarthItem mordorItem = new MiddleEarthItem("Orc Shield", 1, 30, ItemOrigin.MORDOR);
        inventoryManager.addItem(mordorItem);
        inventoryManager.setWarTime(true);
        
        // When/Then
        assertThatThrownBy(() -> 
            inventoryManager.sellItem("Orc Shield", 1, false, "Monday")
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Mordor during wartime");
    }
    
    @Test
    void testUpdateAllItems() {
        // Given
        MiddleEarthItem normalItem = new MiddleEarthItem("Sword", 1, 10, ItemOrigin.GONDOR);
        MiddleEarthItem cursedItem = new MiddleEarthItem("cursed Dagger", 1, 10, ItemOrigin.MORDOR);
        inventoryManager.addItem(normalItem);
        inventoryManager.addItem(cursedItem);
        
        // When
        inventoryManager.updateAllItems();
        
        // Then
        List<MiddleEarthItem> allItems = inventoryManager.getAllItems();
        
        // Trouver les objets mis à jour
        MiddleEarthItem updatedNormalItem = findItemByName(allItems, "Sword");
        MiddleEarthItem updatedCursedItem = findItemByName(allItems, "cursed Dagger");
        
        assertThat(updatedNormalItem).isNotNull();
        assertThat(updatedNormalItem.qual).isEqualTo(9); // Les objets normaux perdent 1 de qualité
        
        assertThat(updatedCursedItem).isNotNull();
        assertThat(updatedCursedItem.qual).isEqualTo(5); // Les objets maudits de Mordor perdent 5 de qualité au total (1 basique + 2 car maudit + 2 car Mordor)
    }
    
    // Méthode utilitaire pour trouver un objet par son nom
    private MiddleEarthItem findItemByName(List<MiddleEarthItem> items, String name) {
        return items.stream()
                    .filter(item -> item.n.equals(name))
                    .findFirst()
                    .orElse(null);
    }
}