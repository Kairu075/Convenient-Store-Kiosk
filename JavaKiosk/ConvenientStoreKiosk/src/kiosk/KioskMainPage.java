package kiosk;

import javax.swing.*;
import java.awt.*;


public class KioskMainPage extends JFrame {

    public KioskMainPage() {
        setTitle("Convenient Store Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        //  (Menu Button + Search Bar + Help Button + Home and Cart Button)
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton menuButton = new JButton("☰");
        JTextField searchBar = new JTextField(30);
        JButton helpButton = new JButton("❓ Help");
        JButton homeButton = new JButton("🏠");
        JButton cartButton = new JButton("🛒");

        // Left Panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(menuButton);
        leftPanel.add(searchBar);

        // Right Panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(helpButton);
        rightPanel.add(homeButton);
        rightPanel.add(cartButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Title
        JLabel titleLabel = new JLabel("Convenient Store Kiosk", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.LIGHT_GRAY);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setPreferredSize(new Dimension(1000, 100));

        // Categories Panel
        JPanel categoriesPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton foodsButton = createCategoryButton("Foods & Beverages", "foods.png");
        JButton alcoholButton = createCategoryButton("Tobacco & Alcohol (18+)", "alcohol.png");
        JButton personalCareButton = createCategoryButton("Personal Care & Hygienes", "personal_care.png");
        JButton householdButton = createCategoryButton("Household Essentials", "household.png");

        categoriesPanel.add(foodsButton);
        categoriesPanel.add(alcoholButton);
        categoriesPanel.add(personalCareButton);
        categoriesPanel.add(householdButton);

        // Main Frame 
        add(topPanel, BorderLayout.NORTH);
        add(titleLabel, BorderLayout.CENTER);
        add(categoriesPanel, BorderLayout.SOUTH);

        // Button actions
        foodsButton.addActionListener(e -> {
            dispose();
            new FoodsAndBeveragesPage();
        });

        homeButton.addActionListener(e -> {
            dispose();
            new KioskMainPage();
        });

        setVisible(true);
    }

    private JButton createCategoryButton(String text, String imagePath) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/kiosk/resources/" + imagePath));
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Error: Image " + imagePath + " not found.");
            return new JButton(text);
        }

        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);

        JButton button = new JButton("<html><center>" + text + "</center></html>", icon);
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(200, 200));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        return button;
    }

    public static void main(String[] args) {
        new KioskMainPage();
    }
}


