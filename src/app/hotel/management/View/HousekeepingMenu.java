package app.hotel.management.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class HousekeepingMenu extends JFrame {

    private String hotelName;
    private int hotelID;
    private Connection connection;

    public HousekeepingMenu(String hotelName) {
        this.hotelName = hotelName;
        connectToDatabase();
        fetchHotelID();

        setTitle("Housekeeping Menu - " + hotelName);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Create buttons for menu options
        JButton viewPendingTasksButton = new JButton("View Pending Tasks");
        JButton viewCompletedTasksButton = new JButton("View Completed Tasks");
        JButton updateTaskStatusButton = new JButton("Update Task Status");
        JButton viewScheduleButton = new JButton("View Cleaning Schedule");
        JButton logoutButton = new JButton("Logout");

        // Set button bounds
        int xStart = 150, yStart = 50, buttonWidth = 300, buttonHeight = 30, yGap = 20;

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

        // Add action listeners
        viewPendingTasksButton.addActionListener(e -> viewTasks("Pending"));
        viewCompletedTasksButton.addActionListener(e -> viewTasks("Completed"));
        updateTaskStatusButton.addActionListener(e -> updateTaskStatus());
        viewScheduleButton.addActionListener(e -> viewSchedule());

        logoutButton.addActionListener(e -> {
            dispose(); // Close Housekeeping Menu
            new UserLoginMenu(); // Return to Login Menu
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "password"; // Replace with your credentials
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchHotelID() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT Hotel_ID FROM Hotel WHERE Hotel_Name = ?")) {
            stmt.setString(1, hotelName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) hotelID = rs.getInt("Hotel_ID");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching hotel ID: " + ex.getMessage());
        }
    }

    private void viewTasks(String status) {
        try {
            String query = "SELECT Task_ID, Room_ID, Schedule, Task_Status FROM Housekeeping WHERE Task_Status = ? AND Room_ID IN " +
                    "(SELECT Room_ID FROM Room WHERE Hotel_ID = ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, hotelID);

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new String[]{"Task ID", "Room ID", "Schedule", "Task_Status"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Task_ID"),
                        rs.getInt("Room_ID"),
                        rs.getDate("Schedule"),
                        rs.getString("Task_Status")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table), status + " Tasks", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error viewing tasks: " + ex.getMessage());
        }
    }

    private void updateTaskStatus() {
        JTextField taskIDField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Completed"});

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Task ID:"));
        panel.add(taskIDField);
        panel.add(new JLabel("New Task_Status:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Task Task_Status", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement stmt = connection.prepareStatement("UPDATE Housekeeping SET Task_Status = ? WHERE Task_ID = ?");
                stmt.setString(1, (String) statusBox.getSelectedItem());
                stmt.setInt(2, Integer.parseInt(taskIDField.getText()));
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Task status updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Task ID not found.");
                }
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewSchedule() {
        try {
            String query = "SELECT Room_ID, Schedule, Task_Status FROM Housekeeping WHERE Room_ID IN " +
                    "(SELECT Room_ID FROM Room WHERE Hotel_ID = ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, hotelID);

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new String[]{"Room ID", "Schedule", "Task_Status"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Room_ID"),
                        rs.getDate("Schedule"),
                        rs.getString("Task_Status")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Cleaning Schedule", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error viewing schedule: " + ex.getMessage());
        }
    }
}
