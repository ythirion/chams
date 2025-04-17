package org.lotr.kata;

import java.util.ArrayList;
import java.util.List;

public class MiddleEarthItems extends ArrayList<MiddleEarthItem> {
    public MiddleEarthItems(List<MiddleEarthItem> items) {
        super(items);
    }

    public static MiddleEarthItems from(InventoryManagerInterface inventoryManager, List<MiddleEarthItem> items) {
        verifyItemsAreInventory(inventoryManager, items);
        return new MiddleEarthItems(items);
    }

    private static void verifyItemsAreInventory(InventoryManagerInterface inventoryManager, List<MiddleEarthItem> items) {
        var inventoryItems = inventoryManager.getAllItems();
        if (items.stream().anyMatch(item -> !inventoryItems.contains(item))) {
            throw new IllegalStateException("Item is not available in sufficient quantity.");
        }
    }

    public boolean containsRing() {
        return this.stream().anyMatch(item -> item.isRing);
    }

    void removeConsumedItems(InventoryManagerInterface inventoryManager, QuestType questType) {
        this.stream().filter(item -> (questType == QuestType.DESTROY_RING && item.isRing) || Math.random() < 0.2)
                .forEach(item -> inventoryManager.removeItem(item.n, item.q));
    }
}
