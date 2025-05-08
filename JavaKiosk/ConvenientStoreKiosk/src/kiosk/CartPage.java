package kiosk;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

public class CartPage extends JFrame {
    private final Map<String, String> imageFileMap = new HashMap<>();
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Deeper blue
    private final Color ACCENT_COLOR = new Color(245, 247, 250); // Light background
    private final Color TEXT_COLOR = new Color(24, 24, 24);  // Deep black
    private final Color BUTTON_COLOR = new Color(46, 204, 113); // Green for checkout
    private final Color REMOVE_COLOR = new Color(231, 76, 60); // Red for remove
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Border ROUNDED_BORDER = new LineBorder(Color.LIGHT_GRAY, 2, true);
    
    private JLabel totalLabel;
    private JPanel itemsPanel;
    private JButton discountBtn;
    private boolean isDiscountApplied = false;
    private JDialog verificationDialog; // Dialog for ID verification

    public CartPage() {
        this(true);
    }
    
    public CartPage(boolean showImmediately) {
        setTitle("Shopping Cart");
        setSize(800, 800); // Larger size for kiosk display
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15)); // More spacing
        getContentPane().setBackground(ACCENT_COLOR);
        
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
        
        if (showImmediately) {
            setVisible(true);
        }
    }
    
    private void initializeImageMap() {
        // Add all image mappings
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
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
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
            dispose();  // Close this window
            new KioskMainPage();  // Open the main page directly
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
            updateCartPage();
        });
        
        incrementBtn.addActionListener(e -> {
            CartManager.addItem(itemName, price);
            updateCartPage();
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
            updateCartPage();
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Larger font
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2)); // More visible border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY), // Thicker border
            BorderFactory.createEmptyBorder(20, 30, 20, 30) // More padding
        ));
        summaryPanel.setBackground(Color.WHITE);
        
        // Order summary section with larger text
        JPanel summaryHeaderPanel = new JPanel(new BorderLayout());
        summaryHeaderPanel.setBackground(Color.WHITE);
        
        JLabel summaryHeaderLabel = new JLabel("Order Summary");
        summaryHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger font
        summaryHeaderPanel.add(summaryHeaderLabel, BorderLayout.WEST);
        
        // Discount button
        discountBtn = createStyledButton("Apply 20% Discount (PWD/Senior)");
        discountBtn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        discountBtn.setPreferredSize(new Dimension(0, 50)); // Taller button
        discountBtn.setBackground(new Color(39, 174, 96));
        discountBtn.addActionListener(e -> {
            if (isDiscountApplied) {
                // If discount is already applied, just remove it
                isDiscountApplied = false;
                CartManager.applyDiscount(false);
                updateCartSummary();
                discountBtn.setText("Apply 20% Discount (PWD/Senior)");
                discountBtn.setBackground(new Color(39, 174, 96));
            } else {
                // If discount is not applied, show verification dialog
                showIdVerificationDialog();
            }
        });
        
        // Cost breakdown with larger fonts
        JPanel costBreakdownPanel = new JPanel(new GridLayout(3, 2, 5, 15)); // More vertical spacing
        costBreakdownPanel.setBackground(Color.WHITE);
        
        JLabel subtotalTextLabel = new JLabel("Subtotal:");
        subtotalTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Larger font
        JLabel subtotalValueLabel = new JLabel("₱0.00", JLabel.RIGHT);
        subtotalValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Larger font
        
        JLabel discountTextLabel = new JLabel("Discount:");
        discountTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Larger font
        JLabel discountValueLabel = new JLabel("₱0.00", JLabel.RIGHT);
        discountValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Larger font
        
        JLabel totalTextLabel = new JLabel("Total:");
        totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger font
        totalLabel = new JLabel("₱0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger font
        
        costBreakdownPanel.add(subtotalTextLabel);
        costBreakdownPanel.add(subtotalValueLabel);
        costBreakdownPanel.add(discountTextLabel);
        costBreakdownPanel.add(discountValueLabel);
        costBreakdownPanel.add(totalTextLabel);
        costBreakdownPanel.add(totalLabel);
        
        // Update values based on cart
        DecimalFormat df = new DecimalFormat("0.00");
        double subtotal = CartManager.getTotalPrice();
        subtotalValueLabel.setText("₱" + df.format(subtotal));
        
        double discount = CartManager.getDiscountAmount();
        discountValueLabel.setText("₱" + df.format(discount));
        
        double total = CartManager.getTotal();
        totalLabel.setText("₱" + df.format(total));
        
        // Checkout button - larger for kiosk
        JButton checkoutBtn = createStyledButton("PROCEED TO CHECKOUT");
        checkoutBtn.setBackground(BUTTON_COLOR);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font
        checkoutBtn.setPreferredSize(new Dimension(0, 70)); // Much taller button for touch
        checkoutBtn.addActionListener(e -> {
            if (CartManager.isCartEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Your cart is empty. Please add items before checkout.", 
                    "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JTextArea receiptArea = new JTextArea(CartManager.getFormattedReceipt());
            receiptArea.setEditable(false);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Larger font
            
            JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
            receiptScrollPane.setPreferredSize(new Dimension(500, 400)); // Larger receipt
            
            JOptionPane.showMessageDialog(this, receiptScrollPane, 
                "Purchase Completed", JOptionPane.INFORMATION_MESSAGE);
            
            CartManager.clearCart();
            dispose();  // Close this window
            new KioskMainPage();  // Return to the main page
        });
        
        // Put it all together
        summaryPanel.add(summaryHeaderPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(costBreakdownPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        summaryPanel.add(discountBtn);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(checkoutBtn);
        
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
    
    private void updateCartSummary() {
        DecimalFormat df = new DecimalFormat("0.00");
        double total = CartManager.getTotal();
        totalLabel.setText("₱" + df.format(total));
        
        // Update the discount button state based on cart manager
        isDiscountApplied = CartManager.isDiscountApplied();
        if (isDiscountApplied) {
            discountBtn.setText("Remove Discount");
            discountBtn.setBackground(new Color(231, 76, 60));
        } else {
            discountBtn.setText("Apply 20% Discount (PWD/Senior)");
            discountBtn.setBackground(new Color(39, 174, 96));
        }
    }
    
    private void showIdVerificationDialog() {
        verificationDialog = new JDialog(this, "ID Verification Required", true);
        verificationDialog.setSize(500, 400);
        verificationDialog.setLocationRelativeTo(this);
        verificationDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(ACCENT_COLOR);
        
        // Header
        JLabel headerLabel = new JLabel("Please Verify Your Eligibility");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Instruction text
        JLabel instructionLabel = new JLabel("Select ID type and enter the ID number");
        instructionLabel.setFont(REGULAR_FONT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ID Type selection
        JPanel idTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idTypePanel.setBackground(ACCENT_COLOR);
        idTypePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel idTypeLabel = new JLabel("ID Type: ");
        idTypeLabel.setFont(REGULAR_FONT);
        
        String[] idTypes = {"Senior Citizen ID", "PWD ID", "Other Valid ID"};
        JComboBox<String> idTypeComboBox = new JComboBox<>(idTypes);
        idTypeComboBox.setFont(REGULAR_FONT);
        idTypeComboBox.setPreferredSize(new Dimension(200, 30));
        
        idTypePanel.add(idTypeLabel);
        idTypePanel.add(idTypeComboBox);
        
        // ID Number input
        JPanel idNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idNumberPanel.setBackground(ACCENT_COLOR);
        idNumberPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel idNumberLabel = new JLabel("ID Number: ");
        idNumberLabel.setFont(REGULAR_FONT);
        
        JTextField idNumberField = new JTextField(15);
        idNumberField.setFont(REGULAR_FONT);
        idNumberField.setPreferredSize(new Dimension(200, 30));
        
        idNumberPanel.add(idNumberLabel);
        idNumberPanel.add(idNumberField);
        
        // Employee verification option
        JPanel employeePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        employeePanel.setBackground(ACCENT_COLOR);
        employeePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JCheckBox employeeVerifiedCheckbox = new JCheckBox("Employee verified ID in person");
        employeeVerifiedCheckbox.setFont(REGULAR_FONT);
        employeeVerifiedCheckbox.setBackground(ACCENT_COLOR);
        
        employeePanel.add(employeeVerifiedCheckbox);
        
        // Error message label (initially hidden)
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setForeground(REMOVE_COLOR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(ACCENT_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(BUTTON_FONT);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> verificationDialog.dispose());
        
        JButton verifyButton = new JButton("Verify & Apply");
        verifyButton.setFont(BUTTON_FONT);
        verifyButton.setPreferredSize(new Dimension(150, 40));
        verifyButton.setBackground(PRIMARY_COLOR);
        verifyButton.setForeground(Color.WHITE);
        verifyButton.addActionListener(e -> {
            String idType = (String) idTypeComboBox.getSelectedItem();
            String idNumber = idNumberField.getText().trim();
            boolean isEmployeeVerified = employeeVerifiedCheckbox.isSelected();
            
            // Basic validation
            if (idNumber.isEmpty()) {
                errorLabel.setText("Please enter an ID number");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!isEmployeeVerified) {
                errorLabel.setText("Employee verification is required");
                errorLabel.setVisible(true);
                return;
            }
            
            // Log the verification (in real system, would store this)
            System.out.println("Discount applied with " + idType + " #" + idNumber);
            
            // Apply the discount
            isDiscountApplied = true;
            CartManager.applyDiscount(true);
            updateCartSummary();
            discountBtn.setText("Remove Discount");
            discountBtn.setBackground(new Color(231, 76, 60));
            
            // Show confirmation
            JOptionPane.showMessageDialog(verificationDialog,
                "Discount successfully applied!",
                "Verification Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            verificationDialog.dispose();
        });
        
        // Add components
        buttonPanel.add(cancelButton);
        buttonPanel.add(verifyButton);
        
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(idTypePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(idNumberPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(employeePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        verificationDialog.add(contentPanel, BorderLayout.CENTER);
        verificationDialog.add(buttonPanel, BorderLayout.SOUTH);
        verificationDialog.setVisible(true);
    }
    
    private void showEmptyCartMessage() {
        itemsPanel.removeAll();
        
        JPanel emptyCartPanel = new JPanel();
        emptyCartPanel.setLayout(new BoxLayout(emptyCartPanel, BoxLayout.Y_AXIS));
        emptyCartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyCartPanel.setBackground(Color.WHITE);
        emptyCartPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 80, 0)); // More padding
        
        JLabel emptyCartIcon = new JLabel("🛒");
        emptyCartIcon.setFont(new Font("Segoe UI", Font.PLAIN, 100)); // Much larger icon
        emptyCartIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel emptyCartLabel = new JLabel("Your cart is empty");
        emptyCartLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Larger font
        emptyCartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel suggestionLabel = new JLabel("Tap below to continue shopping");
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Larger font
        suggestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton shopButton = createStyledButton("BROWSE PRODUCTS");
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font
        shopButton.setMaximumSize(new Dimension(300, 60)); // Fixed width, taller
        shopButton.addActionListener(e -> {
            dispose();  // Close this window
            new FoodsAndBeveragesPage();  // Open the foods page directly
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
    
    private void updateCartPage() {
        dispose();
        new CartPage();
    }

    /**
     * Shows a dialog for the customer to request assistance
     */
    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(this, "Request Assistance", true);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(ACCENT_COLOR);
        
        // Header
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(ACCENT_COLOR);
        
        // Issue type selection
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(ACCENT_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        
        String[] issueTypes = {
            "Payment Issue",
            "Cart Problem",
            "Product Questions",
            "Discount Help",
            "Other"
        };
        
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        // Details field
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(ACCENT_COLOR);
        JLabel detailsLabel = new JLabel("Additional Details (optional):");
        detailsLabel.setFont(REGULAR_FONT);
        
        JTextArea detailsArea = new JTextArea(4, 20);
        detailsArea.setFont(REGULAR_FONT);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Urgency level
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(ACCENT_COLOR);
        
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(ACCENT_COLOR);
        
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        // Add all form components
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ACCENT_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(REGULAR_FONT);
        cancelButton.addActionListener(e -> helpDialog.dispose());
        
        JButton submitButton = new JButton("Request Help");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String issueType = (String) issueComboBox.getSelectedItem();
            String details = detailsArea.getText().trim();
            boolean isUrgent = urgentCheckBox.isSelected();
            
            // Format details with urgency info
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            // Add cart information
            int itemCount = CartManager.getTotalItems();
            double cartTotal = CartManager.getTotal();
            details += String.format("\nCart Info: %d items, Total: ₱%.2f", itemCount, cartTotal);
            
            // Submit help request to the manager
            HelpRequestManager.getInstance().submitRequest(
                "Shopping Cart", 
                issueType, 
                details
            );
            
            // Confirmation message
            JOptionPane.showMessageDialog(helpDialog,
                "Your help request has been submitted.\nA staff member will assist you shortly.",
                "Help Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            helpDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Assemble dialog
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CartPage::new);
    }
}