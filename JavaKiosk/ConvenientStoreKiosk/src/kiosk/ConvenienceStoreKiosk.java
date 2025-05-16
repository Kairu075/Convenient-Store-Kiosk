package kiosk;

import javax.swing.*;

public class ConvenienceStoreKiosk {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Create and configure the main window
                KioskMainPage mainPage = new KioskMainPage();
                
                // Make sure it starts in full screen
                mainPage.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainPage.setUndecorated(true);
                mainPage.setVisible(true);
                
                // Print debugging information
                System.out.println("Application initialized successfully in full-screen mode");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
