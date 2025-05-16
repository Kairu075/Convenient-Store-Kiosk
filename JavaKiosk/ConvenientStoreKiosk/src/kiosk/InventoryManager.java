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
        
        // Personal Care - Bath & Body
        addItem(new InventoryItem("Soap", "Personal Care", 50.0, 30, today.plusMonths(12), "PC001", "Safeguard"));
        addItem(new InventoryItem("Shower Gel", "Personal Care", 120.0, 20, today.plusMonths(12), "PC002", "Dove"));
        addItem(new InventoryItem("Body Lotion", "Personal Care", 200.0, 15, today.plusMonths(12), "PC003", "Nivea"));
        addItem(new InventoryItem("Body Scrub", "Personal Care", 150.0, 10, today.plusMonths(12), "PC004", "St. Ives"));
        addItem(new InventoryItem("Bath Salts", "Personal Care", 180.0, 8, today.plusMonths(12), "PC005", "Epsom"));
        
        // Personal Care - Hair Care
        addItem(new InventoryItem("Shampoo", "Personal Care", 100.0, 25, today.plusMonths(12), "PC006", "Pantene"));
        addItem(new InventoryItem("Conditioner", "Personal Care", 150.0, 20, today.plusMonths(12), "PC007", "Palmolive"));
        addItem(new InventoryItem("Hair Oil", "Personal Care", 200.0, 12, today.plusMonths(12), "PC008", "Cream Silk"));
        addItem(new InventoryItem("Hair Mask", "Personal Care", 250.0, 10, today.plusMonths(12), "PC009", "Dove"));
        addItem(new InventoryItem("Hair Spray", "Personal Care", 300.0, 8, today.plusMonths(12), "PC010", "TRESemmé"));
        
        // Personal Care - Oral Care
        addItem(new InventoryItem("Toothpaste", "Personal Care", 80.0, 30, today.plusMonths(12), "PC011", "Colgate"));
        addItem(new InventoryItem("Mouthwash", "Personal Care", 150.0, 20, today.plusMonths(12), "PC012", "Listerine"));
        addItem(new InventoryItem("Dental Floss", "Personal Care", 100.0, 15, today.plusMonths(12), "PC013", "Oral-B"));
        addItem(new InventoryItem("Toothbrush", "Personal Care", 50.0, 25, today.plusMonths(12), "PC014", "Colgate"));
        addItem(new InventoryItem("Whitening Strips", "Personal Care", 200.0, 10, today.plusMonths(12), "PC015", "Crest"));
        
        // Personal Care - Skin Care
        addItem(new InventoryItem("Face Wash", "Personal Care", 120.0, 20, today.plusMonths(12), "PC016", "Cetaphil"));
        addItem(new InventoryItem("Moisturizer", "Personal Care", 250.0, 15, today.plusMonths(12), "PC017", "Neutrogena"));
        addItem(new InventoryItem("Sunscreen", "Personal Care", 300.0, 12, today.plusMonths(12), "PC018", "Biore"));
        addItem(new InventoryItem("Face Mask", "Personal Care", 200.0, 15, today.plusMonths(12), "PC019", "The Face Shop"));
        addItem(new InventoryItem("Serum", "Personal Care", 400.0, 10, today.plusMonths(12), "PC020", "The Ordinary"));
        
        // Household - Cleaning Supplies
        addItem(new InventoryItem("Zonrox Bleach", "Household", 65.0, 30, today.plusMonths(12), "HS001", "Zonrox"));
        addItem(new InventoryItem("Mr. Clean", "Household", 120.0, 20, today.plusMonths(12), "HS002", "Mr. Clean"));
        addItem(new InventoryItem("Lysol Disinfectant", "Household", 180.0, 25, today.plusMonths(12), "HS003", "Lysol"));
        addItem(new InventoryItem("Joy Dishwashing Liquid", "Household", 45.0, 40, today.plusMonths(12), "HS004", "Joy"));
        addItem(new InventoryItem("Domex Toilet Cleaner", "Household", 75.0, 30, today.plusMonths(12), "HS005", "Domex"));
        
        // Household - Laundry Essentials
        addItem(new InventoryItem("Ariel Detergent", "Household", 120.0, 30, today.plusMonths(12), "HS006", "Ariel"));
        addItem(new InventoryItem("Tide Detergent", "Household", 130.0, 25, today.plusMonths(12), "HS007", "Tide"));
        addItem(new InventoryItem("Downy Fabric Conditioner", "Household", 95.0, 35, today.plusMonths(12), "HS008", "Downy"));
        addItem(new InventoryItem("Surf Powder", "Household", 110.0, 40, today.plusMonths(12), "HS009", "Surf"));
        addItem(new InventoryItem("Champion Detergent", "Household", 95.0, 30, today.plusMonths(12), "HS010", "Champion"));
        
        // Tobacco - Cigarettes
        addItem(new InventoryItem("Marlboro Red", "Tobacco", 215.0, 50, today.plusMonths(6), "TB001", "Philip Morris"));
        addItem(new InventoryItem("Marlboro Gold", "Tobacco", 215.0, 45, today.plusMonths(6), "TB002", "Philip Morris"));
        addItem(new InventoryItem("Marlboro Ice Blast", "Tobacco", 220.0, 40, today.plusMonths(6), "TB003", "Philip Morris"));
        addItem(new InventoryItem("Winston Red", "Tobacco", 180.0, 35, today.plusMonths(6), "TB004", "JTI"));
        addItem(new InventoryItem("Winston Blue", "Tobacco", 180.0, 35, today.plusMonths(6), "TB005", "JTI"));
        
        // Beer & Alcohol
        addItem(new InventoryItem("San Miguel Pale Pilsen", "Alcohol", 59.0, 60, today.plusMonths(12), "AL001", "San Miguel"));
        addItem(new InventoryItem("Red Horse", "Alcohol", 69.0, 60, today.plusMonths(12), "AL002", "San Miguel"));
        addItem(new InventoryItem("Heineken", "Alcohol", 89.0, 40, today.plusMonths(12), "AL003", "Heineken"));
        addItem(new InventoryItem("Corona", "Alcohol", 95.0, 40, today.plusMonths(12), "AL004", "Cervecería Modelo"));
        addItem(new InventoryItem("Smirnoff Mule", "Alcohol", 75.0, 30, today.plusMonths(12), "AL005", "Diageo"));
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

    public InventoryItem getItemByName(String itemName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemByName'");
    }
}
