package org.lotr.kata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager implements InventoryManagerInterface {
    private static InventoryManager instance;
    private List<MiddleEarthItem> items = new ArrayList<>();
    private Map<String, Integer> sales = new HashMap<>();
    private boolean isWarTime = false;

    private InventoryManager() {
        // Private constructor for singleton
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void addItem(MiddleEarthItem i) {
        if (i == null) return;

        // Check if we already have this item and just increase quantity
        for (MiddleEarthItem item : items) {
            if (item.n.equals(i.n) && item.o == i.o && item.qual == i.qual) {
                item.q += i.q;
                return;
            }
        }

        // Otherwise add new item
        this.items.add(i);
    }

    public void removeItem(String name, int quantity) {
        // Find item and remove quantity
        for (MiddleEarthItem item : items) {
            if (item.n.equals(name)) {
                item.q -= quantity;
                if (item.q <= 0) {
                    items.remove(item);
                }
                return;
            }
        }
    }

    public List<MiddleEarthItem> getItemsFromOrigin(ItemOrigin origin) {
        List<MiddleEarthItem> result = new ArrayList<>();

        for (MiddleEarthItem item : items) {
            if (item.o == origin) {
                result.add(item);
            }
        }

        return result;
    }

    public int getTotalInventoryValue() {
        int total = 0;

        for (MiddleEarthItem item : items) {
            total += item.p * item.q;
        }

        return total;
    }

    public int getTotalValueByOrigin(ItemOrigin origin) {
        int total = 0;

        for (MiddleEarthItem item : items) {
            if (item.o == origin) {
                total += item.p * item.q;
            }
        }

        return total;
    }

    public void sellItem(String name, int quantity, boolean isHaggling, String dayOfWeek) {
        // Find the item
        MiddleEarthItem itemToSell = null;
        for (MiddleEarthItem item : items) {
            if (item.n.equals(name) && item.q >= quantity) {
                itemToSell = item;
                break;
            }
        }

        if (itemToSell == null) return;

        // Calculate price
        int price = itemToSell.getFinalPrice(isHaggling, dayOfWeek);

        // Record sale
        if (sales.containsKey(name)) {
            sales.put(name, sales.get(name) + (price * quantity));
        } else {
            sales.put(name, price * quantity);
        }

        // Update inventory
        itemToSell.q -= quantity;
        if (itemToSell.q <= 0) {
            items.remove(itemToSell);
        }

        // Apply special wartime logic
        if (isWarTime && itemToSell.o == ItemOrigin.MORDOR) {
            // During wartime, selling items from Mordor is illegal
            throw new IllegalStateException("Cannot sell items from Mordor during wartime!");
        }
    }

    public void updateAllItems() {
        ArrayList<MiddleEarthItem> itemsToRemove = new ArrayList<>();

        for (MiddleEarthItem item : items) {
            item.updateQuality();

            // Remove completely degraded items
            if (item.qual <= 0 && !item.isE && !item.isGood && !item.isMithril) {
                itemsToRemove.add(item);
            }
        }

        // Remove degraded items
        for (MiddleEarthItem itemToRemove : itemsToRemove) {
            items.remove(itemToRemove);
        }
    }

    public boolean hasRing() {
        for (MiddleEarthItem item : items) {
            if (item.isRing) {
                return true;
            }
        }
        return false;
    }

    public boolean isWarTime() {
        return isWarTime;
    }

    public void setWarTime(boolean warTime) {
        this.isWarTime = warTime;

        // When war starts, gondor items get more valuable
        if (warTime) {
            for (MiddleEarthItem item : items) {
                if (item.o == ItemOrigin.GONDOR) {
                    item.p = (int) (item.p * 1.5);
                } else if (item.o == ItemOrigin.ROHAN) {
                    item.p = (int) (item.p * 1.3);
                } else if (item.o == ItemOrigin.MORDOR) {
                    if (!item.isRing) {
                        item.p = (int) (item.p * 0.5);
                    }
                }
            }
        } else {
            // Reset prices when war ends
            for (MiddleEarthItem item : items) {
                if (item.o == ItemOrigin.GONDOR) {
                    item.p = (int) (item.p / 1.5);
                } else if (item.o == ItemOrigin.ROHAN) {
                    item.p = (int) (item.p / 1.3);
                } else if (item.o == ItemOrigin.MORDOR) {
                    if (!item.isRing) {
                        item.p = (int) (item.p * 2);
                    }
                }
            }
        }
    }

    // For testing - get all items
    public List<MiddleEarthItem> getAllItems() {
        return new ArrayList<>(items);
    }

    // Generate a report with confusing logic
    public String getInventoryReport(boolean includeRings, boolean includeQualityDetails, String sortBy) {
        StringBuilder report = new StringBuilder();
        report.append("INVENTORY REPORT\n");
        report.append("----------------\n");

        List<MiddleEarthItem> sortedItems = new ArrayList<>(items);

        if (sortBy.equals("name")) {
            sortedItems.sort((a, b) -> a.n.compareTo(b.n));
        } else if (sortBy.equals("price")) {
            sortedItems.sort((a, b) -> Integer.compare(b.p, a.p));
        } else if (sortBy.equals("origin")) {
            sortedItems.sort((a, b) -> a.o.toString().compareTo(b.o.toString()));
        }

        for (MiddleEarthItem item : sortedItems) {
            if (!includeRings && item.isRing) continue;

            report.append(item.n).append(" (").append(item.o).append(") - Qty: ").append(item.q)
                    .append(", Price: ").append(item.p);

            if (includeQualityDetails) {
                report.append(", Quality: ").append(item.qual);
                if (item.isGood) report.append(" [Good]");
                if (item.isC) report.append(" [Cursed]");
                if (item.isMithril) report.append(" [Mithril]");
                if (item.isE) report.append(" [Magic Level: ").append(item.m).append("]");
            }

            report.append("\n");
        }

        report.append("\nTotal Value: ").append(getTotalInventoryValue()).append(" gold coins");

        return report.toString();
    }
}