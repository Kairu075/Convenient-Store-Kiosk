package kiosk;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;

public class FoodsAndBeveragesPage extends JFrame {
    private JPanel productPanel;
    private Map<String, List<String>> products;
    private Map<String, List<Double>> prices;
    private JButton snacksButton, readyMealsButton, beveragesButton, frozenFoodsButton;
    private JButton activeButton;

    public FoodsAndBeveragesPage() {
        setTitle("Foods & Beverages");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initProducts();

        // Top Nav
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(1000, 80));
        topPanel.setBackground(new Color(245, 245, 245));

        JButton menuButton = new JButton("☰");
        JTextField searchBar = new JTextField(30);
        JButton helpButton = new JButton("Ask for assistance❓");
        JButton backButton = new JButton("🔙 Back");
        JButton cartButton = new JButton("🛒");

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(menuButton);
        leftPanel.add(searchBar);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(helpButton);
        rightPanel.add(backButton);
        rightPanel.add(cartButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Title and Subcats
        JLabel titleLabel = new JLabel("Foods & Beverages", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.LIGHT_GRAY);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setPreferredSize(new Dimension(1000, 80));

        JPanel subcategoryPanel = new JPanel(new GridLayout(1, 4, 20, 10));
        subcategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        subcategoryPanel.setBackground(new Color(230, 230, 250));

        snacksButton = createSubcategoryButton("Snacks");
        readyMealsButton = createSubcategoryButton("Ready to Eat Meals");
        beveragesButton = createSubcategoryButton("Beverages");
        frozenFoodsButton = createSubcategoryButton("Frozen Foods");

        subcategoryPanel.add(snacksButton);
        subcategoryPanel.add(readyMealsButton);
        subcategoryPanel.add(beveragesButton);
        subcategoryPanel.add(frozenFoodsButton);

        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);       
        headerPanel.add(titleLabel, BorderLayout.CENTER);     
        headerPanel.add(subcategoryPanel, BorderLayout.SOUTH);

        // Product Grid
        productPanel = new JPanel(new GridLayout(5, 3, 20, 20));
        productPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 

        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        
        showProducts("Snacks");
        highlightButton(snacksButton);

        
        backButton.addActionListener(e -> {
            dispose();
            new KioskMainPage();
        });

        // Cart
        cartButton.addActionListener(e -> {
            dispose();
            new CartPage();
        });

        setVisible(true);
    }

    private void initProducts() {
        products = new HashMap<>();
        prices = new HashMap<>();

        products.put("Snacks", Arrays.asList(
            "Piattos", "Chippy", "Roller Coaster", "V-Cut", "Nova", "Tortillos", "Cream-O",
            "Chips Delight", "Choco Mallows", "Rebisco Choco Crackers", "Fudgee Barr", "Stik-O Chocolate", "Cupp Keyk Topps Sarap",
            "Doowee Donut Chocolate", "Chips Ahoy"
        ));
        prices.put("Snacks", Arrays.asList(
            35.1, 25.0, 30.2, 35.0, 32.0, 38.2, 67.0,
            72.0, 38.0, 58.0, 78.0, 156.0, 79.0, 104.0, 150.0
        ));

        products.put("Ready to Eat Meals", Arrays.asList(
            "Hotdog Sandwich", "Siopao", "Cheese Burger", "Sisig with Rice", "BurgerSteak with Rice",
            "Fried Chicken with Rice", "Chocolate Donut", "Choco Butternut Donut", "Bavarian Filled Donut", "Cup Noodles",
            "Pancit Canton", "Buldak", "Waffle", "Pancake", "Hash Brown"
        ));
        prices.put("Ready to Eat Meals", Arrays.asList(
            35.0, 120.0, 85.0, 40.0, 180.0, 90.0, 60.0,
            25.0, 220.0, 30.0, 50.0, 75.0, 85.0, 100.0, 150.0
        ));

        products.put("Beverages", Arrays.asList(
            "Bottled Water", "Soft Drinks", "Juices", "Energy Drinks", "Tea", "Iced Coffee",
            "Milk", "Sports Drinks", "Lemonade", "Coconut Water", "Soda Water", "Flavored Water",
            "Smoothies", "Herbal Tea", "Fruit Punch"
        ));
        prices.put("Beverages", Arrays.asList(
            20.0, 30.0, 40.0, 50.0, 45.0, 60.0, 30.0,
            35.0, 50.0, 40.0, 25.0, 60.0, 80.0, 55.0, 35.0
        ));

        products.put("Frozen Foods", Arrays.asList(
            "Cornetto Classic", "Cornetto Oreo", "Cornetto Chocolate", "Selecta Double Dutch", "Selecta Vanilla",
            "Packed Ice Tubes", "Ice Cups", "Frozen Hotdog", "Frozen Pizza", "Frozen Siomai",
            "Frozen Bacon", "Frozen Ham", "Frozen Nuggets", "Frozen Dumplings", "Frozen Fishball"
        ));
        prices.put("Frozen Foods", Arrays.asList(
            35.0, 42.0, 45.0, 98.0, 99.0, 30.0, 20.0,
            211.0, 170.0, 200.0, 110.0, 130.0, 150.0, 180.0, 109.0
        ));
    }

    private JButton createSubcategoryButton(String name) {
        JButton button = new JButton(name);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(200, 50));

        button.addActionListener(e -> {
            showProducts(name);
            highlightButton(button);
        });

        return button;
    }

    private void showProducts(String category) {
        productPanel.removeAll();

        List<String> items = products.get(category);
        List<Double> itemPrices = prices.get(category);

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            double itemPrice = itemPrices.get(i);

            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
            itemPanel.setBackground(Color.WHITE);

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(100, 100));

            
            String imgPath = null;
            if (itemName.equalsIgnoreCase("Piattos")) {
                imgPath = "src/kiosk/resources/piattos.jpg";
            } else if (itemName.equalsIgnoreCase("Chippy")) {
                imgPath = "src/kiosk/resources/chippy.jpg";
            } else if (itemName.equalsIgnoreCase("Roller Coaster")) {
                imgPath = "src/kiosk/resources/rollercoaster.jpg";
            } else if (itemName.equalsIgnoreCase("V-Cut")) {
                imgPath = "src/kiosk/resources/vcut.jpg";
            } else if (itemName.equalsIgnoreCase("Nova")) {
                imgPath = "src/kiosk/resources/nova.jpg";
            } else if (itemName.equalsIgnoreCase("Tortillos")) {
                imgPath = "src/kiosk/resources/tortillos.jpg";
            } else if (itemName.equalsIgnoreCase("Cream-O")) {
                imgPath = "src/kiosk/resources/creamo.jpg";
            } else if (itemName.equalsIgnoreCase("Chips Delight")) {
                imgPath = "src/kiosk/resources/chipsdelight.jpg";
            } else if (itemName.equalsIgnoreCase("Choco Mallows")) {
                imgPath = "src/kiosk/resources/chocomallows.jpg";
            } else if (itemName.equalsIgnoreCase("Rebisco Choco Crackers")) {
                imgPath = "src/kiosk/resources/rebiscochoco.jpg";
            } else if (itemName.equalsIgnoreCase("Fudgee Barr")) {
                imgPath = "src/kiosk/resources/fudgeebarr.jpg";
            } else if (itemName.equalsIgnoreCase("Stik-O Chocolate")) {
                imgPath = "src/kiosk/resources/stiko.jpg";
            } else if (itemName.equalsIgnoreCase("Cupp Keyk Topps Sarap")) {
                imgPath = "src/kiosk/resources/cuppkeyktoppssarap.jpg";
            } else if (itemName.equalsIgnoreCase("Doowee Donut Chocolate")) {
                imgPath = "src/kiosk/resources/doowee.jpg";
            }else if (itemName.equalsIgnoreCase("Chips Ahoy")) {
                imgPath = "src/kiosk/resources/chipsahoy.jpg";
            }

            if (imgPath != null && new File(imgPath).exists()) {
                ImageIcon icon = new ImageIcon(imgPath);
                imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            } else {
                imageLabel.setText("No image");
            }

            JLabel nameLabel = new JLabel(itemName, SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", itemPrice), SwingConstants.CENTER);
            priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            priceLabel.setForeground(Color.DARK_GRAY);

            JPanel quantityPanel = new JPanel(new FlowLayout());
            JLabel quantityLabel = new JLabel("0");
            quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JButton plusButton = new JButton("+");
            JButton minusButton = new JButton("-");

            plusButton.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityLabel.getText());
                quantity++;
                quantityLabel.setText(String.valueOf(quantity));
                CartManager.addItem(itemName, itemPrice, quantity);
            });

            minusButton.addActionListener(e -> {
                int quantity = Integer.parseInt(quantityLabel.getText());
                if (quantity > 0) {
                    quantity--;
                    quantityLabel.setText(String.valueOf(quantity));
                    CartManager.addItem(itemName, itemPrice, quantity);
                }
            });

            quantityPanel.add(minusButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(plusButton);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(priceLabel, BorderLayout.NORTH);
            bottomPanel.add(quantityPanel, BorderLayout.SOUTH);

            itemPanel.add(imageLabel, BorderLayout.NORTH);
            itemPanel.add(nameLabel, BorderLayout.CENTER);
            itemPanel.add(bottomPanel, BorderLayout.SOUTH);

            productPanel.add(itemPanel);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }


    private void highlightButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(Color.WHITE);
        }
        button.setBackground(new Color(200, 200, 255));
        activeButton = button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FoodsAndBeveragesPage::new);
    }
}
