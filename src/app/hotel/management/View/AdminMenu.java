package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;

class AdminMenu extends JFrame {

    public AdminMenu() {
        setTitle("Administrator Menu");
        setSize(600, 500);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setResizable(false);
        setLayout(null);

        // Create buttons for menu options
        JButton addRoomButton = new JButton("Add Room");
        JButton deleteRoomButton = new JButton("Delete Room");
        JButton manageRoomButton = new JButton("Manage Room Status");
        JButton addUserButton = new JButton("Add User Account");
        JButton viewUsersButton = new JButton("View User Accounts");
        JButton revenueReportButton = new JButton("Generate Revenue Report");
        JButton viewBookingsButton = new JButton("View All Booking Records");
        JButton viewHousekeepingButton = new JButton("View All Housekeeping Records");
        JButton mostBookedRoomButton = new JButton("View Most Booked Room Types");
        JButton viewEmployeesButton = new JButton("View All Employees with Their Role");
        JButton exitButton = new JButton("Exit");

        // Set bounds for buttons (grid-like placement)
        int xStart = 50, yStart = 50, buttonWidth = 200, buttonHeight = 30, yGap = 10;
        addRoomButton.setBounds(xStart, yStart, buttonWidth, buttonHeight);
        deleteRoomButton.setBounds(xStart, yStart + (buttonHeight + yGap), buttonWidth, buttonHeight);
        manageRoomButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 2, buttonWidth, buttonHeight);
        addUserButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 3, buttonWidth, buttonHeight);
        viewUsersButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 4, buttonWidth, buttonHeight);

        revenueReportButton.setBounds(xStart + 250, yStart, buttonWidth, buttonHeight);
        viewBookingsButton.setBounds(xStart + 250, yStart + (buttonHeight + yGap), buttonWidth, buttonHeight);
        viewHousekeepingButton.setBounds(xStart + 250, yStart + (buttonHeight + yGap) * 2, buttonWidth, buttonHeight);
        mostBookedRoomButton.setBounds(xStart + 250, yStart + (buttonHeight + yGap) * 3, buttonWidth, buttonHeight);
        viewEmployeesButton.setBounds(xStart + 250, yStart + (buttonHeight + yGap) * 4, buttonWidth, buttonHeight);

        exitButton.setBounds(200, 400, 200, 30);

        // Add buttons to the frame
        add(addRoomButton);
        add(deleteRoomButton);
        add(manageRoomButton);
        add(addUserButton);
        add(viewUsersButton);
        add(revenueReportButton);
        add(viewBookingsButton);
        add(viewHousekeepingButton);
        add(mostBookedRoomButton);
        add(viewEmployeesButton);
        add(exitButton);

        // Add action listeners to buttons (to be implemented as needed)
        addRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Room clicked"));
        deleteRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Delete Room clicked"));
        manageRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Manage Room Status clicked"));
        addUserButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add User Account clicked"));
        viewUsersButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View User Accounts clicked"));
        revenueReportButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Generate Revenue Report clicked"));
        viewBookingsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View All Booking Records clicked"));
        viewHousekeepingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View All Housekeeping Records clicked"));
        mostBookedRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Most Booked Room Types clicked"));
        viewEmployeesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View All Employees clicked"));

        exitButton.addActionListener(e -> {
            dispose(); // Close Admin Menu
            new UserLoginMenu(); // Return to Login Menu
        });


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}