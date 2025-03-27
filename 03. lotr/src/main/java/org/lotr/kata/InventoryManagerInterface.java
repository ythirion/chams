package org.lotr.kata;

import java.util.List;

public interface InventoryManagerInterface {
    List<MiddleEarthItem> getAllItems();
    
    boolean isWarTime();
    
    void removeItem(String name, int quantity);
    
    void addItem(MiddleEarthItem item);
    
    void updateAllItems();
    
    void setWarTime(boolean warTime);
    
    boolean hasRing();
    
    String getInventoryReport(boolean includeRings, boolean includeQualityDetails, String sortBy);
}