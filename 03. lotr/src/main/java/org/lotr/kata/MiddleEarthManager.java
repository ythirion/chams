package org.lotr.kata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.lotr.kata.QuestService.InventoryManagerInterface;
import org.lotr.kata.QuestService.QuestDatabaseInterface;
import org.lotr.kata.QuestService.CharacterServiceInterface;

public class MiddleEarthManager {
    private static MiddleEarthManager instance;
    private InventoryManagerInterface im;
    private QuestService qs;
    private CharacterServiceInterface cs;
    private QuestDatabaseInterface qd;
    private boolean ringDestroyed = false;
    private Map<String, Integer> characterGold = new HashMap<>();
    
    private MiddleEarthManager() {
        im = InventoryManager.getInstance();
        qd = QuestDatabase.getInstance();
        cs = CharacterService.getInstance();
        qs = new QuestService(im, qd, cs);
        
        // Initialize starting gold for characters
        for (String chr : new String[] {"Frodo", "Gandalf", "Aragorn", "Legolas", "Gimli", "Boromir", "Sam", "Merry", "Pippin"}) {
            characterGold.put(chr, 100); // Starting gold
        }
    }
    
    public static MiddleEarthManager getInstance() {
        if (instance == null) {
            instance = new MiddleEarthManager();
        }
        return instance;
    }
    
    public void addItemToInventory(String name, int qty, int qual, ItemOrigin orig) {
        MiddleEarthItem item = new MiddleEarthItem(name, qty, qual, orig);
        im.addItem(item);
    }
    
    public boolean attemptQuestWithCharacter(String charName, QuestType qt, List<String> compNames, List<String> itemNames) {
        if (!cs.isCharacterAvailable(charName)) return false;
        for (String c : compNames) if (!cs.isCharacterAvailable(c)) return false;
        
        List<MiddleEarthItem> items = new ArrayList<>();
        for (String itemName : itemNames) {
            boolean found = false;
            for (MiddleEarthItem item : im.getAllItems()) {
                if (item.n.equals(itemName)) {
                    items.add(item);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        
        try {
            QuestResult result = qs.startQuest(charName, qt, compNames, items);
            if (result.isSuccess()) {
                // Distribute rewards
                int share = result.getRewardAmount() / (compNames.size() + 1);
                characterGold.put(charName, characterGold.getOrDefault(charName, 0) + share);
                for (String comp : compNames) {
                    characterGold.put(comp, characterGold.getOrDefault(comp, 0) + share);
                }
                
                // Special handling for ring destruction
                if (qt == QuestType.DESTROY_RING && result.isSuccess()) {
                    this.ringDestroyed = true;
                    System.out.println("The One Ring has been destroyed! Middle Earth is saved!");
                }
                
                return true;
            } else {
                System.out.println("Quest failed!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during quest: " + e.getMessage());
            return false;
        }
    }
    
    public void processMiddleEarthDay() {
        // Update all items
        im.updateAllItems();
        
        // Random weather changes
        String[] weatherTypes = {"Clear", "Rainy", "Stormy", "Foggy"};
        qd.setCurrentWeather(weatherTypes[(int)(Math.random() * weatherTypes.length)]);
        
        // Chance of war
        if (Math.random() < 0.1) {
            im.setWarTime(!im.isWarTime());
            if (im.isWarTime()) {
                System.out.println("War has broken out in Middle Earth!");
            } else {
                System.out.println("Peace has returned to Middle Earth.");
            }
        }
    }
    
    public String getStateOfMiddleEarth() {
        StringBuilder sb = new StringBuilder();
        sb.append("STATE OF MIDDLE EARTH\n");
        sb.append("====================\n\n");
        
        sb.append("Current Weather: ").append(qd.getCurrentWeather()).append("\n");
        sb.append("War Status: ").append(im.isWarTime() ? "AT WAR" : "AT PEACE").append("\n");
        sb.append("One Ring Status: ").append(ringDestroyed ? "DESTROYED" : (im.hasRing() ? "IN INVENTORY" : "MISSING")).append("\n\n");
        
        sb.append("INVENTORY SUMMARY:\n");
        sb.append(im.getInventoryReport(true, true, "price").substring(0, im.getInventoryReport(true, true, "price").indexOf("INVENTORY REPORT") + 16));
        
        sb.append("\n\nQUEST SUMMARY:\n");
        sb.append(qd.generateQuestReport().substring(0, qd.generateQuestReport().indexOf("QUEST REPORT") + 12));
        
        sb.append("\n\nCHARACTER GOLD:\n");
        for (Map.Entry<String, Integer> entry : characterGold.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" gold\n");
        }
        
        return sb.toString();
    }
    
    public boolean buyItemForCharacter(String charName, String itemName, boolean haggle) {
        if (!characterGold.containsKey(charName)) return false;
        
        MiddleEarthItem itemToBuy = null;
        for (MiddleEarthItem item : im.getAllItems()) {
            if (item.n.equals(itemName)) {
                itemToBuy = item;
                break;
            }
        }
        
        if (itemToBuy == null) return false;
        
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String today = daysOfWeek[(int)(Math.random() * daysOfWeek.length)];
        
        int price = itemToBuy.getFinalPrice(haggle, today);
        
        if (characterGold.get(charName) >= price) {
            characterGold.put(charName, characterGold.get(charName) - price);
            im.removeItem(itemName, 1);
            System.out.println(charName + " bought " + itemName + " for " + price + " gold.");
            return true;
        } else {
            System.out.println(charName + " cannot afford " + itemName + " (costs " + price + " gold).");
            return false;
        }
    }
    
    public void tradeBetweenCharacters(String giver, String receiver, String itemName, int gold) {
        if (!characterGold.containsKey(giver) || !characterGold.containsKey(receiver)) return;
        if (characterGold.get(receiver) < gold) return;
        
        MiddleEarthItem itemToTrade = null;
        for (MiddleEarthItem item : im.getAllItems()) {
            if (item.n.equals(itemName)) {
                itemToTrade = item;
                break;
            }
        }
        
        if (itemToTrade == null) return;
        
        // Update gold
        characterGold.put(giver, characterGold.get(giver) + gold);
        characterGold.put(receiver, characterGold.get(receiver) - gold);
        
        // Item is removed and readded to simulate transfer
        im.removeItem(itemName, 1);
        System.out.println(giver + " gave " + itemName + " to " + receiver + " for " + gold + " gold.");
    }
    
    // Helper method to initialize game with ring and items
    public void initializeTestScenario() {
        // Add starting items
        addItemToInventory("The One Ring", 1, 100, ItemOrigin.MORDOR);
        addItemToInventory("Mithril Shirt", 2, 90, ItemOrigin.MORIA);
        addItemToInventory("Gondorian Sword", 5, 85, ItemOrigin.GONDOR);
        addItemToInventory("Elven Bow", 3, 88, ItemOrigin.LOTHLORIEN);
        addItemToInventory("Orc Blade", 10, 40, ItemOrigin.MORDOR);
        addItemToInventory("Wizard Staff", 1, 95, ItemOrigin.ISENGARD);
        addItemToInventory("Pipe Weed", 20, 30, ItemOrigin.SHIRE);
        addItemToInventory("Cursed Helmet", 1, 70, ItemOrigin.MORDOR);
        addItemToInventory("Dwarven Axe", 2, 82, ItemOrigin.EREBOR);
    }
}