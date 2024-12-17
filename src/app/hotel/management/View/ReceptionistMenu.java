package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ReceptionistMenu extends JFrame {

    public ReceptionistMenu(String hotelName) {
        setTitle("Receptionist Menu - " + hotelName);
        setSize(500, 500);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);
        setLayout(null);

        // Create buttons for menu options
        JButton addBookingButton = new JButton("Add New Booking");
        JButton modifyBookingButton = new JButton("Modify Booking");
        JButton deleteBookingButton = new JButton("Delete Booking");
        JButton viewBookingsButton = new JButton("View Bookings");
        JButton processPaymentButton = new JButton("Process Payment");
        JButton assignTaskButton = new JButton("Assign Housekeeping Task");
        JButton viewTasksButton = new JButton("View Housekeeping Records");
        JButton logoutButton = new JButton("Logout");

        // Set button bounds (grid-like layout)
        int xStart = 150, yStart = 50, buttonWidth = 200, buttonHeight = 30, yGap = 15;

        addBookingButton.setBounds(xStart, yStart, buttonWidth, buttonHeight);
        modifyBookingButton.setBounds(xStart, yStart + (buttonHeight + yGap), buttonWidth, buttonHeight);
        deleteBookingButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 2, buttonWidth, buttonHeight);
        viewBookingsButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 3, buttonWidth, buttonHeight);
        processPaymentButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 4, buttonWidth, buttonHeight);
        assignTaskButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 5, buttonWidth, buttonHeight);
        viewTasksButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 6, buttonWidth, buttonHeight);
        logoutButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 7, buttonWidth, buttonHeight);

        // Add buttons to the frame
        add(addBookingButton);
        add(modifyBookingButton);
        add(deleteBookingButton);
        add(viewBookingsButton);
        add(processPaymentButton);
        add(assignTaskButton);
        add(viewTasksButton);
        add(logoutButton);

        // Add action listeners for buttons
        addBookingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add New Booking clicked"));
        modifyBookingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Modify Booking clicked"));
        deleteBookingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Delete Booking clicked"));
        viewBookingsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Bookings clicked"));
        processPaymentButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Process Payment clicked"));
        assignTaskButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Assign Housekeeping Task clicked"));
        viewTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Housekeeping Records clicked"));

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close Receptionist Menu
                new UserLoginMenu(); // Return to Login Menu
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}