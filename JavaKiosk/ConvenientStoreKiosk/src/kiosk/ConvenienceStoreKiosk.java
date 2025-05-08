package kiosk;

import javax.swing.*;

public class ConvenienceStoreKiosk {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Launch the main page directly
            new KioskMainPage();
            
            // Print debugging information
            System.out.println("Application initialized successfully");
        });
    }
}
