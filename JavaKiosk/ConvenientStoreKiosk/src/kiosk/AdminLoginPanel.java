package kiosk;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Login panel for administrative access
 */
public class AdminLoginPanel extends JFrame {
    // UI Constants
    private final Color PRIMARY_COLOR = new Color(52, 73, 94);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    
    // Hard-coded credentials map (in a real application, this would be stored securely)
    private static final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>();
    
    static {
        // Initialize with some default credentials
        ADMIN_CREDENTIALS.put("admin", "admin123");
        ADMIN_CREDENTIALS.put("manager", "manager123");
    }
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorMessageLabel;
    
    public AdminLoginPanel() {
        setTitle("Admin Authentication");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        JPanel headerPanel = createHeaderPanel();
        JPanel loginPanel = createLoginPanel();
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set visible
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Administrator Login", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(REGULAR_FONT);
        usernameField = new JTextField(15);
        usernameField.setFont(REGULAR_FONT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(REGULAR_FONT);
        passwordField = new JPasswordField(15);
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Error message label
        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        
        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(errorMessageLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listeners
        loginButton.addActionListener(e -> attemptLogin());
        cancelButton.addActionListener(e -> dispose());
        
        // Add enter key press handling
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        });
        
        panel.add(loginButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (validateCredentials(username, password)) {
            // Successful login
            errorMessageLabel.setText(" ");
            dispose();
            new AdminPanel();
        } else {
            // Failed login
            errorMessageLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }
    
    private boolean validateCredentials(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        String storedPassword = ADMIN_CREDENTIALS.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLoginPanel::new);
    }
}
