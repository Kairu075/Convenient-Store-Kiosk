package kiosk;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap; // Added for maintaining insertion order
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the shopping cart functionality for the kiosk application.
 * Stores items, quantities, and prices in a singleton instance.
 */
public class CartManager {
    
    // Singleton instance
    private static final CartManager instance = new CartManager();
    
    // Maps to store cart data
    private Map<String, Integer> itemQuantities;
    private Map<String, Double> itemPrices;
    
    // Discount management
    private static boolean discountApplied = false;
    private static final double DISCOUNT_RATE = 0.20; // 20% discount
    
    // Private constructor for singleton pattern
    private CartManager() {
        itemQuantities = new HashMap<>();
        itemPrices = new HashMap<>();
    }
    
    /**
     * Adds an item to the cart or increases its quantity if already present
     * 
     * @param itemName The name of the item
     * @param price The price of the item
     */
    public static void addItem(String itemName, double price) {
        // Get current quantity or default to 0 if not in cart
        int currentQty = instance.itemQuantities.getOrDefault(itemName, 0);
        
        // Add or update item
        instance.itemQuantities.put(itemName, currentQty + 1);
        instance.itemPrices.put(itemName, price);
    }
    
    /**
     * Removes one instance of an item from the cart
     * If quantity reaches 0, removes the item completely
     * 
     * @param itemName The name of the item to remove
     */
    public static void removeItem(String itemName) {
        if (!instance.itemQuantities.containsKey(itemName)) {
            return;
        }
        
        int currentQty = instance.itemQuantities.get(itemName);
        
        if (currentQty <= 1) {
            // Remove item completely
            instance.itemQuantities.remove(itemName);
            instance.itemPrices.remove(itemName);
        } else {
            // Decrease quantity
            instance.itemQuantities.put(itemName, currentQty - 1);
        }
    }
    
    /**
     * Gets the current quantity of an item in the cart
     * 
     * @param itemName The name of the item
     * @return The quantity of the item, or 0 if not in cart
     */
    public static int getItemQuantity(String itemName) {
        return instance.itemQuantities.getOrDefault(itemName, 0);
    }
    
    /**
     * Gets the price of an item
     * 
     * @param itemName The name of the item
     * @return The price of the item, or 0 if not in cart
     */
    public static double getItemPrice(String itemName) {
        return instance.itemPrices.getOrDefault(itemName, 0.0);
    }
    
    /**
     * Gets the total number of items in the cart
     * 
     * @return The total number of items (sum of all quantities)
     */
    public static int getTotalItems() {
        int total = 0;
        for (int qty : instance.itemQuantities.values()) {
            total += qty;
        }
        return total;
    }
    
    /**
     * Calculates the total price of all items in the cart
     * 
     * @return The total price
     */
    public static double getTotalPrice() {
        double total = 0.0;
        for (String item : instance.itemQuantities.keySet()) {
            int qty = instance.itemQuantities.get(item);
            double price = instance.itemPrices.get(item);
            total += qty * price;
        }
        return total;
    }
    
    /**
     * Gets all items in the cart with their quantities
     * 
     * @return A map of item names to quantities
     */
    public static Map<String, Integer> getAllItems() {
        return new HashMap<>(instance.itemQuantities);
    }
    
    /**
     * Checks if the cart is empty
     * 
     * @return true if the cart has no items, false otherwise
     */
    public static boolean isCartEmpty() {
        return instance.itemQuantities.isEmpty();
    }
    
    /**
     * Gets all items in the cart with their quantities, preserving order of insertion
     * 
     * @return An ordered map of item names to quantities
     */
    public static Map<String, Integer> getOrderedItems() {
        Map<String, Integer> orderedItems = new LinkedHashMap<>();
        // Copy items in a way that maintains insertion order
        for (String key : instance.itemQuantities.keySet()) {
            orderedItems.put(key, instance.itemQuantities.get(key));
        }
        return orderedItems;
    }
    
    /**
     * Gets all items in the cart with their quantities (alias for getAllItems)
     * 
     * @return A map of item names to quantities
     */
    public static Map<String, Integer> getCartItems() {
        return getAllItems();
    }
    
    /**
     * Gets all items in the cart with their prices
     * 
     * @return A map of item names to prices
     */
    public static Map<String, Double> getCartPrices() {
        return new HashMap<>(instance.itemPrices);
    }
    
    /**
     * Gets the subtotal for a specific item
     * 
     * @param itemName The name of the item
     * @return The subtotal (price * quantity)
     */
    public static double getItemSubtotal(String itemName) {
        if (!instance.itemQuantities.containsKey(itemName)) {
            return 0.0;
        }
        return instance.itemQuantities.get(itemName) * instance.itemPrices.get(itemName);
    }
    
    /**
     * Saves the current cart as a receipt
     * 
     * @return A unique receipt ID
     */
    public static String saveReceipt() {
        if (getTotalItems() == 0) {
            return null; // Don't save empty receipts
        }
        
        // Generate receipt ID using timestamp
        LocalDateTime now = LocalDateTime.now();
        String receiptId = "RCP" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // In a real application, you'd persist this data to a database or file
        // For now, we'll just return the ID
        return receiptId;
    }
    
    /**
     * Updates the quantity of an item directly
     * 
     * @param itemName The name of the item
     * @param quantity The new quantity (removes item if 0)
     */
    public static void updateItemQuantity(String itemName, int quantity) {
        if (quantity <= 0) {
            instance.itemQuantities.remove(itemName);
            instance.itemPrices.remove(itemName);
        } else if (instance.itemPrices.containsKey(itemName)) {
            instance.itemQuantities.put(itemName, quantity);
        }
    }
    
    /**
     * Applies or removes discount from the cart
     * 
     * @param apply true to apply discount, false to remove it
     */
    public static void applyDiscount(boolean apply) {
        discountApplied = apply;
    }
    
    /**
     * Checks if discount is currently applied
     * 
     * @return true if discount is applied, false otherwise
     */
    public static boolean isDiscountApplied() {
        return discountApplied;
    }
    
    /**
     * Gets the discount amount based on cart total
     * 
     * @return The amount of discount
     */
    public static double getDiscountAmount() {
        if (!discountApplied) {
            return 0.0;
        }
        double subtotal = getTotalPrice();
        return subtotal * DISCOUNT_RATE;
    }
    
    /**
     * Gets the total price after discount
     * 
     * @return The final total price
     */
    public static double getTotal() {
        double subtotal = getTotalPrice();
        double discount = getDiscountAmount();
        return subtotal - discount;
    }
    
    /**
     * Generates a formatted receipt as a string
     * 
     * @return Formatted receipt text
     */
    public static String getFormattedReceipt() {
        StringBuilder receipt = new StringBuilder();
        
        // Header
        receipt.append("==================================\n");
        receipt.append("           RECEIPT\n");
        receipt.append("==================================\n\n");
        
        // Current date and time
        LocalDateTime now = LocalDateTime.now();
        receipt.append("Date: ").append(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        // Items
        receipt.append("Items:\n");
        receipt.append("----------------------------------\n");
        
        for (String item : instance.itemQuantities.keySet()) {
            int qty = instance.itemQuantities.get(item);
            double price = instance.itemPrices.get(item);
            double subtotal = price * qty;
            
            receipt.append(String.format("%-25s %2d x ₱%6.2f = ₱%7.2f\n", 
                                       item, qty, price, subtotal));
        }
        
        receipt.append("----------------------------------\n\n");
        
        // Summary
        double subtotal = getTotalPrice();
        receipt.append(String.format("Subtotal:                      ₱%7.2f\n", subtotal));
        
        if (discountApplied) {
            double discount = getDiscountAmount();
            receipt.append(String.format("Discount (20%%):                ₱%7.2f\n", discount));
        }
        
        double total = getTotal();
        receipt.append(String.format("TOTAL:                         ₱%7.2f\n\n", total));
        
        // Footer
        receipt.append("==================================\n");
        receipt.append("        Thank you for shopping!\n");
        receipt.append("==================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Clears all items from the cart
     */
    public static void clearCart() {
        instance.itemQuantities.clear();
        instance.itemPrices.clear();
        discountApplied = false;
    }
}
