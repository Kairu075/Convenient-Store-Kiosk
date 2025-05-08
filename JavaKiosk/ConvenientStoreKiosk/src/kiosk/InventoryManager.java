package kiosk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the inventory data for the store
 */
public class InventoryManager {
    private static final InventoryManager instance = new InventoryManager();
    private List<InventoryItem> inventoryItems;
    
    private InventoryManager() {
        inventoryItems = new ArrayList<>();
        
        // Add sample inventory data
        initializeSampleInventory();
    }
    
    public static InventoryManager getInstance() {
        return instance;
    }
    
    private void initializeSampleInventory() {
        // Add sample data for existing products
        LocalDate today = LocalDate.now();
        
        // Snacks
        addItem(new InventoryItem("Piattos", "Snacks", 35.1, 45, today.plusMonths(6), "SN001", "Jack n Jill"));
        addItem(new InventoryItem("Chippy", "Snacks", 25.0, 60, today.plusMonths(8), "SN002", "Jack n Jill"));
        addItem(new InventoryItem("V-Cut", "Snacks", 35.0, 55, today.plusMonths(9), "SN003", "Jack n Jill"));
        addItem(new InventoryItem("Nova", "Snacks", 32.0, 40, today.plusDays(5), "SN004", "Jack n Jill"));
        addItem(new InventoryItem("Tortillos", "Snacks", 38.2, 25, today.plusMonths(4), "SN005", "Jack n Jill"));
        
        // Ready to Eat Meals
        addItem(new InventoryItem("Hotdog Sandwich", "Ready to Eat Meals", 35.0, 15, today.plusDays(2), "RM001", "7-Eleven"));
        addItem(new InventoryItem("Siopao", "Ready to Eat Meals", 120.0, 12, today.plusDays(1), "RM002", "7-Eleven"));
        addItem(new InventoryItem("Cheese Burger", "Ready to Eat Meals", 85.0, 8, today, "RM003", "Ministop"));
        
        // Beverages
        addItem(new InventoryItem("Bottled Water", "Beverages", 20.0, 100, today.plusYears(1), "BV001", "Nestle"));
        addItem(new InventoryItem("Soft Drinks", "Beverages", 30.0, 80, today.plusMonths(6), "BV002", "Coca-Cola"));
        addItem(new InventoryItem("Juices", "Beverages", 40.0, 50, today.plusMonths(3), "BV003", "Minute Maid"));
        
        // Frozen Foods
        addItem(new InventoryItem("Selecta Double Dutch", "Frozen Foods", 98.0, 20, today.plusMonths(2), "FF001", "Selecta"));
        addItem(new InventoryItem("Cornetto Classic", "Frozen Foods", 35.0, 25, today.minusDays(1), "FF002", "Cornetto"));
    }
    
    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(inventoryItems);
    }
    
    public void addItem(InventoryItem item) {
        // Check if item with same name already exists
        for (int i = 0; i < inventoryItems.size(); i++) {
            if (inventoryItems.get(i).getName().equals(item.getName())) {
                // Update existing item
                inventoryItems.set(i, item);
                return;
            }
        }
        // Add new item
        inventoryItems.add(item);
    }
    
    public boolean removeItem(String itemName) {
        return inventoryItems.removeIf(item -> item.getName().equals(itemName));
    }
    
    public InventoryItem getItem(String itemName) {
        for (InventoryItem item : inventoryItems) {
            if (item.getName().equals(itemName)) {
                return new InventoryItem(item); // Return a copy
            }
        }
        return null;
    }
    
    public void updateItem(InventoryItem updatedItem) {
        for (int i = 0; i < inventoryItems.size(); i++) {
            if (inventoryItems.get(i).getName().equals(updatedItem.getName())) {
                inventoryItems.set(i, updatedItem);
                return;
            }
        }
    }
    
    public List<InventoryItem> getExpiredItems() {
        return inventoryItems.stream()
                .filter(InventoryItem::isExpired)
                .collect(Collectors.toList());
    }
    
    public List<InventoryItem> getExpiringItems(int daysWarning) {
        return inventoryItems.stream()
                .filter(item -> item.isExpiringSoon(daysWarning))
                .collect(Collectors.toList());
    }
    
    public List<InventoryItem> getLowStockItems(int threshold) {
        return inventoryItems.stream()
                .filter(item -> item.isLowStock(threshold))
                .collect(Collectors.toList());
    }
    
    public List<InventoryItem> getItemsByCategory(String category) {
        return inventoryItems.stream()
                .filter(item -> item.getCategory().equals(category))
                .collect(Collectors.toList());
    }
    
    public Map<String, Integer> getStockByCategory() {
        Map<String, Integer> stockByCategory = new HashMap<>();
        
        for (InventoryItem item : inventoryItems) {
            String category = item.getCategory();
            int currentStock = stockByCategory.getOrDefault(category, 0);
            stockByCategory.put(category, currentStock + item.getStockQuantity());
        }
        
        return stockByCategory;
    }
}
