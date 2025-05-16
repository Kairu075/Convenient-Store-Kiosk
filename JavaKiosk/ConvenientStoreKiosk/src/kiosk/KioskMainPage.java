package kiosk;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;

public class KioskMainPage extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private PersonalCarePage personalCarePage;
    private TobaccoAndAlcoholPage tobaccoPage;
    private FoodsAndBeveragesPage foodsPage;
    private HouseholdEssentialsPage householdPage;
    private CartPage cartPage;
    
    // UI Constants for consistent design
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 20);

    public KioskMainPage() {
        this(true);
    }
    
    public KioskMainPage(boolean showImmediately) {
        setTitle("Convenient Store Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);

        // Create main panel first
        JPanel mainPanel = createMainPanel();
        contentPanel.add(mainPanel, "main");

        // Initialize all pages with this instance
        cartPage = new CartPage(this);
        personalCarePage = new PersonalCarePage(this);
        tobaccoPage = new TobaccoAndAlcoholPage(this);
        foodsPage = new FoodsAndBeveragesPage(this);
        householdPage = new HouseholdEssentialsPage(this);

        // Add pages to card layout
        contentPanel.add(personalCarePage, "personal");
        contentPanel.add(tobaccoPage, "tobacco");
        contentPanel.add(foodsPage, "foods");
        contentPanel.add(householdPage, "household");
        contentPanel.add(cartPage, "cart");

        showMainPage();
        
        if (showImmediately) {
            setVisible(true);
        }
    }
    
    public void showMainPage() {
        cardLayout.show(contentPanel, "main");
    }

    public void showPersonalCarePage() {
        cardLayout.show(contentPanel, "personal");
    }

    public void showTobaccoPage() {
        cardLayout.show(contentPanel, "tobacco");
    }

    public void showFoodsPage() {
        cardLayout.show(contentPanel, "foods");
    }

    public void showHouseholdPage() {
        cardLayout.show(contentPanel, "household");
    }

    public void showCartPage() {
        cardLayout.show(contentPanel, "cart");
        cartPage.refreshCart(); // Ensure cart is up-to-date when shown
        updateAllCartCounters();
    }

    // Call this after checkout or cart update to sync all cart counters
    public void updateAllCartCounters() {
        cartPage.updateCartCount();
        if (personalCarePage != null) personalCarePage.updateCartCount();
        if (tobaccoPage != null) tobaccoPage.updateCartCount();
        if (foodsPage != null) foodsPage.updateCartCount();
        if (householdPage != null) householdPage.updateCartCount();
        // If you have a cart count label in main page, update it here as well
        // Example: updateMainCartCount();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Top Panel with cart and help buttons
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Title panel with welcome message
        JPanel titlePanel = createTitlePanel();
        
        // Categories Panel - larger for kiosk
        JPanel categoriesPanel = createCategoriesPanel();
        
        // Create a main content panel to hold title and categories
        JPanel contentPanel = new JPanel(new BorderLayout(0, 30));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(categoriesPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(0, 70)); // Taller for touch
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Left section with store name/logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);
        
        JLabel storeLabel = new JLabel("Convenient Store Kiosk");
        storeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        leftPanel.add(storeLabel);
        
        // Right section with help and cart
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);
        
        // Create text-based buttons instead of icon buttons
        JButton helpButton = new JButton("Help");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        helpButton.setForeground(Color.WHITE);
        helpButton.setBackground(PRIMARY_COLOR);
        helpButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        helpButton.setFocusPainted(false);
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelpRequestDialog());
        
        JButton adminButton = new JButton("Admin");
        adminButton.setToolTipText("Administrative Functions");
        adminButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        adminButton.setForeground(Color.WHITE);
        adminButton.setBackground(PRIMARY_COLOR);
        adminButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        adminButton.setFocusPainted(false);
        adminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminButton.addActionListener(e -> {
            AdminLoginPanel adminPanel = new AdminLoginPanel();
            adminPanel.setVisible(true);
            setVisible(false);
        });
        
        JPanel cartPanel = new JPanel(new BorderLayout(5, 0));
        cartPanel.setOpaque(false);
        
        JButton cartButton = new JButton("Cart");
        cartButton.setToolTipText("View Cart");
        cartButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cartButton.setForeground(Color.WHITE);
        cartButton.setBackground(PRIMARY_COLOR);
        cartButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cartButton.setFocusPainted(false);
        cartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel cartCountLabel = new JLabel();
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(new Color(231, 76, 60));
        cartCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartCountLabel.setPreferredSize(new Dimension(28, 28));
        cartCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        // Update cart count from cart manager
        int count = CartManager.getTotalItems();
        cartCountLabel.setText(String.valueOf(count));
        cartCountLabel.setVisible(count > 0);
        
        cartPanel.add(cartButton, BorderLayout.WEST);
        cartPanel.add(cartCountLabel, BorderLayout.EAST);
        
        // Make the cart button open the cart directly
        cartButton.addActionListener(e -> showCartPage());
        
        JTextField searchBar = new JTextField(30);
        searchBar.setPreferredSize(new Dimension(280, 40));  // Increased height for touch
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Increased font size
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        rightPanel.add(helpButton);
        rightPanel.add(adminButton);
        rightPanel.add(cartPanel);
        
        // Add close button to the right panel
        JButton closeButton = new JButton("Exit");
        closeButton.setToolTipText("Close Application");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        rightPanel.add(closeButton);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        return topPanel;
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ACCENT_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        titlePanel.setPreferredSize(new Dimension(0, 150));
        
        JLabel welcomeLabel = new JLabel("Welcome to Our Store", SwingConstants.LEFT);
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Please select a category to start shopping", SwingConstants.LEFT);
        subtitleLabel.setFont(REGULAR_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        labelPanel.setOpaque(false);
        labelPanel.add(welcomeLabel);
        labelPanel.add(subtitleLabel);
        
        titlePanel.add(labelPanel, BorderLayout.CENTER);
        return titlePanel;
    }
    
    private JPanel createCategoriesPanel() {
        JPanel categoriesPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        categoriesPanel.setOpaque(false);
        
        // Foods & Beverages card
        JPanel foodsCard = createCategoryCard("Foods & Beverages", "foods.png", e -> showFoodsPage());
        
        // Tobacco & Alcohol card with age verification
        JPanel alcoholCard = createCategoryCard("Tobacco & Alcohol (18+)", "alcohol.png", e -> verifyAgeBeforeShowingTobaccoPage());
        
        // Personal Care card
        JPanel personalCareCard = createCategoryCard("Personal Care & Hygiene", "personal_care.png", e -> showPersonalCarePage());
        
        // Household Essentials card
        JPanel householdCard = createCategoryCard("Household Essentials", "household.png", e -> showHouseholdPage());
        
        categoriesPanel.add(foodsCard);
        categoriesPanel.add(alcoholCard);
        categoriesPanel.add(personalCareCard);
        categoriesPanel.add(householdCard);
        
        return categoriesPanel;
    }
    
    private JPanel createCategoryCard(String title, String imagePath, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        URL imageUrl = getClass().getResource("/kiosk/resources/" + imagePath);
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            imageLabel.setText("Image not found");
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ACCENT_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Assemble card
        card.add(imagePanel, BorderLayout.CENTER);
        card.add(titlePanel, BorderLayout.SOUTH);
        
        // Add click behavior to entire card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 3, true));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
            }
        });
        
        return card;
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
            "General Assistance",
            "Product Information",
            "Technical Problem",
            "Payment Issue",
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
        submitButton.setFont(BUTTON_FONT);
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
            
            // Submit help request to the manager
            HelpRequestManager.getInstance().submitRequest(
                "Main Menu", 
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

    /**
     * Verifies age before showing tobacco and alcohol page
     */
    private void verifyAgeBeforeShowingTobaccoPage() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "You must be 18 or older to view this category.\nAre you 18 or older?",
            "Age Verification",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            showTobaccoPage();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "You must be 18 or older to view tobacco and alcohol products.",
                "Age Verification Failed",
                JOptionPane.WARNING_MESSAGE
            );
            // Return to main page or stay on current page
            showMainPage();
        }
    }

    private static void showPage(JFrame currentFrame, JPanel newPanel) {
        currentFrame.getContentPane().removeAll();
        currentFrame.add(newPanel);
        currentFrame.revalidate();
        currentFrame.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(KioskMainPage::new);
    }
}


