package kiosk;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static Map<String, Integer> cartItems = new HashMap<>();
    private static Map<String, Double> cartPrices = new HashMap<>();
    private static boolean isDiscountApplied = false;

    // Add one item at a time
    public static void addItem(String item, double price) {
        cartItems.put(item, cartItems.getOrDefault(item, 0) + 1);
        cartPrices.put(item, price);
    }

    // Add multiple quantity at once
    public static void addItem(String item, double price, int quantity) {
        cartItems.put(item, cartItems.getOrDefault(item, 0) + quantity);
        cartPrices.put(item, price);
    }

    // Remove one quantity or remove completely if quantity is 1
    public static void removeItem(String item) {
        if (cartItems.containsKey(item)) {
            int quantity = cartItems.get(item);
            if (quantity > 1) {
                cartItems.put(item, quantity - 1);
            } else {
                cartItems.remove(item);
                cartPrices.remove(item);
            }
        }
    }

    // cart items
    public static Map<String, Integer> getCartItems() {
        return cartItems;
    }

    // cart prices
    public static Map<String, Double> getCartPrices() {
        return cartPrices;
    }

    // total price
    public static double getTotal() {
        double total = 0;
        for (String item : cartItems.keySet()) {
            total += cartItems.get(item) * cartPrices.get(item);
        }
        if (isDiscountApplied) {
            total *= 0.8; // 20% discount
        }
        return total;
    }

    // Apply or remove discount
    public static void applyDiscount(boolean apply) {
        isDiscountApplied = apply;
    }

    // Clear cart after checkout
    public static void clearCart() {
        cartItems.clear();
        cartPrices.clear();
        isDiscountApplied = false;
    }
}
