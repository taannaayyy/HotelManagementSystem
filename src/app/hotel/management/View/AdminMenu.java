package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Objects;

class AdminMenu extends JFrame {

    private String hotelName;
    private int hotelID; // To store Hotel ID for adding rooms
    private Connection connection;

    public AdminMenu(String hotelName) {
        this.hotelName = hotelName;
        connectToDatabase();
        fetchHotelID();

        setTitle("Administrator Menu - " + hotelName);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Button names
        String[] buttonNames = {
                "Add Room", "Delete Room", "Manage Room Status",
                "Add User Account", "View User Accounts", "Generate Revenue Report",
                "View All Booking Records", "View All Housekeeping Records",
                "View Most Booked Room Types", "View All Employees with Their Role"
        };

        // Create buttons dynamically
        int xStart = 50, yStart = 50, buttonWidth = 250, buttonHeight = 30, yGap = 10;

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            int row = i % 5; // Row position
            int col = i / 5; // Column position

            button.setBounds(xStart + (col * 300), yStart + (row * (buttonHeight + yGap)), buttonWidth, buttonHeight);
            add(button);

            // Attach button actions
            attachButtonAction(button, buttonNames[i]);
        }

        // Logout button
        JButton exitButton = new JButton("Logout");
        exitButton.setBounds(300, 400, 200, buttonHeight);
        exitButton.addActionListener(e -> {
            dispose();
            new UserLoginMenu();
        });
        add(exitButton);

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

    private void fetchHotelID() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT Hotel_ID FROM Hotel WHERE Hotel_Name = ?")) {
            stmt.setString(1, hotelName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hotelID = rs.getInt("Hotel_ID");
            } else {
                JOptionPane.showMessageDialog(this, "Hotel not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching hotel ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachButtonAction(JButton button, String actionName) {
        switch (actionName) {
            case "Add Room" -> button.addActionListener(e -> showAddRoomForm());
            case "Delete Room" -> button.addActionListener(e -> showDeleteRoomForm());
            case "Manage Room Status" -> button.addActionListener(e -> showManageRoomStatusForm());
            case "Add User Account" -> button.addActionListener(e -> showAddUserForm());
            case "View User Accounts" -> button.addActionListener(e -> viewUserAccounts());
            case "Generate Revenue Report" -> button.addActionListener(e -> generateRevenueReport());
            case "View All Booking Records" -> button.addActionListener(e -> viewBookingRecords());
            case "View All Housekeeping Records" -> button.addActionListener(e -> viewHousekeepingRecords());
            case "View Most Booked Room Types" -> button.addActionListener(e -> viewMostBookedRoomTypes());
            case "View All Employees with Their Role" -> button.addActionListener(e -> viewEmployeesWithRoles());
        }
    }

    private void showAddRoomForm() {
        JComboBox<String> roomTypeBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        JTextField roomPriceField = new JTextField();
        JComboBox<String> roomStatusBox = new JComboBox<>(new String[]{"Available", "Occupied", "Cleaning", "Maintenance"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeBox);
        panel.add(new JLabel("Room Price:"));
        panel.add(roomPriceField);
        panel.add(new JLabel("Room Status:"));
        panel.add(roomStatusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Room", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double roomPrice = Double.parseDouble(roomPriceField.getText());
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO room (Room_Type, Room_Price, Room_Status, Hotel_ID) VALUES (?, ?, ?, ?)");
                stmt.setString(1, (String) roomTypeBox.getSelectedItem());
                stmt.setDouble(2, roomPrice);
                stmt.setString(3, (String) roomStatusBox.getSelectedItem());
                stmt.setInt(4, hotelID);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Room added successfully!");
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteRoomForm() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT Room_ID, Room_Type FROM room WHERE Hotel_ID = ?");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            DefaultComboBoxModel<String> roomModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                roomModel.addElement("ID: " + rs.getInt("Room_ID") + " | Type: " + rs.getString("Room_Type"));
            }

            if (roomModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No rooms to delete.");
                return;
            }

            JComboBox<String> roomComboBox = new JComboBox<>(roomModel);
            int result = JOptionPane.showConfirmDialog(this, roomComboBox, "Select Room to Delete", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                int roomID = Integer.parseInt(((String) Objects.requireNonNull(roomComboBox.getSelectedItem())).split("\\|")[0].split(":")[1].trim());
                PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM room WHERE Room_ID = ?");
                deleteStmt.setInt(1, roomID);
                deleteStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showManageRoomStatusForm() {
        try {
            // Fetch rooms for the current hotel
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT Room_ID, Room_Type, Room_Status FROM room WHERE Hotel_ID = ?");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Populate the dropdown menu with rooms
            DefaultComboBoxModel<String> roomModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int roomID = rs.getInt("Room_ID");
                String roomType = rs.getString("Room_Type");
                String roomStatus = rs.getString("Room_Status");
                roomModel.addElement("ID: " + roomID + " | Type: " + roomType + " | Status: " + roomStatus);
            }

            if (roomModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No rooms available to manage.", "No Rooms", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> roomComboBox = new JComboBox<>(roomModel);

            // Dropdown for selecting the new status
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Available", "Occupied", "Cleaning", "Maintenance"});

            // Create a panel to display options
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Select Room:"));
            panel.add(roomComboBox);
            panel.add(new JLabel("Set New Status:"));
            panel.add(statusComboBox);

            // Show dialog
            int result = JOptionPane.showConfirmDialog(this, panel, "Manage Room Status", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract room ID from the selected item
                String selectedRoom = (String) roomComboBox.getSelectedItem();
                assert selectedRoom != null;
                int roomID = Integer.parseInt(selectedRoom.split("\\|")[0].split(":")[1].trim());

                // Get the selected new status
                String newStatus = (String) statusComboBox.getSelectedItem();

                // Update room status in the database
                PreparedStatement updateStmt = connection.prepareStatement(
                        "UPDATE room SET Room_Status = ? WHERE Room_ID = ?");
                updateStmt.setString(1, newStatus);
                updateStmt.setInt(2, roomID);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room status updated successfully to: " + newStatus);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error managing room status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserForm() {
        // Step 1: Input fields for user account
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Guest", "Administrator", "Receptionist", "Housekeeper"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String role = (String) roleBox.getSelectedItem();

                if (username.isEmpty() || password.isEmpty() || role == null) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Step 2: Insert into user_account
                String insertUserSQL = "INSERT INTO user_account (Username, Password_Hash, Hotel_ID, Role) VALUES (?, ?, ?, ?)";
                PreparedStatement userStmt = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username);
                userStmt.setString(2, password); // Optionally hash the password
                userStmt.setInt(3, hotelID);
                userStmt.setString(4, role.toLowerCase());
                userStmt.executeUpdate();

                // Step 3: Get generated User_ID
                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                int userID = -1;
                if (generatedKeys.next()) {
                    userID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user account failed, no User_ID generated.");
                }

                // Step 4: Handle additional details based on role
                if (role.equalsIgnoreCase("Guest")) {
                    showAddGuestDetails(userID);
                } else {
                    showAddStaffDetails(userID, role); // Redirect to the staff details form
                }

                JOptionPane.showMessageDialog(this, "User account created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddStaffDetails(int userID, String role) throws SQLException {
        JTextField staffNameField = new JTextField();
        JTextField salaryField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Staff Name:"));
        panel.add(staffNameField);
        panel.add(new JLabel("Staff Salary:"));
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Staff Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String staffName = staffNameField.getText().trim();
            double salary;

            try {
                salary = Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException e) {
                throw new SQLException("Invalid salary input.");
            }

            if (staffName.isEmpty() || salary <= 0) {
                throw new SQLException("Staff details are required.");
            }

            // Step 1: Insert into staff table
            String insertStaffSQL = "INSERT INTO staff (User_ID, Staff_Name, Staff_Type, Staff_Salary, Hotel_ID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement staffStmt = connection.prepareStatement(insertStaffSQL);
            staffStmt.setInt(1, userID);
            staffStmt.setString(2, staffName);
            staffStmt.setString(3, role);
            staffStmt.setDouble(4, salary);
            staffStmt.setInt(5, hotelID);
            staffStmt.executeUpdate();

            // Step 2: Redirect to role-specific form
            if (role.equalsIgnoreCase("Administrator")) {
                showAddAdminDetails(userID);
            } else if (role.equalsIgnoreCase("Receptionist")) {
                showAddReceptionistDetails(userID);
            } else if (role.equalsIgnoreCase("Housekeeper")) {
                showAddHousekeeperDetails(userID);
            }
        }
    }
    private void showAddGuestDetails(int userID) throws SQLException {
        JTextField guestNameField = new JTextField();
        JTextField contactInfoField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Guest Name:"));
        panel.add(guestNameField);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactInfoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Guest Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String guestName = guestNameField.getText().trim();
            String contactInfo = contactInfoField.getText().trim();

            if (guestName.isEmpty() || contactInfo.isEmpty()) {
                throw new SQLException("Guest details are required.");
            }

            // Insert into guest table
            String insertGuestSQL = "INSERT INTO guest (User_ID, Guest_Name, Guest_ContactInfo) VALUES (?, ?, ?)";
            PreparedStatement guestStmt = connection.prepareStatement(insertGuestSQL);
            guestStmt.setInt(1, userID);
            guestStmt.setString(2, guestName);
            guestStmt.setString(3, contactInfo);
            guestStmt.executeUpdate();
        }
    }

    private void showAddAdminDetails(int userID) throws SQLException {
        // Step 1: Fetch the Staff_ID associated with the User_ID
        int staffID = fetchStaffID(userID);

        // Step 2: Insert into Administrator table
        String insertAdminSQL = "INSERT INTO Administrator (Staff_ID) VALUES (?)";
        PreparedStatement adminStmt = connection.prepareStatement(insertAdminSQL);
        adminStmt.setInt(1, staffID);
        adminStmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Administrator added successfully with Staff ID: " + staffID,
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddReceptionistDetails(int userID) throws SQLException {
        // Step 1: Fetch the Staff_ID associated with the User_ID
        int staffID = fetchStaffID(userID);

        // Step 2: Collect Receptionist-specific details
        JTextField shiftField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Shift:"));
        panel.add(shiftField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Receptionist Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String shift = shiftField.getText().trim();

            if (shift.isEmpty()) {
                throw new SQLException("Receptionist shift details are required.");
            }

            // Step 3: Insert into Receptionist table
            String insertReceptionistSQL = "INSERT INTO Receptionist (Staff_ID, Reception_Shift) VALUES (?, ?)";
            PreparedStatement receptionistStmt = connection.prepareStatement(insertReceptionistSQL);
            receptionistStmt.setInt(1, staffID);
            receptionistStmt.setString(2, shift);
            receptionistStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Receptionist added successfully with Staff ID: " + staffID,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddHousekeeperDetails(int userID) throws SQLException {
        JTextField shiftField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Shift:"));
        panel.add(shiftField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Housekeeper Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String shift = shiftField.getText().trim();

            if (shift.isEmpty()) {
                throw new SQLException("Housekeeper shift details are required.");
            }

            // Step 1: Fetch the Staff_ID associated with the User_ID
            int staffID = fetchStaffID(userID);

            // Step 2: Insert into housekeeper table
            String insertHousekeeperSQL = "INSERT INTO housekeeper (Staff_ID, Shift) VALUES (?, ?)";
            PreparedStatement housekeeperStmt = connection.prepareStatement(insertHousekeeperSQL);
            housekeeperStmt.setInt(1, staffID);
            housekeeperStmt.setString(2, shift);
            housekeeperStmt.executeUpdate();
        }
    }

    private int fetchStaffID(int userID) throws SQLException {
        String fetchStaffIDSQL = "SELECT Staff_ID FROM staff WHERE User_ID = ?";
        PreparedStatement stmt = connection.prepareStatement(fetchStaffIDSQL);
        stmt.setInt(1, userID);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("Staff_ID");
        } else {
            throw new SQLException("Staff ID not found for the given User ID.");
        }
    }
    private void viewUserAccounts() {
        try {
            // Fetch user accounts for the current hotel
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT User_ID, Username, Role FROM user_account WHERE Hotel_ID = ?");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Define column names for the table
            String[] columnNames = {"User ID", "Username", "Role"};

            // Build data for the table
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();
            while (rs.next()) {
                int userID = rs.getInt("User_ID");
                String username = rs.getString("Username");
                String role = rs.getString("Role");
                dataList.add(new Object[]{userID, username, role});
            }

            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No user accounts found for this hotel.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to array for JTable
            Object[][] data = new Object[dataList.size()][3];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Create JTable and display it in a scrollable pane
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable editing
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "View User Accounts", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching user accounts: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateRevenueReport() {
        try {
            // Query to calculate total revenue for 'Completed' reservations only
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT SUM(rm.Room_Price) AS Total_Revenue " +
                            "FROM reservation r " +
                            "JOIN room rm ON r.Room_ID = rm.Room_ID " +
                            "WHERE rm.Hotel_ID = ? AND r.Booking_Status = 'Confirmed'");
            stmt.setInt(1, hotelID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double totalRevenue = rs.getDouble("Total_Revenue");

                if (rs.wasNull()) { // Handle no revenue case
                    JOptionPane.showMessageDialog(this, "No completed reservations found for this hotel.",
                            "Revenue Report", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display the total revenue for completed reservations
                    JOptionPane.showMessageDialog(this,
                            "Total Revenue for Completed Rooms: $" + String.format("%.2f", totalRevenue),
                            "Revenue Report", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating revenue report: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewBookingRecords() {
        try {
            // Fetch all booking records for the current hotel
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT r.Reservation_ID, r.Room_ID, r.Guest_ID, r.CheckInDate, r.CheckOutDate, r.Booking_Status " +
                            "FROM reservation r " +
                            "JOIN room rm ON r.Room_ID = rm.Room_ID " +
                            "WHERE rm.Hotel_ID = ?");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Define table column names
            String[] columnNames = {"Reservation ID", "Room ID", "Guest ID", "Check-in Date", "Check-out Date", "Status"};

            // Build data for the table
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();
            while (rs.next()) {
                int reservationID = rs.getInt("Reservation_ID");
                int roomID = rs.getInt("Room_ID");
                int guestID = rs.getInt("Guest_ID");
                String checkIn = rs.getString("CheckInDate");
                String checkOut = rs.getString("CheckOutDate");
                String status = rs.getString("Booking_Status");

                dataList.add(new Object[]{reservationID, roomID, guestID, checkIn, checkOut, status});
            }

            // Handle no records found
            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No booking records found for this hotel.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to array for JTable
            Object[][] data = new Object[dataList.size()][6];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Create JTable and display it in a scrollable pane
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable table editing
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "View All Booking Records", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching booking records: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewHousekeepingRecords() {
        try {
            // Query to fetch all housekeeping tasks
            String query = """
            SELECT Task_ID, Housekeeper_Staff_ID, Room_ID, Schedule, Task_Status
            FROM Housekeeping
        """;

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Define table column names
            String[] columnNames = {"Task ID", "Housekeeper Staff ID", "Room ID", "Schedule", "Task Status"};

            // Build data for the table
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();
            while (rs.next()) {
                int taskID = rs.getInt("Task_ID");
                int housekeeperStaffID = rs.getInt("Housekeeper_Staff_ID");
                int roomID = rs.getInt("Room_ID");
                Date schedule = rs.getDate("Schedule");
                String taskStatus = rs.getString("Task_Status");

                dataList.add(new Object[]{
                        taskID,
                        housekeeperStaffID != 0 ? housekeeperStaffID : "N/A", // Handle NULL Housekeeper_Staff_ID
                        roomID,
                        schedule != null ? schedule.toString() : "N/A",
                        taskStatus
                });
            }

            // Handle no records found
            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No housekeeping records found.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to array for JTable
            Object[][] data = new Object[dataList.size()][columnNames.length];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Create JTable and display it in a scrollable pane
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable editing
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "Housekeeping Records", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching housekeeping records: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMostBookedRoomTypes() {
        try {
            // Query to count bookings for each room type in the current hotel
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT rm.Room_Type, COUNT(r.Reservation_ID) AS Booking_Count " +
                            "FROM reservation r " +
                            "JOIN room rm ON r.Room_ID = rm.Room_ID " +
                            "WHERE rm.Hotel_ID = ? AND r.Booking_Status = 'Confirmed' " +
                            "GROUP BY rm.Room_Type " +
                            "ORDER BY Booking_Count DESC");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Define table column names
            String[] columnNames = {"Room Type", "Number of Bookings"};

            // Build data for the table
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();
            while (rs.next()) {
                String roomType = rs.getString("Room_Type");
                int bookingCount = rs.getInt("Booking_Count");
                dataList.add(new Object[]{roomType, bookingCount});
            }

            // Handle no bookings found
            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No booking data available for this hotel.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to 2D array for JTable
            Object[][] data = new Object[dataList.size()][2];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Create JTable and display it in a scrollable pane
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable table editing
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "Most Booked Room Types", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching most booked room types: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewEmployeesWithRoles() {
        try {
            // Fetch employee data (exclude 'guest' role)
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT User_ID, Username, Role FROM user_account " +
                            "WHERE Hotel_ID = ? AND Role != 'guest'");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Define table column names
            String[] columnNames = {"User ID", "Username", "Role"};

            // Build data for the table
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();
            while (rs.next()) {
                int userID = rs.getInt("User_ID");
                String username = rs.getString("Username");
                String role = rs.getString("Role");
                dataList.add(new Object[]{userID, username, role});
            }

            // Check if there are no employees
            if (dataList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No employees found for this hotel.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert list to 2D array for JTable
            Object[][] data = new Object[dataList.size()][3];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }

            // Create JTable and display it in a scrollable pane
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Disable table editing
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the table in a dialog
            JOptionPane.showMessageDialog(this, scrollPane, "View All Employees with Their Role", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching employee data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}