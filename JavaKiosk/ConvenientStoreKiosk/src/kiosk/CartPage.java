package kiosk;

import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.BufferedImage;

public class CartPage extends JPanel implements KioskPage {
    private final Map<String, String> imageFileMap = new HashMap<>();
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Deeper blue
    private final Color ACCENT_COLOR = new Color(245, 247, 250); // Light background
    private final Color TEXT_COLOR = new Color(24, 24, 24); // Deep black
    private final Color BUTTON_COLOR = new Color(46, 204, 113); // Green for checkout
    private final Color REMOVE_COLOR = new Color(231, 76, 60); // Red for remove
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Border ROUNDED_BORDER = new LineBorder(Color.LIGHT_GRAY, 2, true);

    private JLabel totalLabel;
    private JPanel itemsPanel;
    private JPanel summaryPanel;
    private JButton discountBtn;
    private boolean isDiscountApplied = false;
    private JDialog verificationDialog; // Dialog for ID verification
    private KioskMainPage parent;
    private JLabel cartCountLabel;

    public CartPage(KioskMainPage parent) {
        this.parent = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(ACCENT_COLOR);

        // Initialize image mapping
        initializeImageMap();

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create the scrollable items panel
        JScrollPane scrollPane = createItemsScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        // Create summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        // Update the initial state
        updateCartSummary();

        // Check if cart is empty and show message if needed
        if (CartManager.isCartEmpty()) {
            showEmptyCartMessage();
        }
    }

    private void initializeImageMap() {
        // Add all image mappings
        imageFileMap.put("Chippy", "chippy.jpg");
        imageFileMap.put("Chips Ahoy", "chipsahoy.jpg");
        imageFileMap.put("Chips Delight", "chipsdelight.jpg");
        imageFileMap.put("Choco Mallows", "chocomallows.jpg");
        imageFileMap.put("Cupp Keyk Topps Sarap", "cuppkeyktoppssarap.jpg");
        imageFileMap.put("Doowee Donut Chocolate", "doowee.jpg");
        imageFileMap.put("Foods", "foods.png");
        imageFileMap.put("Fudgee Barr", "fudgeebarr.jpg");
        imageFileMap.put("Nova", "nova.jpg");
        imageFileMap.put("Piattos", "piattos.jpg");
        imageFileMap.put("Rebisco Choco Crackers", "rebiscochoco.jpg");
        imageFileMap.put("Roller Coaster", "rollercoaster.jpg");
        imageFileMap.put("Tortillos", "tortillos.jpg");
        imageFileMap.put("Alcohol", "alcohol.png");
        imageFileMap.put("Household", "household.png");
        imageFileMap.put("Personal Care", "personal_care.png");
        imageFileMap.put("V-Cut", "vcut.jpg");
        imageFileMap.put("Cream-O", "creamo.jpg");
        imageFileMap.put("Stik-O", "stiko.jpg");
        imageFileMap.put("Cheeseburger", "cheeseburger.png");
        imageFileMap.put("Hotdog", "hotdogsandwich.png");
        imageFileMap.put("Siopao", "siopao.png");
        imageFileMap.put("Sisig with Rice", "sisig.jpg");
        imageFileMap.put("Bavarian Filled Donut", "bavarian.jpg");
        imageFileMap.put("BurgerSteak with Rice", "burgersteak.jpg");
        imageFileMap.put("Chocolate Donut", "chocolatedonut.png");
        imageFileMap.put("Cup Noodles Very Veggie", "cupnoodles.png");
        imageFileMap.put("Fried Chicken with Rice", "friedchicken.png");
        imageFileMap.put("Pancit Canton", "pancitcanton.png");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        cartCountLabel = new JLabel("0");
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Your Shopping Cart");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        JButton helpButton = new JButton("❓");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setBorderPainted(false);
        helpButton.setContentAreaFilled(false);
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelpRequestDialog());

        titlePanel.add(titleLabel);
        titlePanel.add(helpButton);

        JButton backBtn = createStyledButton("← Continue Shopping");
        backBtn.setFont(BUTTON_FONT);
        backBtn.setPreferredSize(new Dimension(250, 50));
        backBtn.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
                setVisible(false);
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createItemsScrollPane() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        populateCartItems();

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    public void clearCart() {
        // Clear the cart data
        CartManager.clearCart();

        // Update the UI
        itemsPanel.removeAll();
        showEmptyCartMessage();
        isDiscountApplied = false;
        itemsPanel.revalidate();
        itemsPanel.repaint();

        // Update summary and cart count
        updateCartSummary();
        updateCartCount();
    }

    private void populateCartItems() {
        Map<String, Integer> cartItems = CartManager.getCartItems();
        Map<String, Double> cartPrices = CartManager.getCartPrices();

        if (cartItems.isEmpty()) {
            return;
        }

        for (String item : cartItems.keySet()) {
            JPanel itemCard = createItemCard(item, cartItems.get(item), cartPrices.get(item));
            itemsPanel.add(itemCard);

            // Add a small gap between items
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private JPanel createItemCard(String itemName, int quantity, double price) {
        JPanel card = new JPanel(new BorderLayout(20, 10)); // More spacing
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                ROUNDED_BORDER,
                BorderFactory.createEmptyBorder(15, 15, 15, 15) // More padding
        ));

        // Left: Image (larger)
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100)); // Larger image
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        String fileName = imageFileMap.get(itemName);
        if (fileName != null) {
            URL imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + fileName);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImage = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("No Image");
                imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        }

        // Center: Item details with larger fonts
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        detailsPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Larger font
        nameLabel.setForeground(TEXT_COLOR);

        JLabel priceLabel = new JLabel("₱" + new DecimalFormat("0.00").format(price));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Larger font
        priceLabel.setForeground(new Color(100, 100, 100));

        JLabel subtotalLabel = new JLabel("Subtotal: ₱" + new DecimalFormat("0.00").format(price * quantity));
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        subtotalLabel.setForeground(new Color(50, 50, 50));

        detailsPanel.add(nameLabel);
        detailsPanel.add(priceLabel);
        detailsPanel.add(subtotalLabel);

        // Right: Quantity controls (larger for touch)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(Color.WHITE);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quantityPanel.setBackground(Color.WHITE);

        JButton decrementBtn = createQuantityButton("-");
        decrementBtn.setPreferredSize(new Dimension(50, 40)); // Larger button for touch
        decrementBtn.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font

        JLabel quantityLabel = new JLabel(String.valueOf(quantity));
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(50, 40)); // Larger area
        quantityLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton incrementBtn = createQuantityButton("+");
        incrementBtn.setPreferredSize(new Dimension(50, 40)); // Larger button for touch
        incrementBtn.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font

        decrementBtn.addActionListener(e -> {
            CartManager.removeItem(itemName);
            int newQty = CartManager.getItemQuantity(itemName);
            quantityLabel.setText(String.valueOf(newQty));
            decrementBtn.setEnabled(newQty > 0);
            updateProductCard(card, itemName, price);
            updateCartSummary();

            // If quantity reaches 0, remove the card
            if (newQty == 0) {
                itemsPanel.remove(card);
                itemsPanel.revalidate();
                itemsPanel.repaint();

                // Show empty cart message if no items left
                if (CartManager.isCartEmpty()) {
                    showEmptyCartMessage();
                }
            }
        });

        incrementBtn.addActionListener(e -> {
            CartManager.addItem(itemName, price);
            int newQty = CartManager.getItemQuantity(itemName);
            quantityLabel.setText(String.valueOf(newQty));
            decrementBtn.setEnabled(true);
            updateProductCard(card, itemName, price);
            updateCartSummary();
        });

        quantityPanel.add(decrementBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(incrementBtn);

        JButton removeBtn = new JButton("Remove Item");
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Larger, bolder font
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setBackground(REMOVE_COLOR); // Red button for removal
        removeBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            // Remove all quantities of this item
            for (int i = 0; i < quantity; i++) {
                CartManager.removeItem(itemName);
            }
            itemsPanel.remove(card);
            itemsPanel.revalidate();
            itemsPanel.repaint();
            updateCartSummary();
            updateCartCount();
            if (CartManager.isCartEmpty()) {
                showEmptyCartMessage();
            }
        });

        controlsPanel.add(quantityPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(removeBtn);

        card.add(imageLabel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(controlsPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createQuantityButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));
        summaryPanel.setBackground(Color.WHITE);

        JPanel summaryHeaderPanel = new JPanel(new BorderLayout());
        summaryHeaderPanel.setBackground(Color.WHITE);

        JLabel summaryHeaderLabel = new JLabel("Order Summary");
        summaryHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        summaryHeaderPanel.add(summaryHeaderLabel, BorderLayout.WEST);

        discountBtn = createStyledButton("Apply 20% Discount (PWD/Senior)");
        discountBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        discountBtn.setPreferredSize(new Dimension(0, 50));
        discountBtn.setBackground(new Color(39, 174, 96));
        discountBtn.addActionListener(e -> {
            if (isDiscountApplied) {
                isDiscountApplied = false;
                CartManager.applyDiscount(false);
                updateCartSummary();
                discountBtn.setText("Apply 20% Discount (PWD/Senior)");
                discountBtn.setBackground(new Color(39, 174, 96));
            } else {
                showIdVerificationDialog();
            }
        });

        // Add labels for subtotal, discount, and total
        JPanel costBreakdownPanel = new JPanel(new GridBagLayout());
        costBreakdownPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Subtotal
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(Color.WHITE);
        JLabel subtotalTextLabel = new JLabel("Subtotal:");
        subtotalTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JLabel subtotalValueLabel = new JLabel("₱0.00");
        subtotalValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtotalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        subtotalPanel.add(subtotalTextLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValueLabel, BorderLayout.EAST);
        gbc.gridy = 0;
        costBreakdownPanel.add(subtotalPanel, gbc);

        // Discount
        JPanel discountPanel = new JPanel(new BorderLayout());
        discountPanel.setBackground(Color.WHITE);
        JLabel discountTextLabel = new JLabel("Discount:");
        discountTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JLabel discountValueLabel = new JLabel("₱0.00");
        discountValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        discountValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        discountPanel.add(discountTextLabel, BorderLayout.WEST);
        discountPanel.add(discountValueLabel, BorderLayout.EAST);
        gbc.gridy = 1;
        costBreakdownPanel.add(discountPanel, gbc);

        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        JLabel totalTextLabel = new JLabel("Total:");
        totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel = new JLabel("₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPanel.add(totalTextLabel, BorderLayout.WEST);
        totalPanel.add(totalLabel, BorderLayout.EAST);
        gbc.gridy = 2;
        costBreakdownPanel.add(totalPanel, gbc);

        // Update values based on cart
        DecimalFormat df = new DecimalFormat("0.00");
        double subtotal = CartManager.getTotalPrice();
        subtotalValueLabel.setText("₱" + df.format(subtotal));
        double discount = CartManager.getDiscountAmount();
        discountValueLabel.setText("₱" + df.format(discount));
        double total = CartManager.getTotal();
        totalLabel.setText("₱" + df.format(total));

        JButton checkoutBtn = createStyledButton("PROCEED TO CHECKOUT");
        checkoutBtn.setBackground(BUTTON_COLOR);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        checkoutBtn.setPreferredSize(new Dimension(0, 70));
        checkoutBtn.addActionListener(e -> handleCheckout());

        summaryPanel.add(summaryHeaderPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(costBreakdownPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        summaryPanel.add(discountBtn);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(checkoutBtn);

        // Store references for updateCartSummary
        summaryPanel.putClientProperty("subtotalValueLabel", subtotalValueLabel);
        summaryPanel.putClientProperty("discountValueLabel", discountValueLabel);
        summaryPanel.putClientProperty("totalLabel", totalLabel);

        return summaryPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateProductCard(JPanel card, String itemName, double itemPrice) {
        Container parent = card.getParent();
        int index = -1;

        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == card) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            parent.remove(card);
            parent.add(createItemCard(itemName, CartManager.getItemQuantity(itemName), itemPrice), index);
            parent.revalidate();
            parent.repaint();

            // Update the summary after card update
            updateCartSummary();
            updateCartCount();
        }
    }

    private void removeItem(String itemName, int quantity, JPanel card) {
        for (int i = 0; i < quantity; i++) {
            CartManager.removeItem(itemName);
        }

        itemsPanel.remove(card);
        itemsPanel.revalidate();
        itemsPanel.repaint();

        // Update UI after removal
        updateCartSummary();
        updateCartCount();

        // Show empty cart message if no items left
        if (CartManager.isCartEmpty()) {
            showEmptyCartMessage();
        }
    }

    private void updateCartSummary() {
        DecimalFormat df = new DecimalFormat("0.00");
        double subtotal = CartManager.getTotalPrice();
        double discount = CartManager.getDiscountAmount();
        double total = CartManager.getTotal();

        JLabel subtotalValueLabel = (JLabel) summaryPanel.getClientProperty("subtotalValueLabel");
        JLabel discountValueLabel = (JLabel) summaryPanel.getClientProperty("discountValueLabel");
        JLabel totalLabelRef = (JLabel) summaryPanel.getClientProperty("totalLabel");

        if (subtotalValueLabel != null)
            subtotalValueLabel.setText("₱" + df.format(subtotal));
        if (discountValueLabel != null)
            discountValueLabel.setText("₱" + df.format(discount));
        if (totalLabelRef != null)
            totalLabelRef.setText("₱" + df.format(total));

        // Update the discount button text
        if (isDiscountApplied) {
            discountBtn.setText("Remove Discount");
            discountBtn.setBackground(REMOVE_COLOR);
        } else {
            discountBtn.setText("Apply 20% Discount (PWD/Senior)");
            discountBtn.setBackground(new Color(39, 174, 96));
        }
    }

    private void handleCheckout() {
        if (CartManager.isCartEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty. Please add items before checkout.",
                    "Empty Cart",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show payment dialog before receipt
        double total = CartManager.getTotal();
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(70, 100, 250, 100)); // Adds padding
        
        // Payment method selection
        JLabel methodLabel = new JLabel("Select payment method:");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Increased font size
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Spacing between elements
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton cashButton = new JRadioButton("Cash");
        cashButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cashButton.setBackground(Color.WHITE);
        cashButton.setSelected(true);
        
        JRadioButton ecashButton = new JRadioButton("E-Cash (QR Code)");
        ecashButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        ecashButton.setBackground(Color.WHITE);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(cashButton);
        paymentGroup.add(ecashButton);
        
        radioPanel.add(cashButton);
        radioPanel.add(ecashButton);
        
        // Cash panel components
        JPanel cashPanel = new JPanel();
        cashPanel.setLayout(new BoxLayout(cashPanel, BoxLayout.Y_AXIS));
        cashPanel.setBackground(Color.WHITE);
        cashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel paymentLabel = new JLabel("Enter payment amount:");
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField paymentField = new JTextField();
        paymentField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        paymentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel changeLabel = new JLabel("Change: ₱0.00");
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        changeLabel.setForeground(new Color(39, 174, 96));
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cashPanel.add(paymentLabel);
        cashPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cashPanel.add(paymentField);
        cashPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cashPanel.add(changeLabel);
        
        // QR Code panel
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qrPanel.setVisible(false);
        
        JLabel qrInstructionLabel = new JLabel("Scan QR code with your mobile banking app:");
        qrInstructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        qrInstructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel qrCodeLabel = new JLabel();
        qrCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCodeLabel.setPreferredSize(new Dimension(200, 200));
        
        JLabel qrAmountLabel = new JLabel(String.format("Amount: ₱%.2f", total));
        qrAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qrAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton completeQrPaymentButton = new JButton("I've Completed Payment");
        completeQrPaymentButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        completeQrPaymentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeQrPaymentButton.setBackground(new Color(39, 174, 96));
        completeQrPaymentButton.setForeground(Color.WHITE);
        completeQrPaymentButton.setMaximumSize(new Dimension(250, 40));
        
        qrPanel.add(qrInstructionLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(qrCodeLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(qrAmountLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(completeQrPaymentButton);
        
        // Generate mock QR code
        generateMockQrCode(qrCodeLabel, total);
        
        // Toggle between payment panels
        cashButton.addActionListener(e -> {
            cashPanel.setVisible(true);
            qrPanel.setVisible(false);
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });
        
        ecashButton.addActionListener(e -> {
            cashPanel.setVisible(false);
            qrPanel.setVisible(true);
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });
        
        // Add components to main panel
        paymentPanel.add(methodLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(radioPanel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        paymentPanel.add(cashPanel);
        paymentPanel.add(qrPanel);
        
        double[] paymentHolder = new double[1];
        double[] changeHolder = new double[1];
        boolean[] isEcashPayment = new boolean[1];

        // Create a final reference to the dialog we'll create
        final JDialog[] paymentDialog = new JDialog[1];
        // Create a safer dialog initialization
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof Frame) {
                paymentDialog[0] = new JDialog((Frame)parentWindow, "Payment", true);
            } else if (parentWindow instanceof Dialog) {
                paymentDialog[0] = new JDialog((Dialog)parentWindow, "Payment", true);
            } else {
                // If we can't find a proper parent, create a non-modal dialog
                paymentDialog[0] = new JDialog();
                paymentDialog[0].setTitle("Payment");
            }
        } catch (Exception ex) {
            // Fallback for any unexpected errors
            paymentDialog[0] = new JDialog();
            paymentDialog[0].setTitle("Payment");
        }
        
        paymentDialog[0].setContentPane(paymentPanel);
        paymentDialog[0].setSize(700, 900);
        paymentDialog[0].setLocationRelativeTo(null); // Center on screen instead of relative to component
        
        // Separate handling for QR code payment
        completeQrPaymentButton.addActionListener(e -> {
            isEcashPayment[0] = true;
            paymentHolder[0] = total;
            changeHolder[0] = 0.0;
            paymentDialog[0].dispose(); // Close the dialog when QR payment completes
        });

        // Add buttons for cash payment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            if (cashButton.isSelected()) {
                // Validate cash payment
                String input = paymentField.getText().trim();
                try {
                    double payment = Double.parseDouble(input);
                    if (payment < total) {
                        JOptionPane.showMessageDialog(paymentDialog[0], 
                            "Insufficient payment. Please enter an amount equal to or greater than the total.",
                            "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    double change = payment - total;
                    paymentHolder[0] = payment;
                    changeHolder[0] = change;
                    isEcashPayment[0] = false;
                    paymentDialog[0].dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(paymentDialog[0], 
                        "Please enter a valid payment amount.", 
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // For E-Cash, we wait for the completeQrPaymentButton action
                JOptionPane.showMessageDialog(paymentDialog[0],
                    "Please complete your payment by scanning the QR code and clicking 'I've Completed Payment'.",
                    "E-Cash Payment", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            paymentDialog[0].dispose();
        });
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        paymentPanel.add(buttonPanel);
        
        // Show the payment dialog and wait for user interaction
        paymentDialog[0].setVisible(true);
        
        // If dialog was closed without completing a payment, return
        if (!isEcashPayment[0] && paymentHolder[0] == 0.0) {
            return; // Payment was cancelled
        }

        // Show receipt preview with payment and change
        JTextArea receiptArea = new JTextArea(
            isEcashPayment[0] 
            ? getFormattedReceiptWithEcashPayment(total) 
            : getFormattedReceiptWithPayment(paymentHolder[0], changeHolder[0])
        );
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        // Increase the preferred size to show more content without scrolling
        receiptScrollPane.setPreferredSize(new Dimension(800, 700));

        int option = JOptionPane.showConfirmDialog(this,
                receiptScrollPane,
                "Confirm Purchase",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) { // Process the purchase
            CartManager.saveReceipt();

            // Clear the cart after updating inventory
            clearCart();

            // Update cart count label to 0 after clearing cart
            updateCartCount();

            updateInventoryStock();

            // Also update cart counters in other pages if needed
            if (parent != null) {
                parent.updateAllCartCounters();
            }

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Thank you for your purchase!\nPlease collect your items.",
                    "Purchase Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Return to main page
            if (parent != null) {
                parent.showMainPage();
            }
        }
    }

    private void updateInventoryStock() {
        // Get all items from the cart
        Map<String, Integer> cartItems = CartManager.getCartItems();

        // Update inventory stock for each item
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            // Get the item from inventory
            InventoryItem item = InventoryManager.getInstance().getItemByName(itemName);

            if (item != null) {
                // Reduce stock quantity
                int currentStock = item.getStockQuantity();
                item.setStockQuantity(currentStock - quantity);

                // Update the item in inventory
                InventoryManager.getInstance().updateItem(item);
            }
        }
    }

    // Add this helper method to show payment and change in the receipt
    private String getFormattedReceiptWithPayment(double payment, double change) {
        StringBuilder receipt = new StringBuilder();
        receipt.append(CartManager.getFormattedReceipt());
        receipt.append(String.format("Payment:                      ₱%7.2f\n", payment));
        receipt.append(String.format("Change:                       ₱%7.2f\n", change));
        receipt.append("==================================\n");
        return receipt.toString();
    }

    // Add this helper method to generate a mock QR code
    private void generateMockQrCode(JLabel qrLabel, double amount) {
        // Create a simple mock QR code (a black and white grid)
        int size = 200;
        BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = qrImage.createGraphics();
        
        // Fill with white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        
        // Draw black border
        g.setColor(Color.BLACK);
        g.drawRect(5, 5, size-10, size-10);
        
        // Generate random pattern for QR code
        g.setColor(Color.BLACK);
        int blockSize = 10;
        Random rand = new Random((long) (amount * 1000)); // Use amount as seed for consistent generation
        
        for (int y = 10; y < size-10; y += blockSize) {
            for (int x = 10; x < size-10; x += blockSize) {
                if (rand.nextBoolean()) {
                    g.fillRect(x, y, blockSize, blockSize);
                }
            }
        }
        
        // Add finder patterns (the three squares in corners of QR codes)
        drawFinderPattern(g, 20, 20, 40);
        drawFinderPattern(g, size-60, 20, 40);
        drawFinderPattern(g, 20, size-60, 40);
        
        // Add payment info in the center
        g.setColor(Color.WHITE);
        g.fillRect(70, 85, 60, 30);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("PAY", 80, 100);
        g.drawString(String.format("₱%.2f", amount), 80, 110);
        
        g.dispose();
        qrLabel.setIcon(new ImageIcon(qrImage));
    }
    
    private void drawFinderPattern(Graphics2D g, int x, int y, int size) {
        // Outer square
        g.setColor(Color.BLACK);
        g.fillRect(x, y, size, size);
        
        // Middle white square
        g.setColor(Color.WHITE);
        g.fillRect(x + size/7, y + size/7, size - 2*size/7, size - 2*size/7);
        
        // Inner black square
        g.setColor(Color.BLACK);
        g.fillRect(x + 2*size/7, y + 2*size/7, size - 4*size/7, size - 4*size/7);
    }
    
    // Add this helper method to show e-cash payment in the receipt
    private String getFormattedReceiptWithEcashPayment(double amount) {
        StringBuilder receipt = new StringBuilder();
        receipt.append(CartManager.getFormattedReceipt());
        receipt.append(String.format("Payment Method:               E-Cash\n"));
        receipt.append(String.format("Amount:                       ₱%7.2f\n", amount));
        receipt.append("==================================\n");
        return receipt.toString();
    }

    private void showEmptyCartMessage() {
        itemsPanel.removeAll();

        JPanel emptyCartPanel = new JPanel();
        emptyCartPanel.setLayout(new BoxLayout(emptyCartPanel, BoxLayout.Y_AXIS));
        emptyCartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyCartPanel.setBackground(Color.WHITE);
        emptyCartPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 80, 0));

        JLabel emptyCartIcon = new JLabel("🛒");
        emptyCartIcon.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        emptyCartIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyCartLabel = new JLabel("Your cart is empty");
        emptyCartLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        emptyCartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel suggestionLabel = new JLabel("Tap below to continue shopping");
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        suggestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton shopButton = createStyledButton("BROWSE PRODUCTS");
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        shopButton.setMaximumSize(new Dimension(300, 60));
        shopButton.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
            }
        });

        emptyCartPanel.add(emptyCartIcon);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        emptyCartPanel.add(emptyCartLabel);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        emptyCartPanel.add(suggestionLabel);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        emptyCartPanel.add(shopButton);

        itemsPanel.add(emptyCartPanel);
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void showIdVerificationDialog() {
        verificationDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "ID Verification",
                Dialog.ModalityType.APPLICATION_MODAL);
        verificationDialog.setSize(400, 300);
        verificationDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Please verify eligibility");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Checkbox for staff verification
        JCheckBox staffVerifiedCheckbox = new JCheckBox("ID has been verified by staff");
        staffVerifiedCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        staffVerifiedCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        staffVerifiedCheckbox.setBackground(panel.getBackground());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton verifyButton = new JButton("Verify ID");
        verifyButton.setPreferredSize(new Dimension(120, 40));
        verifyButton.setEnabled(false); // Disabled by default

        // Enable verify button only if checkbox is checked
        staffVerifiedCheckbox.addActionListener(e -> {
            verifyButton.setEnabled(staffVerifiedCheckbox.isSelected());
        });

        verifyButton.addActionListener(e -> {
            isDiscountApplied = true;
            CartManager.applyDiscount(true);
            updateCartSummary();
            verificationDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> verificationDialog.dispose());

        buttonPanel.add(verifyButton);
        buttonPanel.add(cancelButton);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(staffVerifiedCheckbox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        verificationDialog.add(panel);
        verificationDialog.setVisible(true);
    }

    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Request Assistance",
                Dialog.ModalityType.APPLICATION_MODAL);
        helpDialog.setSize(400, 300);
        helpDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Need help with your cart?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton assistanceButton = createStyledButton("Request Staff Assistance");
        assistanceButton.setMaximumSize(new Dimension(250, 50));
        assistanceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        assistanceButton.addActionListener(e -> {
            HelpRequestManager.getInstance().submitRequest(
                    "Shopping Cart",
                    "Customer Assistance",
                    "Customer needs help with shopping cart");
            helpDialog.dispose();
            JOptionPane.showMessageDialog(this,
                    "A staff member will assist you shortly.",
                    "Help Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(assistanceButton);

        helpDialog.add(panel);
        helpDialog.setVisible(true);
    }

    @Override
    public void backToMain() {
        if (parent != null) {
            parent.showMainPage();
        }
    }

    @Override
    public void updateCartCount() {
        int count = CartManager.getTotalItems();
        if (cartCountLabel != null) {
            cartCountLabel.setText(String.valueOf(count));
            cartCountLabel.setVisible(count > 0);
        }
    }

    /**
     * Refreshes the cart UI to reflect the current cart contents.
     * Call this when switching to the cart page.
     */
    public void refreshCart() {
        itemsPanel.removeAll();
        populateCartItems();
        itemsPanel.revalidate();
        itemsPanel.repaint();

        if (CartManager.isCartEmpty()) {
            showEmptyCartMessage();
        } else {
        }
            updateCartSummary();
            updateCartCount();
    }
    
}