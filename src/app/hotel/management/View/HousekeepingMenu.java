package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class HousekeepingMenu extends JFrame {

    public HousekeepingMenu(String hotelName) {
        setTitle("Housekeeping Menu - " + hotelName);
        setSize(500, 400);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);
        setLayout(null);

        // Create buttons for menu options
        JButton viewPendingTasksButton = new JButton("View Pending Tasks");
        JButton viewCompletedTasksButton = new JButton("View Completed Tasks");
        JButton updateTaskStatusButton = new JButton("Update Task Status");
        JButton viewScheduleButton = new JButton("View Cleaning Schedule");
        JButton logoutButton = new JButton("Logout");

        // Set button bounds (grid-like layout)
        int xStart = 150, yStart = 50, buttonWidth = 200, buttonHeight = 30, yGap = 20;

        viewPendingTasksButton.setBounds(xStart, yStart, buttonWidth, buttonHeight);
        viewCompletedTasksButton.setBounds(xStart, yStart + (buttonHeight + yGap), buttonWidth, buttonHeight);
        updateTaskStatusButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 2, buttonWidth, buttonHeight);
        viewScheduleButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 3, buttonWidth, buttonHeight);
        logoutButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 4, buttonWidth, buttonHeight);

        // Add buttons to the frame
        add(viewPendingTasksButton);
        add(viewCompletedTasksButton);
        add(updateTaskStatusButton);
        add(viewScheduleButton);
        add(logoutButton);

        // Add action listeners for buttons
        viewPendingTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Pending Tasks clicked"));
        viewCompletedTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Completed Tasks clicked"));
        updateTaskStatusButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update Task Status clicked"));
        viewScheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Cleaning Schedule clicked"));

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close Housekeeping Menu
                new UserLoginMenu(); // Return to Login Menu
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}