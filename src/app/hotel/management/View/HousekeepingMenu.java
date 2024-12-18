package app.hotel.management.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingMenu extends JFrame {

    private final String hotelName;
    private final int userID; // Housekeeper's User ID
    private int staffID; // Housekeeper's Staff ID
    private Connection connection;

    public HousekeepingMenu(String hotelName, int userID) {
        this.hotelName = hotelName;
        this.userID = userID;

        connectToDatabase();
        fetchStaffID(); // Retrieve staff ID for the logged-in user

        setTitle("Housekeeping Menu - " + hotelName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Buttons
        String[] buttonNames = {
                "View My Pending Tasks", "View My Completed Tasks", "Mark Task as Completed", "Logout"
        };

        int yStart = 50, buttonHeight = 40, yGap = 15, xStart = 200, buttonWidth = 300;

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            button.setBounds(xStart, yStart + i * (buttonHeight + yGap), buttonWidth, buttonHeight);
            attachButtonAction(button, buttonNames[i]);
            add(button);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "Tny_0102032003";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchStaffID() {
        try {
            String query = "SELECT Staff_ID FROM Staff WHERE User_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                staffID = rs.getInt("Staff_ID");
            } else {
                throw new SQLException("Staff ID not found for the given User ID.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving staff information: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose(); // Close the menu if staff ID cannot be retrieved
        }
    }

    private void attachButtonAction(JButton button, String actionName) {
        switch (actionName) {
            case "View My Pending Tasks" -> button.addActionListener(e -> viewPendingTasks());
            case "View My Completed Tasks" -> button.addActionListener(e -> viewCompletedTasks());
            case "Mark Task as Completed" -> button.addActionListener(e -> markTaskAsCompleted());
            case "Logout" -> button.addActionListener(e -> {
                dispose();
                new UserLoginMenu();
            });
        }
    }

    private void viewPendingTasks() {
        fetchAndDisplayTasks("Pending", "My Pending Tasks");
    }

    private void viewCompletedTasks() {
        fetchAndDisplayTasks("Completed", "My Completed Tasks");
    }

    private void fetchAndDisplayTasks(String taskStatus, String title) {
        try {
            // Query to fetch tasks with the specified status for the logged-in housekeeper
            String query = """
                SELECT Task_ID, Room_ID, Schedule, Task_Status
                FROM Housekeeping
                WHERE Housekeeper_Staff_ID = ? AND Task_Status = ?
            """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, staffID);
            stmt.setString(2, taskStatus);
            ResultSet rs = stmt.executeQuery();

            // Define column names for the table
            String[] columnNames = {"Task ID", "Room ID", "Schedule", "Task Status"};
            List<Object[]> dataList = new ArrayList<>();

            while (rs.next()) {
                int taskID = rs.getInt("Task_ID");
                int roomID = rs.getInt("Room_ID");
                Date schedule = rs.getDate("Schedule");

                dataList.add(new Object[]{
                        taskID,
                        roomID,
                        schedule != null ? schedule.toString() : "N/A",
                        taskStatus
                });
            }

            // Handle no records found
            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No " + taskStatus.toLowerCase() + " tasks found.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to a 2D array
            Object[][] data = new Object[dataList.size()][columnNames.length];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Display the data in a JTable
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable editing
            JScrollPane scrollPane = new JScrollPane(table);

            JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching tasks: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markTaskAsCompleted() {
        try {
            // Fetch the pending tasks for the logged-in housekeeper
            String query = """
                SELECT Task_ID, Room_ID
                FROM Housekeeping
                WHERE Housekeeper_Staff_ID = ? AND Task_Status = 'Pending'
            """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, staffID);
            ResultSet rs = stmt.executeQuery();

            DefaultComboBoxModel<String> taskModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int taskID = rs.getInt("Task_ID");
                int roomID = rs.getInt("Room_ID");
                taskModel.addElement("Task ID: " + taskID + ", Room ID: " + roomID);
            }

            if (taskModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No pending tasks available to mark as completed.",
                        "No Tasks", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> taskDropdown = new JComboBox<>(taskModel);

            int result = JOptionPane.showConfirmDialog(this, taskDropdown, "Select Task to Complete",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract Task_ID from the selected item
                String selectedTask = (String) taskDropdown.getSelectedItem();
                int taskID = Integer.parseInt(selectedTask.split(",")[0].split(":")[1].trim());

                // Update the task status to 'Completed'
                String updateQuery = "UPDATE Housekeeping SET Task_Status = 'Completed' WHERE Task_ID = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, taskID);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Task marked as completed successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating task status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}