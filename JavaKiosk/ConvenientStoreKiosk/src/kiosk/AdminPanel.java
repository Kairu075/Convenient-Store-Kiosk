package kiosk;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Admin panel for inventory and expiration management
 */
public class AdminPanel extends JFrame {
    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                
                switch (status) {
                    case "Expired":
                        c.setForeground(DANGER_COLOR);
                        break;
                    case "Expiring Soon":
                        c.setForeground(WARNING_COLOR);
                        break;
                    case "Low Stock":
                        c.setForeground(ACCENT_COLOR);
                        break;
                    default:
                        c.setForeground(SUCCESS_COLOR);
                        break;
                }
            }
            
            return c;
        }
    }
    
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private InventoryManager inventoryManager;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryFilter;
    private JLabel alertsCountLabel;
    private JPanel alertsPanel;
    private JPanel helpRequestsPanel;
    private JLabel helpRequestCountLabel;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Track the current view state
    private enum ViewMode { ALL, EXPIRED, EXPIRING_SOON, LOW_STOCK }
    private ViewMode currentView = ViewMode.ALL;
    
    public AdminPanel() {
        setTitle("Admin Panel - Inventory Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        inventoryManager = InventoryManager.getInstance();
        
        // Register as a help request listener
        HelpRequestManager.getInstance().addListener(this::handleNewHelpRequest);
        
        // Create the main components
        JPanel headerPanel = createHeaderPanel();
        JPanel leftPanel = createLeftPanel();
        JPanel mainPanel = createMainPanel();
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Load initial data
        refreshTableData();
        updateAlerts();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(DANGER_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            dispose();
            new KioskMainPage();
        });
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel navLabel = new JLabel("Navigation");
        navLabel.setFont(SUBTITLE_FONT);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton allItemsBtn = createNavButton("All Inventory Items", "📋");
        JButton expiredItemsBtn = createNavButton("Expired Items", "❌");
        JButton expiringItemsBtn = createNavButton("Expiring Soon", "⚠️");
        JButton lowStockBtn = createNavButton("Low Stock Items", "📉");
        JButton addItemBtn = createNavButton("Add New Item", "➕");
        
        allItemsBtn.addActionListener(e -> {
            currentView = ViewMode.ALL;
            refreshTableData();
        });
        
        expiredItemsBtn.addActionListener(e -> {
            currentView = ViewMode.EXPIRED;
            refreshTableData();
        });
        
        expiringItemsBtn.addActionListener(e -> {
            currentView = ViewMode.EXPIRING_SOON;
            refreshTableData();
        });
        
        lowStockBtn.addActionListener(e -> {
            currentView = ViewMode.LOW_STOCK;
            refreshTableData();
        });
        
        addItemBtn.addActionListener(e -> showAddItemDialog());
        
        navPanel.add(navLabel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        navPanel.add(allItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(expiredItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(expiringItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(lowStockBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        navPanel.add(new JSeparator());
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        navPanel.add(addItemBtn);
        
        // Alerts panel
        JPanel alertsHeaderPanel = new JPanel(new BorderLayout());
        alertsHeaderPanel.setBackground(Color.WHITE);
        alertsHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel alertsLabel = new JLabel("Alerts");
        alertsLabel.setFont(SUBTITLE_FONT);
        
        alertsCountLabel = new JLabel("0");
        alertsCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        alertsCountLabel.setForeground(Color.WHITE);
        alertsCountLabel.setBackground(DANGER_COLOR);
        alertsCountLabel.setOpaque(true);
        alertsCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        alertsCountLabel.setPreferredSize(new Dimension(30, 30));
        alertsCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        alertsHeaderPanel.add(alertsLabel, BorderLayout.WEST);
        alertsHeaderPanel.add(alertsCountLabel, BorderLayout.EAST);
        
        alertsPanel = new JPanel();
        alertsPanel.setLayout(new BoxLayout(alertsPanel, BoxLayout.Y_AXIS));
        alertsPanel.setBackground(Color.WHITE);
        alertsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        
        JScrollPane alertsScrollPane = new JScrollPane(alertsPanel);
        alertsScrollPane.setBorder(null);
        
        // Help requests panel
        JPanel helpRequestsHeaderPanel = new JPanel(new BorderLayout());
        helpRequestsHeaderPanel.setBackground(Color.WHITE);
        helpRequestsHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel helpRequestsLabel = new JLabel("Customer Assistance");
        helpRequestsLabel.setFont(SUBTITLE_FONT);
        
        helpRequestCountLabel = new JLabel("0");
        helpRequestCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        helpRequestCountLabel.setForeground(Color.WHITE);
        helpRequestCountLabel.setBackground(WARNING_COLOR);
        helpRequestCountLabel.setOpaque(true);
        helpRequestCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helpRequestCountLabel.setPreferredSize(new Dimension(30, 30));
        helpRequestCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        helpRequestsHeaderPanel.add(helpRequestsLabel, BorderLayout.WEST);
        helpRequestsHeaderPanel.add(helpRequestCountLabel, BorderLayout.EAST);
        
        helpRequestsPanel = new JPanel();
        helpRequestsPanel.setLayout(new BoxLayout(helpRequestsPanel, BoxLayout.Y_AXIS));
        helpRequestsPanel.setBackground(Color.WHITE);
        helpRequestsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        
        JScrollPane helpRequestsScrollPane = new JScrollPane(helpRequestsPanel);
        helpRequestsScrollPane.setBorder(null);
        helpRequestsScrollPane.setPreferredSize(new Dimension(280, 200));
        
        // Assemble left panel
        panel.add(navPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(alertsHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(alertsScrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(helpRequestsHeaderPanel, BorderLayout.NORTH);
        southPanel.add(helpRequestsScrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        // Load initial help requests
        updateHelpRequests();
        
        return panel;
    }
    
    private JButton createNavButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(REGULAR_FONT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BACKGROUND_COLOR);
                button.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table header panel with filters
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel tableTitle = new JLabel("Inventory Items");
        tableTitle.setFont(SUBTITLE_FONT);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel filterLabel = new JLabel("Filter by Category:");
        filterLabel.setFont(REGULAR_FONT);
        
        String[] categories = {"All Categories", "Snacks", "Ready to Eat Meals", "Beverages", "Frozen Foods"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setFont(REGULAR_FONT);
        categoryFilter.addActionListener(e -> refreshTableData());
        
        JTextField searchField = new JTextField(15);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton searchButton = new JButton("Search");
        searchButton.setFont(REGULAR_FONT);
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        filterPanel.add(filterLabel);
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(filterPanel, BorderLayout.EAST);
        
        // Table panel
        String[] columnNames = {"Item Name", "Category", "Price", "Stock", "Expiration Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(REGULAR_FONT);
        inventoryTable.setRowHeight(30);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Add custom cell renderer for status column
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        JButton editButton = new JButton("Edit Selected");
        editButton.setFont(REGULAR_FONT);
        editButton.setBackground(ACCENT_COLOR);
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                InventoryItem item = inventoryManager.getItem(itemName);
                if (item != null) {
                    showEditItemDialog(item);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to edit.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(REGULAR_FONT);
        deleteButton.setBackground(DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + itemName + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    inventoryManager.removeItem(itemName);
                    refreshTableData();
                    updateAlerts();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to delete.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(REGULAR_FONT);
        refreshButton.setBackground(SUCCESS_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            refreshTableData();
            updateAlerts();
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);
        
        // Assemble main panel
        panel.add(tableHeaderPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get selected category filter
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        // Get items based on current view
        List<InventoryItem> items;
        switch (currentView) {
            case EXPIRED:
                items = inventoryManager.getExpiredItems();
                break;
            case EXPIRING_SOON:
                items = inventoryManager.getExpiringItems(7); // 7 days warning
                break;
            case LOW_STOCK:
                items = inventoryManager.getLowStockItems(10); // 10 items threshold
                break;
            case ALL:
            default:
                items = inventoryManager.getAllItems();
                break;
        }
        
        // Apply category filter if needed
        if (!"All Categories".equals(selectedCategory)) {
            items = items.stream()
                    .filter(item -> item.getCategory().equals(selectedCategory))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Add filtered items to table
        for (InventoryItem item : items) {
            String status = getItemStatus(item);
            
            tableModel.addRow(new Object[]{
                item.getName(),
                item.getCategory(),
                String.format("₱%.2f", item.getPrice()),
                item.getStockQuantity(),
                item.getFormattedExpirationDate(),
                status
            });
        }
    }
    
    private String getItemStatus(InventoryItem item) {
        if (item.isExpired()) {
            return "Expired";
        } else if (item.isExpiringSoon(7)) {
            return "Expiring Soon";
        } else if (item.isLowStock(10)) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
    
    private void updateAlerts() {
        alertsPanel.removeAll();
        
        List<InventoryItem> expiredItems = inventoryManager.getExpiredItems();
        List<InventoryItem> expiringItems = inventoryManager.getExpiringItems(7);
        List<InventoryItem> lowStockItems = inventoryManager.getLowStockItems(10);
        
        int totalAlerts = expiredItems.size() + expiringItems.size() + lowStockItems.size();
        alertsCountLabel.setText(String.valueOf(totalAlerts));
        
        if (totalAlerts == 0) {
            JLabel noAlertsLabel = new JLabel("No alerts to display");
            noAlertsLabel.setFont(REGULAR_FONT);
            noAlertsLabel.setForeground(Color.GRAY);
            noAlertsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            alertsPanel.add(noAlertsLabel);
        } else {
            // Add expired items alerts
            for (InventoryItem item : expiredItems) {
                JLabel alertLabel = new JLabel("<html>❌ <b>" + item.getName() + "</b> is expired</html>");
                alertLabel.setFont(REGULAR_FONT);
                alertLabel.setForeground(DANGER_COLOR);
                alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                alertsPanel.add(alertLabel);
                alertsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            // Add expiring soon alerts
            for (InventoryItem item : expiringItems) {
                JLabel alertLabel = new JLabel("<html>⚠️ <b>" + item.getName() + 
                                             "</b> expires on " + item.getFormattedExpirationDate() + "</html>");
                alertLabel.setFont(REGULAR_FONT);
                alertLabel.setForeground(WARNING_COLOR);
                alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                alertsPanel.add(alertLabel);
                alertsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            // Add low stock alerts
            for (InventoryItem item : lowStockItems) {
                if (!item.isExpired() && !item.isExpiringSoon(7)) { // Avoid duplicate alerts
                    JLabel alertLabel = new JLabel("<html>📉 <b>" + item.getName() + 
                                                 "</b> is low in stock (" + item.getStockQuantity() + ")</html>");
                    alertLabel.setFont(REGULAR_FONT);
                    alertLabel.setForeground(ACCENT_COLOR);
                    alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    alertsPanel.add(alertLabel);
                    alertsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }
        
        alertsPanel.revalidate();
        alertsPanel.repaint();
    }
    
    private void updateHelpRequests() {
        helpRequestsPanel.removeAll();
        
        List<HelpRequestManager.HelpRequest> requests = HelpRequestManager.getInstance().getActiveRequests();
        
        helpRequestCountLabel.setText(String.valueOf(requests.size()));
        helpRequestCountLabel.setVisible(requests.size() > 0);
        
        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No assistance requests");
            noRequestsLabel.setFont(REGULAR_FONT);
            noRequestsLabel.setForeground(Color.GRAY);
            noRequestsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            helpRequestsPanel.add(noRequestsLabel);
        } else {
            for (HelpRequestManager.HelpRequest request : requests) {
                JPanel requestPanel = createHelpRequestPanel(request);
                helpRequestsPanel.add(requestPanel);
                helpRequestsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        helpRequestsPanel.revalidate();
        helpRequestsPanel.repaint();
    }
    
    private JPanel createHelpRequestPanel(HelpRequestManager.HelpRequest request) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 240)); // Light yellow background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String urgentPrefix = request.getDetails().startsWith("[URGENT]") ? "⚠️ URGENT: " : "";
        
        JLabel locationLabel = new JLabel(urgentPrefix + "Location: " + request.getLocation());
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        locationLabel.setForeground(Color.BLACK);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel("Issue: " + request.getIssueType());
        typeLabel.setFont(REGULAR_FONT);
        typeLabel.setForeground(Color.BLACK);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Time: " + request.getFormattedTimestamp());
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String details = request.getDetails();
        if (details != null && !details.isEmpty()) {
            if (details.startsWith("[URGENT]")) {
                details = details.substring(8);
            }
            
            JTextArea detailsArea = new JTextArea(details);
            detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailsArea.setBackground(new Color(255, 250, 240));
            detailsArea.setWrapStyleWord(true);
            detailsArea.setLineWrap(true);
            detailsArea.setEditable(false);
            detailsArea.setOpaque(false);
            detailsArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            panel.add(locationLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(typeLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(timeLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(new JSeparator());
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(detailsArea);
        } else {
            panel.add(locationLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton resolveButton = new JButton("Mark Resolved");
        resolveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setBackground(SUCCESS_COLOR);
        resolveButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        resolveButton.setFocusPainted(false);
        resolveButton.addActionListener(e -> {
            HelpRequestManager.getInstance().resolveRequest(request.getId());
            updateHelpRequests();
        });
        
        buttonPanel.add(resolveButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private void showAddItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Inventory Item", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Snacks", "Ready to Eat Meals", "Beverages", "Frozen Foods"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        JLabel priceLabel = new JLabel("Price (₱):");
        JTextField priceField = new JTextField();
        
        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField();
        
        JLabel expirationLabel = new JLabel("Expiration Date (YYYY-MM-DD):");
        JTextField expirationField = new JTextField();
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(expirationLabel);
        formPanel.add(expirationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add Item");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        addButton.addActionListener(e -> {
            // Validate input
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an item name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate expirationDate;
            try {
                expirationDate = LocalDate.parse(expirationField.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid date in format YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new item
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            
            // Create new item
            InventoryItem newItem = new InventoryItem(category, category, stock, stock, expirationDate, category, category);
            newItem.setName(name);
            newItem.setCategory(category);
            newItem.setPrice(price);
            newItem.setStockQuantity(stock);
            newItem.setExpirationDate(expirationDate);
            inventoryManager.addItem(newItem);
            
            refreshTableData();
            updateAlerts();
            dialog.dispose();
            
            JOptionPane.showMessageDialog(dialog, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditItemDialog(InventoryItem item) {
        JDialog dialog = new JDialog(this, "Edit Inventory Item", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField(item.getName());
        nameField.setEditable(false); // Don't allow name editing
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Snacks", "Ready to Eat Meals", "Beverages", "Frozen Foods"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(item.getCategory());
        
        JLabel priceLabel = new JLabel("Price (₱):");
        JTextField priceField = new JTextField(String.valueOf(item.getPrice()));
        
        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField(String.valueOf(item.getStockQuantity()));
        
        JLabel expirationLabel = new JLabel("Expiration Date (YYYY-MM-DD):");
        JTextField expirationField = new JTextField(item.getFormattedExpirationDate());
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(expirationLabel);
        formPanel.add(expirationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Changes");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            // Validate input
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate expirationDate;
            try {
                expirationDate = LocalDate.parse(expirationField.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid date in format YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update item
            item.setCategory((String) categoryCombo.getSelectedItem());
            item.setPrice(price);
            item.setStockQuantity(stock);
            item.setExpirationDate(expirationDate);
            
            inventoryManager.updateItem(item);
            
            refreshTableData();
            updateAlerts();
            dialog.dispose();
            
            JOptionPane.showMessageDialog(dialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Called when a new help request is received
     */
    private void handleNewHelpRequest(HelpRequestManager.HelpRequest request) {
        // Update the UI
        updateHelpRequests();
        
        // Show a notification
        boolean isUrgent = request.getDetails().startsWith("[URGENT]");
        
        // Play notification sound
        Toolkit.getDefaultToolkit().beep();
        
        // Display the notification dialog
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this, "Customer Assistance Request", false);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.setBackground(Color.WHITE);
            
            JLabel iconLabel = new JLabel(isUrgent ? "⚠️" : "ℹ️");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel titleLabel = new JLabel("New Assistance Request");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel detailsLabel = new JLabel(
                "<html><b>Location:</b> " + request.getLocation() + 
                "<br><b>Type:</b> " + request.getIssueType() + 
                (isUrgent ? "<br><span style='color:red'><b>URGENT ASSISTANCE NEEDED</b></span>" : "") +
                "</html>"
            );
            detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.add(titleLabel, BorderLayout.NORTH);
            infoPanel.add(detailsLabel, BorderLayout.CENTER);
            
            contentPanel.add(iconLabel, BorderLayout.WEST);
            contentPanel.add(infoPanel, BorderLayout.CENTER);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(okButton);
            
            dialog.add(contentPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
            // Auto-dismiss after 10 seconds if urgent, 15 seconds otherwise
            new Timer(isUrgent ? 10000 : 15000, e -> dialog.dispose()).start();
        });
    }
    
    @Override
    public void dispose() {
        HelpRequestManager.getInstance().removeListener(this::handleNewHelpRequest);
        super.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLoginPanel::new);
    }
}
