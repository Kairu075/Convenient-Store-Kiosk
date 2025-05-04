package kiosk;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CartPage extends JFrame {
    private final Map<String, String> imageFileMap = new HashMap<>();

    public CartPage() {
        setTitle("Cart");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        
        imageFileMap.put("Chippy", "chippy.jpg");
        imageFileMap.put("Chips Ahoy", "chipsahoy.jpg");
        imageFileMap.put("Chips Delight", "chipsdelight.jpg");
        imageFileMap.put("Choco Mallows", "chocomallows.jpg");
        imageFileMap.put("Cupp Keyk Topps Sarap", "cuppkeyktoppssarap.jpg");
        imageFileMap.put("Doowee Donut Chocolate", "dooweedonutchocolate.jpg");
        imageFileMap.put("Foods", "foods.png");
        imageFileMap.put("Fudgee Barr", "fudgeebarr.jpg");
        imageFileMap.put("Nova", "nova.jpg");
        imageFileMap.put("Piattos", "piattos.jpg");
        imageFileMap.put("Rebisco Choco Crackers", "rebiscochococrackers.jpg");
        imageFileMap.put("Roller Coaster", "rollercoaster.jpg");
        imageFileMap.put("Tortillos", "tortillos.jpg");
        imageFileMap.put("Alcohol", "alcohol.png");
        imageFileMap.put("Household", "household.png");
        imageFileMap.put("Personal Care", "personal_care.png");
        imageFileMap.put("V-Cut", "vcut.jpg");
        imageFileMap.put("Cream-O", "creamo.jpg");
        imageFileMap.put("Stik-O Chocolate", "stikochocolate.jpg");

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        Map<String, Integer> cartItems = CartManager.getCartItems();
        Map<String, Double> cartPrices = CartManager.getCartPrices();

        for (String item : cartItems.keySet()) {
            JPanel itemPanel = new JPanel(new GridBagLayout());
            itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            itemPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            
            JLabel imageLabel = new JLabel();
            String fileName = imageFileMap.get(item);
            if (fileName != null) {
                URL imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + fileName);
                if (imageUrl != null) {
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    imageLabel.setText("No Image");
                }
            } else {
                imageLabel.setText("No Image");
            }

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            itemPanel.add(imageLabel, gbc);

            // Item name
            JLabel nameLabel = new JLabel(item);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridheight = 1;
            itemPanel.add(nameLabel, gbc);

            // Quantity controls
            JPanel controlsPanel = new JPanel();
            controlsPanel.setBackground(Color.WHITE);
            JButton removeBtn = new JButton("-");
            JLabel qtyLabel = new JLabel(String.valueOf(cartItems.get(item)));
            JButton addBtn = new JButton("+");
            controlsPanel.add(removeBtn);
            controlsPanel.add(qtyLabel);
            controlsPanel.add(addBtn);

            gbc.gridx = 2;
            itemPanel.add(controlsPanel, gbc);

            // Price
            double price = cartPrices.get(item);
            int quantity = cartItems.get(item);
            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", price * quantity));
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            itemPanel.add(priceLabel, gbc);

            // Button logic
            removeBtn.addActionListener(e -> {
                CartManager.removeItem(item);
                updateCartPage();
            });

            addBtn.addActionListener(e -> {
                CartManager.addItem(item, price);
                updateCartPage();
            });

            itemsPanel.add(itemPanel);

            // Divider
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setForeground(Color.LIGHT_GRAY);
            itemsPanel.add(separator);
        }

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton discountBtn = new JButton("Apply 20% Discount (PWD/Senior)");
        discountBtn.addActionListener(e -> {
            CartManager.applyDiscount(true);
            updateCartPage();
        });

        JLabel totalLabel = new JLabel("Total: ₱" + String.format("%.2f", CartManager.getTotal()), SwingConstants.CENTER);

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Thank you for your purchase! Total Paid: ₱" + String.format("%.2f", CartManager.getTotal()));
            CartManager.clearCart();
            dispose();
        });

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> dispose());

        bottomPanel.add(discountBtn);
        bottomPanel.add(totalLabel);
        bottomPanel.add(checkoutBtn);
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void updateCartPage() {
        dispose();
        new CartPage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CartPage::new);
    }
}
