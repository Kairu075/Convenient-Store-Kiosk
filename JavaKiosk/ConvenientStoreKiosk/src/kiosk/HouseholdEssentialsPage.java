package kiosk;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class HouseholdEssentialsPage extends JPanel implements KioskPage {
    private JPanel productPanel;
    private Map<String, List<String>> products;
    private Map<String, List<Double>> prices;
    private JButton cleaningButton, laundryButton, kitchenButton, papersButton;
    private CartPage cartPage;
    private JButton activeButton;
    private JLabel cartCountLabel;
    private JTextField searchBar;
    private String currentCategory = "Cleaning Supplies";
    private final DecimalFormat priceFormat = new DecimalFormat("0.00");
    
    // UI Constants
    private final Color PRIMARY_COLOR = new Color(46, 134, 193); // Blue theme for household
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_LIGHT = new Color(236, 240, 241);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private KioskMainPage parent;

    public HouseholdEssentialsPage(KioskMainPage parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        initProducts();

        // Create main components
        JPanel topPanel = createTopPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel subcategoryPanel = createSubcategoryPanel();
        JScrollPane productScrollPane = createProductScrollPane();
        
        // Assemble the UI
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.add(subcategoryPanel, BorderLayout.NORTH);
        contentPanel.add(productScrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Initial display
        showProducts("Cleaning Supplies");
        highlightButton(cleaningButton);
    }

    private void initProducts() {
        products = new HashMap<>();
        prices = new HashMap<>();

        // Cleaning Supplies
        products.put("Cleaning Supplies", Arrays.asList(
            "Zonrox Bleach", "Mr. Clean", "Lysol Disinfectant", "Joy Dishwashing Liquid", 
            "Domex Toilet Cleaner", "Toilet Duck", "Magic Clean", "Windex Glass Cleaner",
            "Tide Powder", "Mr. Muscle Kitchen Cleaner", "Vim Dishwashing Bar", "Clorox Wipes",
            "Mop", "Broom", "Dustpan & Brush Set"
        ));
        prices.put("Cleaning Supplies", Arrays.asList(
            65.0, 120.0, 180.0, 45.0, 75.0, 95.0, 55.0, 130.0,
            120.0, 110.0, 30.0, 220.0, 180.0, 150.0, 120.0
        ));

        // Laundry Essentials
        products.put("Laundry Essentials", Arrays.asList(
            "Ariel Detergent", "Tide Detergent", "Downy Fabric Conditioner", "Surf Powder", 
            "Champion Detergent", "Bareta Laundry Bar", "Pride Powder", "Laundry Basket",
            "Clothes Pins (24pc)", "Clothesline Rope", "Hangers (10pc)", "Laundry Net",
            "Delicate Wash Soap", "Bleach", "Stain Remover"
        ));
        prices.put("Laundry Essentials", Arrays.asList(
            120.0, 130.0, 95.0, 110.0, 95.0, 35.0, 70.0, 250.0,
            30.0, 45.0, 120.0, 85.0, 140.0, 50.0, 180.0
        ));

        // Kitchen Essentials
        products.put("Kitchen Essentials", Arrays.asList(
            "Aluminum Foil", "Food Containers (3pc)", "Plastic Wrap", "Freezer Bags", 
            "Paper Plates (25pc)", "Plastic Cups (25pc)", "Drinking Straws", "Dish Sponges",
            "Kitchen Towels", "Oven Mitts", "Can Opener", "Measuring Cups",
            "Wooden Spoons", "Garbage Bags", "Food Storage Bags"
        ));
        prices.put("Kitchen Essentials", Arrays.asList(
            85.0, 150.0, 65.0, 95.0, 80.0, 75.0, 35.0, 45.0,
            120.0, 160.0, 95.0, 130.0, 75.0, 110.0, 80.0
        ));

        // Paper Products
        products.put("Paper Products", Arrays.asList(
            "Tissue Paper (6 rolls)", "Kleenex Facial Tissue", "Paper Towels", "Wet Wipes", 
            "Bathroom Tissue (12 rolls)", "Kitchen Towels", "Napkins", "Handkerchiefs",
            "Toilet Paper (24 rolls)", "Facial Cotton", "Cotton Buds", "Makeup Removers",
            "Hand Towels", "Face Masks (10pc)", "Sanitary Napkins"
        ));
        prices.put("Paper Products", Arrays.asList(
            120.0, 75.0, 85.0, 65.0, 240.0, 90.0, 45.0, 65.0,
            380.0, 55.0, 45.0, 120.0, 135.0, 150.0, 95.0
        ));
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(1100, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Left section with menu and search
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);
        
        JButton menuButton = createIconButton("☰", "Main Menu");
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        menuButton.setPreferredSize(new Dimension(45, 40));
        menuButton.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
            }
        });
        
        searchBar = new JTextField(25);
        searchBar.setPreferredSize(new Dimension(280, 40));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchBar.setText("Search products...");
        searchBar.setForeground(Color.GRAY);
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals("Search products...")) {
                    searchBar.setText("");
                    searchBar.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText("Search products...");
                    searchBar.setForeground(Color.GRAY);
                }
            }
        });
        
        searchBar.addActionListener(e -> {
            String query = searchBar.getText().trim().toLowerCase();
            if (query.isEmpty() || query.equals("search products...")) {
                showProducts(currentCategory);
            } else {
                filterProducts(query);
            }
        });
        
        JButton searchButton = createIconButton("🔍", "Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        searchButton.setPreferredSize(new Dimension(45, 40));
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim().toLowerCase();
            if (query.isEmpty() || query.equals("search products...")) {
                showProducts(currentCategory);
            } else {
                filterProducts(query);
            }
        });
        
        leftPanel.add(menuButton);
        leftPanel.add(searchBar);
        leftPanel.add(searchButton);
        
        // Right section with help, back, and cart
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);
        
        JButton helpButton = createIconButton("❓", "Help");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.addActionListener(e -> showHelpRequestDialog());
        
        JButton backButton = createIconButton("←", "Back");
        backButton.setToolTipText("Return to main menu");
        backButton.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
            }
        });
        
        JPanel cartPanel = new JPanel(new BorderLayout(5, 0));
        cartPanel.setOpaque(false);
        
        JButton cartButton = createIconButton("🛒", "Cart");
        cartButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        cartCountLabel = new JLabel("0");
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cartCountLabel.setForeground(Color.WHITE);
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(new Color(231, 76, 60));
        cartCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartCountLabel.setPreferredSize(new Dimension(25, 25));
        cartCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        updateCartCount();
        
        cartPanel.add(cartButton, BorderLayout.WEST);
        cartPanel.add(cartCountLabel, BorderLayout.EAST);
        
        cartButton.addActionListener(e -> {
            if (parent != null) {
                parent.showCartPage();
            }
        });
        
        rightPanel.add(helpButton);
        rightPanel.add(backButton);
        rightPanel.add(cartPanel);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setPreferredSize(new Dimension(1100, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        JLabel titleLabel = new JLabel("Household Essentials", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createSubcategoryPanel() {
        JPanel subcategoryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        subcategoryPanel.setOpaque(false);
        subcategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        cleaningButton = createCategoryButton("Cleaning Supplies", "🧹");
        laundryButton = createCategoryButton("Laundry Essentials", "👕");
        kitchenButton = createCategoryButton("Kitchen Essentials", "🍽️");
        papersButton = createCategoryButton("Paper Products", "🧻");
        
        subcategoryPanel.add(cleaningButton);
        subcategoryPanel.add(laundryButton);
        subcategoryPanel.add(kitchenButton);
        subcategoryPanel.add(papersButton);
        
        return subcategoryPanel;
    }
    
    private JScrollPane createProductScrollPane() {
        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 3, 20, 20));
        productPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        return scrollPane;
    }
    
    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 8, 8, 8));
        return button;
    }
    
    private JButton createCategoryButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setForeground(TEXT_DARK);
        button.setBackground(CARD_COLOR);
        button.setFont(SUBTITLE_FONT);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            currentCategory = text;
            showProducts(text);
            highlightButton(button);
        });
        
        return button;
    }

    private void showProducts(String category) {
        productPanel.removeAll();
        
        String actualCategory = null;
        for (String key : products.keySet()) {
            if (key.equalsIgnoreCase(category)) {
                actualCategory = key;
                break;
            }
        }
        
        if (actualCategory == null) {
            JLabel noProductsLabel = new JLabel("No products found for category: " + category);
            noProductsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noProductsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productPanel.setLayout(new BorderLayout());
            productPanel.add(noProductsLabel, BorderLayout.CENTER);
        } else {
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
            
            List<String> items = products.get(actualCategory);
            List<Double> itemPrices = prices.get(actualCategory);
            
            if (items != null && itemPrices != null && items.size() == itemPrices.size()) {
                for (int i = 0; i < items.size(); i++) {
                    String itemName = items.get(i);
                    double itemPrice = itemPrices.get(i);
                    
                    JPanel productCard = createProductCard(itemName, itemPrice);
                    productPanel.add(productCard);
                }
            }
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }
    
    private void filterProducts(String query) {
        productPanel.removeAll();
        
        int resultCount = 0;
        
        for (String category : products.keySet()) {
            List<String> items = products.get(category);
            List<Double> itemPrices = prices.get(category);
            
            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i);
                
                if (itemName.toLowerCase().contains(query)) {
                    double itemPrice = itemPrices.get(i);
                    JPanel productCard = createProductCard(itemName, itemPrice);
                    productPanel.add(productCard);
                    resultCount++;
                }
            }
        }
        
        if (resultCount == 0) {
            JPanel noResultsPanel = new JPanel();
            noResultsPanel.setLayout(new BoxLayout(noResultsPanel, BoxLayout.Y_AXIS));
            noResultsPanel.setBackground(BACKGROUND_COLOR);
            noResultsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            
            JLabel iconLabel = new JLabel("🔎");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel messageLabel = new JLabel("No products found for \"" + query + "\"");
            messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton clearButton = new JButton("Clear Search");
            clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearButton.addActionListener(e -> {
                searchBar.setText("Search products...");
                searchBar.setForeground(Color.GRAY);
                showProducts(currentCategory);
            });
            
            noResultsPanel.add(iconLabel);
            noResultsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            noResultsPanel.add(messageLabel);
            noResultsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            noResultsPanel.add(clearButton);
            
            productPanel.setLayout(new BorderLayout());
            productPanel.add(noResultsPanel, BorderLayout.CENTER);
        } else {
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductCard(String itemName, double itemPrice) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setPreferredSize(new Dimension(150, 150));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setPreferredSize(new Dimension(120, 120));
        placeholder.setBackground(new Color(240, 240, 240));
        placeholder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel placeholderText = new JLabel(itemName.substring(0, 1).toUpperCase());
        placeholderText.setFont(new Font("SansSerif", Font.BOLD, 48));
        placeholderText.setForeground(new Color(150, 150, 150));
        placeholderText.setHorizontalAlignment(SwingConstants.CENTER);
        
        placeholder.add(placeholderText, BorderLayout.CENTER);
        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(placeholder, BorderLayout.CENTER);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        
        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(TEXT_DARK);
        
        JLabel priceLabel = new JLabel("₱" + priceFormat.format(itemPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setForeground(PRIMARY_COLOR);
        
        int currentQty = CartManager.getItemQuantity(itemName);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.setBackground(CARD_COLOR);
        
        JButton decrementBtn = new JButton("-");
        decrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        decrementBtn.setFocusPainted(false);
        decrementBtn.setPreferredSize(new Dimension(45, 40));
        decrementBtn.setEnabled(currentQty > 0);
        decrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        decrementBtn.setMargin(new Insets(5, 10, 5, 10));
        
        JLabel quantityLabel = new JLabel(String.valueOf(currentQty));
        quantityLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(50, 40));
        quantityLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        
        JButton incrementBtn = new JButton("+");
        incrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        incrementBtn.setFocusPainted(false);
        incrementBtn.setPreferredSize(new Dimension(45, 40));
        incrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        incrementBtn.setMargin(new Insets(5, 10, 5, 10));
        
        controlsPanel.add(decrementBtn);
        controlsPanel.add(quantityLabel);
        controlsPanel.add(incrementBtn);
        
        decrementBtn.addActionListener(e -> {
            CartManager.removeItem(itemName);
            updateProductCard(card, itemName, itemPrice);
            updateCartCount();
            // Update all cart counters after any cart change
            if (parent != null) parent.updateAllCartCounters();
        });

        incrementBtn.addActionListener(e -> {
            CartManager.addItem(itemName, itemPrice);
            updateProductCard(card, itemName, itemPrice);
            updateCartCount();
            // Update all cart counters after any cart change
            if (parent != null) parent.updateAllCartCounters();
            showAddToCartFeedback(itemName);
        });
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(controlsPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showAddToCartFeedback(String itemName) {
        JWindow notification = new JWindow(SwingUtilities.getWindowAncestor(this));
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(50, 50, 50, 220));
        content.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel messageLabel = new JLabel(itemName + " added to cart");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(messageLabel, BorderLayout.CENTER);
        
        notification.setContentPane(content);
        notification.pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        notification.setLocation(
            screenSize.width - notification.getWidth() - 20,
            screenSize.height - notification.getHeight() - 50
        );
        
        notification.setVisible(true);
        
        new javax.swing.Timer(1500, e -> notification.dispose()).start();
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
            parent.add(createProductCard(itemName, itemPrice), index);
            parent.revalidate();
            parent.repaint();
        }
        // After updating, also update all cart counters
        if (parent instanceof HouseholdEssentialsPage && this.parent != null) {
            this.parent.updateAllCartCounters();
        }
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
        cartCountLabel.setText(String.valueOf(count));
        cartCountLabel.setVisible(count > 0);
    }

    private void highlightButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(CARD_COLOR);
            activeButton.setForeground(TEXT_DARK);
        }
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        activeButton = button;
    }

    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Request Assistance", Dialog.ModalityType.APPLICATION_MODAL);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(BACKGROUND_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        
        String[] issueTypes = {
            "Product Recommendation",
            "Cleaning Product Advice",
            "Product Availability",
            "Product Usage Help",
            "Other"
        };
        
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(BACKGROUND_COLOR);
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
        
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(BACKGROUND_COLOR);
        
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(REGULAR_FONT);
        cancelButton.addActionListener(e -> helpDialog.dispose());
        
        JButton submitButton = new JButton("Request Help");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String issueType = (String) issueComboBox.getSelectedItem();
            String details = detailsArea.getText().trim();
            boolean isUrgent = urgentCheckBox.isSelected();
            
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            details += "\nCategory: " + currentCategory;
            
            HelpRequestManager.getInstance().submitRequest(
                "Household Essentials", 
                issueType, 
                details
            );
            
            JOptionPane.showMessageDialog(helpDialog,
                "Your help request has been submitted.\nA staff member will assist you shortly.",
                "Help Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            helpDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.setVisible(true);
    }


}
