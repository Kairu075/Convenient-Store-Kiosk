package kiosk;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class TobaccoAndAlcoholPage extends JPanel implements KioskPage {
    private KioskMainPage parent;
    private JPanel productPanel;
    private JButton tobaccoButton, alcoholButton;
    private JButton activeButton;
    private JLabel cartCountLabel;
    private JTextField searchBar;
    private String currentCategory = "Tobacco";
    private final DecimalFormat priceFormat = new DecimalFormat("0.00");

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(231, 76, 60);
    private final Color ACCENT_COLOR = new Color(192, 57, 43);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_LIGHT = new Color(236, 240, 241);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private Map<String, java.util.List<String>> products;
    private Map<String, java.util.List<Double>> prices;
    private Map<String, java.util.List<String>> images;

    public TobaccoAndAlcoholPage(KioskMainPage parent) {
        this.parent = parent;
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        // Age verification is now handled in the main page
        // before navigating to this page

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
        showProducts("Cigarettes");
        highlightButton(tobaccoButton);
    }
    
    private boolean verifyAge() {
        // Create a simple age verification panel
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Please verify you are 18 years or older to view this category.");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JCheckBox ageCheck = new JCheckBox("I confirm that I am 18 years or older");
        ageCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel warningLabel = new JLabel("Note: Only adults 18+ can purchase alcohol and tobacco products");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        warningLabel.setForeground(Color.RED);
        
        panel.add(label);
        panel.add(ageCheck);
        panel.add(warningLabel);
        
        int result = JOptionPane.showConfirmDialog(null, panel, 
                "Age Verification", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION && ageCheck.isSelected()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, 
                "Age verification is required to access this category.", 
                "Access Denied", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void initProducts() {
        products = new HashMap<>();
        prices = new HashMap<>();
        images = new HashMap<>();

        // Cigarettes
        products.put("Cigarettes", Arrays.asList(
            "Marlboro Red", "Marlboro Gold", "Marlboro Ice Blast", "Winston Red", 
            "Winston Blue", "Camel", "Lucky Strike", "Pall Mall", "Dunhill", "Philip Morris",
            "Mighty", "Mevius", "Esse", "Chesterfield", "Fortune"
        ));
        prices.put("Cigarettes", Arrays.asList(
            215.0, 215.0, 220.0, 180.0, 180.0, 200.0, 210.0, 170.0, 230.0, 185.0,
            140.0, 210.0, 190.0, 165.0, 110.0
        ));
        images.put("Cigarettes", Arrays.asList(
            "marlborored.png", "marlborogold.jpg", "marlboroiceblast.png", "winstonred.png", "winstonblue.jpg", "camel.jpg", "luckystrike.jpg", "pallmall.jpg", "dunhill.jpg", "philipmorrisjpg", "mighty.jpg", "mevius.jpg", "esse.jpg", "chesterfield.jpg", "fortune.jpg"
        ));

        // Beer & Alcohol
        products.put("Beer & Alcohol", Arrays.asList(
            "San Miguel Pale Pilsen", "Red Horse", "Heineken", "Corona", "Smirnoff Mule",
            "San Miguel Light", "Tanduay Rhum", "GSM Blue", "Emperador Light", "Alfonso Light",
            "Ginebra San Miguel", "Absolut Vodka", "Jack Daniel's", "Johnnie Walker", "Jose Cuervo"
        ));
        prices.put("Beer & Alcohol", Arrays.asList(
            59.0, 69.0, 89.0, 95.0, 75.0, 65.0, 125.0, 145.0, 150.0, 320.0,
            135.0, 890.0, 1150.0, 1380.0, 950.0
        ));
        images.put("Beer & Alcohol", Arrays.asList(
            "sanmiguelpalepilsen.png", "redhorse.jpg", "heineken.jpg", "corona.jpg", "smirnoffmule.jpg", "sanmiguellight.jpg", "tanduayrhum.jpg", "gsmblue.jpg", "emperadorlight.jpg", "alfonsolight.jpg", "ginebrasanmiguel.jpg", "absolutvodka.jpg", "jackdaniels.jpg", "johnniewalker.jpg", "josecuervo.jpg"
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
        menuButton.addActionListener(e -> backToMain());

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

        JLabel titleLabel = new JLabel("Tobacco & Alcohol (18+)", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createSubcategoryPanel() {
        JPanel subcategoryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        subcategoryPanel.setOpaque(false);
        subcategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        tobaccoButton = createCategoryButton("Cigarettes");
        alcoholButton = createCategoryButton("Beer & Alcohol");

        subcategoryPanel.add(tobaccoButton);
        subcategoryPanel.add(alcoholButton);

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

    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
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

            java.util.List<String> items = products.get(actualCategory);
            java.util.List<Double> itemPrices = prices.get(actualCategory);

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
            java.util.List<String> items = products.get(category);
            java.util.List<Double> itemPrices = prices.get(category);

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
        }        productPanel.revalidate();
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

        // Try to load an image for the product
        String itemCategory = getCategoryForItem(itemName);
        String imageFileName = getImageFileName(itemName, itemCategory);
        ImageIcon icon = loadImage(imageFileName);

        if (icon != null) {
            // Resize the image to fit nicely in the product card
            Image img = icon.getImage();
            Image resizedImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(resizedImg));
        } else {
            // If no image available, use placeholder
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
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        // Center the item name and price
        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel priceLabel = new JLabel("₱" + priceFormat.format(itemPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setForeground(ACCENT_COLOR);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addButton = new JButton("Request Staff Assistance");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> showRestrictedMessage());

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(addButton);
        infoPanel.add(Box.createVerticalGlue());

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    private void showRestrictedMessage() {
        JOptionPane.showMessageDialog(this,
            "<html><center>For your safety and in compliance with the law,<br>please ask for staff assistance to purchase tobacco or alcohol products.</center></html>",
            "Staff Assistance Required",
            JOptionPane.INFORMATION_MESSAGE);
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
        if (parent instanceof TobaccoAndAlcoholPage && this.parent != null) {
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
        if (cartCountLabel != null) {
            cartCountLabel.setText(String.valueOf(count));
            cartCountLabel.setVisible(count > 0);
        }
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

    private ImageIcon loadImage(String imageName) {
        URL imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + imageName);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        } else {
            // Try with lowercase filename as a fallback
            imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + imageName.toLowerCase());
            if (imageUrl != null) {
                return new ImageIcon(imageUrl);
            }

            // Try without the file extension
            int dotIndex = imageName.lastIndexOf('.');
            if (dotIndex > 0) {
                String nameWithoutExtension = imageName.substring(0, dotIndex);
                String[] extensions = {".jpg", ".png", ".gif"};
                for (String extension : extensions) {
                    imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + nameWithoutExtension + extension);
                    if (imageUrl != null) {
                        return new ImageIcon(imageUrl);
                    }
                }
            }
            
            System.out.println("Image not found: " + imageName);
            return null;
        }
    }

    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Request Assistance", Dialog.ModalityType.APPLICATION_MODAL);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        // Issue type selection
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(BACKGROUND_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        
        String[] issueTypes = {
            "Product Recommendation",
            "Age Verification",
            "Price Information",
            "Brand Information",
            "Other"
        };
        
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        // Details field
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
        
        // Urgency level
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(BACKGROUND_COLOR);
        
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        // Add all form components
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        // Button panel
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
            
            // Format details with urgency info
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            // Add current category information
            details += "\nCategory: " + currentCategory;
            
            // Submit help request to the manager
            HelpRequestManager.getInstance().submitRequest(
                "Tobacco & Alcohol", 
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

    private String getCategoryForItem(String itemName) {
        for (String category : products.keySet()) {
            java.util.List<String> items = products.get(category);
            if (items != null && items.contains(itemName)) {
                return category;
            }
        }
        return null;
    }

    private String getImageFileName(String itemName, String category) {
        // Convert the item name to a simple filename (lowercase, no spaces or special chars)
        String baseFileName = itemName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".png";
        
        // Special cases for specific products
        if (category != null) {
            switch (category) {
                case "Cigarettes":
                    if (itemName.contains("Marlboro")) {
                        return "marlboro.png";
                    } else if (itemName.contains("Winston")) {
                        return "winston.png";
                    } else if (itemName.contains("Camel")) {
                        return "camel.png";
                    } else if (itemName.contains("Lucky Strike")) {
                        return "luckystrike.png";
                    } else {
                        return "cigarette.png"; // Generic cigarette image
                    }
                case "Beer & Alcohol":
                    if (itemName.contains("San Miguel")) {
                        return "sanmiguel.png";
                    } else if (itemName.contains("Red Horse")) {
                        return "redhorse.png";
                    } else if (itemName.contains("Heineken")) {
                        return "heineken.png";
                    } else if (itemName.contains("Corona")) {
                        return "corona.png";
                    } else if (itemName.contains("Vodka")) {
                        return "vodka.png";
                    } else if (itemName.contains("Whiskey") || itemName.contains("Daniel") || itemName.contains("Walker")) {
                        return "whiskey.png";
                    } else if (itemName.contains("Rhum") || itemName.contains("Rum")) {
                        return "rum.png";
                    } else if (itemName.contains("Cuervo")) {
                        return "tequila.png";
                    } else {
                        return "alcohol.png"; // Generic alcohol image
                    }
                default:
                    return baseFileName;
            }
        }
        
        return baseFileName;
    }
}
