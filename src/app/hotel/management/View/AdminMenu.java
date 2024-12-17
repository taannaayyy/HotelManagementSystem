package app.hotel.management.View;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

class AdminMenu extends JFrame {

    private String hotelName;

    public AdminMenu(String hotelName) {
        this.hotelName = hotelName; // Hotel name passed from login

        setTitle("Administrator Menu - " + hotelName);
        setSize(600, 500);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);
        setLayout(null);

        // Button names and action messages
        String[] buttonNames = {
                "Add Room", "Delete Room", "Manage Room Status",
                "Add User Account", "View User Accounts", "Generate Revenue Report",
                "View All Booking Records", "View All Housekeeping Records",
                "View Most Booked Room Types", "View All Employees with Their Role"
        };

        String[] buttonMessages = {
                "Add Room clicked", "Delete Room clicked", "Manage Room Status clicked",
                "Add User Account clicked", "View User Accounts clicked", "Generate Revenue Report clicked",
                "View All Booking Records clicked", "View All Housekeeping Records clicked",
                "View Most Booked Room Types clicked", "View All Employees clicked"
        };

        // Create and position buttons dynamically
        Map<JButton, String> buttonActionMap = new HashMap<>();
        int xStart = 50, yStart = 50, buttonWidth = 200, buttonHeight = 30, yGap = 10;

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            int row = i % 5; // Row position
            int col = i / 5; // Column position

            button.setBounds(xStart + (col * 250), yStart + (row * (buttonHeight + yGap)), buttonWidth, buttonHeight);
            add(button);

            // Map button to its message for action listener
            buttonActionMap.put(button, buttonMessages[i]);
        }

        // Exit button
        JButton exitButton = new JButton("Logout");
        exitButton.setBounds(200, 400, 200, 30);
        add(exitButton);

        // Add action listeners to buttons
        buttonActionMap.forEach((button, message) ->
                button.addActionListener(e -> JOptionPane.showMessageDialog(this, message))
        );

        exitButton.addActionListener(e -> {
            dispose(); // Close Admin Menu
            new UserLoginMenu(); // Return to Login Menu
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}