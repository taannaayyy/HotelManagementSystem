package app.hotel.management.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class ReceptionistMenu extends JFrame {

    private String hotelName;
    private int hotelID;
    private Connection connection;

    public ReceptionistMenu(String hotelName) {
        this.hotelName = hotelName;
        connectToDatabase();
        fetchHotelID();

        setTitle("Receptionist Menu - " + hotelName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Buttons
        String[] buttonNames = {
                "Add New Booking", "Modify Booking", "Delete Booking",
                "Process Payment", "Assign Housekeeping Task", "View Housekeeping Records", "Logout"
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
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage());
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

    private void attachButtonAction(JButton button, String actionName) {
        switch (actionName) {
            case "Add New Booking" -> button.addActionListener(e -> addBooking());
            case "Modify Booking" -> button.addActionListener(e -> modifyBooking());
            case "Delete Booking" -> button.addActionListener(e -> deleteBooking());
            case "Process Payment" -> button.addActionListener(e -> processPayment());
            case "Assign Housekeeping Task" -> button.addActionListener(e -> assignHousekeepingTask());
            case "View Housekeeping Records" -> button.addActionListener(e -> viewHousekeepingTasks());
            case "Logout" -> button.addActionListener(e -> {
                dispose();
                new UserLoginMenu();
            });
        }
    }

    private void addBooking() {
        try {
            // Fetch available guests and rooms
            JComboBox<String> guestDropdown = fetchDropdown(
                    "SELECT Guest_ID, Guest_Name FROM Guest",
                    "Guest_ID", "Guest_Name"
            );
            JComboBox<String> roomDropdown = fetchDropdown(
                    "SELECT Room_ID, Room_Type FROM Room WHERE Hotel_ID = ? AND Room_Status = 'Available'",
                    "Room_ID", "Room_Type", hotelID
            );

            // Input fields for check-in and check-out dates
            JTextField checkInField = new JTextField("YYYY-MM-DD");
            JTextField checkOutField = new JTextField("YYYY-MM-DD");

            // Create a panel to display booking fields
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Select Guest:"));
            panel.add(guestDropdown);
            panel.add(new JLabel("Select Room:"));
            panel.add(roomDropdown);
            panel.add(new JLabel("Check-in Date:"));
            panel.add(checkInField);
            panel.add(new JLabel("Check-out Date:"));
            panel.add(checkOutField);

            // Show booking form dialog
            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Booking", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Validate and process input
                int guestID = Integer.parseInt(guestDropdown.getSelectedItem().toString().split(":")[0].trim());
                int roomID = Integer.parseInt(roomDropdown.getSelectedItem().toString().split(":")[0].trim());
                String checkInDate = checkInField.getText().trim();
                String checkOutDate = checkOutField.getText().trim();

                if (checkInDate.isEmpty() || checkOutDate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter valid check-in and check-out dates.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate that check-out is after check-in
                if (!validateDates(checkInDate, checkOutDate)) {
                    JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert the booking into the reservation table
                String insertBookingQuery = """
                INSERT INTO Reservation (Guest_ID, Room_ID, CheckInDate, CheckOutDate, Booking_Status)
                VALUES (?, ?, ?, ?, 'Booked')
            """;
                PreparedStatement bookingStmt = connection.prepareStatement(insertBookingQuery);
                bookingStmt.setInt(1, guestID);
                bookingStmt.setInt(2, roomID);
                bookingStmt.setString(3, checkInDate);
                bookingStmt.setString(4, checkOutDate);
                bookingStmt.executeUpdate();

                // Update the room status to 'Occupied'
                String updateRoomQuery = "UPDATE Room SET Room_Status = 'Occupied' WHERE Room_ID = ?";
                PreparedStatement updateRoomStmt = connection.prepareStatement(updateRoomQuery);
                updateRoomStmt.setInt(1, roomID);
                updateRoomStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking added successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to validate dates
    private boolean validateDates(String checkInDate, String checkOutDate) {
        try {
            java.time.LocalDate checkIn = java.time.LocalDate.parse(checkInDate);
            java.time.LocalDate checkOut = java.time.LocalDate.parse(checkOutDate);
            return checkOut.isAfter(checkIn);
        } catch (java.time.format.DateTimeParseException e) {
            return false; // Invalid date format
        }
    }

    private void modifyBooking() {
        try {
            JComboBox<String> bookingDropdown = fetchDropdown("SELECT Reservation_ID, Room_ID FROM reservation WHERE EXISTS (SELECT 1 FROM Room WHERE reservation.Room_ID = Room.Room_ID AND Room.Hotel_ID = ?)", "Reservation_ID", "Room_ID", hotelID);
            JTextField newCheckIn = new JTextField("YYYY-MM-DD");
            JTextField newCheckOut = new JTextField("YYYY-MM-DD");

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Select Booking:"));
            panel.add(bookingDropdown);
            panel.add(new JLabel("New Check-in Date:"));
            panel.add(newCheckIn);
            panel.add(new JLabel("New Check-out Date:"));
            panel.add(newCheckOut);

            if (JOptionPane.showConfirmDialog(this, panel, "Modify Booking", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                PreparedStatement stmt = connection.prepareStatement("UPDATE reservation SET CheckInDate = ?, CheckOutDate = ? WHERE Reservation_ID = ?");
                stmt.setString(1, newCheckIn.getText());
                stmt.setString(2, newCheckOut.getText());
                stmt.setInt(3, Integer.parseInt(bookingDropdown.getSelectedItem().toString().split(":")[0]));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking modified successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteBooking() {
        try {
            // Fetch all bookings for the current hotel
            String query = """
            SELECT r.Reservation_ID, r.Room_ID, g.Guest_ID, g.Guest_Name, r.CheckInDate, r.CheckOutDate, r.Booking_Status
            FROM Reservation r
            JOIN Room rm ON r.Room_ID = rm.Room_ID
            JOIN Guest g ON r.Guest_ID = g.Guest_ID
            WHERE rm.Hotel_ID = ?
        """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Populate a dropdown with bookings
            DefaultComboBoxModel<String> bookingModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int reservationID = rs.getInt("Reservation_ID");
                int roomID = rs.getInt("Room_ID");
                String guestName = rs.getString("Guest_Name");
                String checkIn = rs.getString("CheckInDate");
                String checkOut = rs.getString("CheckOutDate");
                String bookingStatus = rs.getString("Booking_Status");

                bookingModel.addElement("Reservation ID: " + reservationID + ", Room ID: " + roomID +
                        ", Guest: " + guestName + ", Check-in: " + checkIn + ", Check-out: " + checkOut +
                        ", Status: " + bookingStatus);
            }

            if (bookingModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No bookings available to delete.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> bookingDropdown = new JComboBox<>(bookingModel);

            // Show a dialog to select a booking
            int result = JOptionPane.showConfirmDialog(this, bookingDropdown, "Select Booking to Delete", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract selected reservation details
                String selectedBooking = (String) bookingDropdown.getSelectedItem();
                assert selectedBooking != null;
                int reservationID = Integer.parseInt(selectedBooking.split(",")[0].split(":")[1].trim());
                int roomID = Integer.parseInt(selectedBooking.split(",")[1].split(":")[1].trim());

                // Delete the booking
                String deleteBookingQuery = "DELETE FROM Reservation WHERE Reservation_ID = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteBookingQuery);
                deleteStmt.setInt(1, reservationID);
                deleteStmt.executeUpdate();

                // Update room status to 'Available'
                String updateRoomQuery = "UPDATE Room SET Room_Status = 'Available' WHERE Room_ID = ?";
                PreparedStatement updateRoomStmt = connection.prepareStatement(updateRoomQuery);
                updateRoomStmt.setInt(1, roomID);
                updateRoomStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking deleted and room marked as 'Available'.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPayment() {
        try {
            // Fetch bookings with pending payments
            String query = """
            SELECT p.Payment_ID, r.Reservation_ID, r.Guest_ID, r.Room_ID, p.Amount, p.Payment_Status
            FROM Payment p
            JOIN Reservation r ON p.Reservation_ID = r.Reservation_ID
            WHERE p.Payment_Status = 'Pending' AND EXISTS (
                SELECT 1 FROM Room rm WHERE r.Room_ID = rm.Room_ID AND rm.Hotel_ID = ?
            )
        """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            DefaultComboBoxModel<String> paymentModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int paymentID = rs.getInt("Payment_ID");
                int reservationID = rs.getInt("Reservation_ID");
                int guestID = rs.getInt("Guest_ID");
                int roomID = rs.getInt("Room_ID");
                double amount = rs.getDouble("Amount");

                paymentModel.addElement("Payment ID: " + paymentID + ", Reservation ID: " + reservationID + ", Guest ID: " + guestID + ", Room ID: " + roomID + ", Amount: $" + amount);
            }

            if (paymentModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No pending payments available for processing.", "No Payments", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> paymentDropdown = new JComboBox<>(paymentModel);

            // Display a dialog for payment selection
            int result = JOptionPane.showConfirmDialog(this, paymentDropdown, "Select Payment to Process", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract selected payment details
                String selectedPayment = (String) paymentDropdown.getSelectedItem();
                assert selectedPayment != null;
                int paymentID = Integer.parseInt(selectedPayment.split(",")[0].split(":")[1].trim());
                int reservationID = Integer.parseInt(selectedPayment.split(",")[1].split(":")[1].trim());

                // Update payment status to 'Paid' and set the payment date
                String updatePaymentQuery = "UPDATE Payment SET Payment_Status = 'Paid', Payment_Date = CURDATE() WHERE Payment_ID = ?";
                PreparedStatement updatePaymentStmt = connection.prepareStatement(updatePaymentQuery);
                updatePaymentStmt.setInt(1, paymentID);
                updatePaymentStmt.executeUpdate();

                // Update reservation status to 'Confirmed'
                String updateReservationQuery = "UPDATE Reservation SET Booking_Status = 'Confirmed' WHERE Reservation_ID = ?";
                PreparedStatement updateReservationStmt = connection.prepareStatement(updateReservationQuery);
                updateReservationStmt.setInt(1, reservationID);
                updateReservationStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Payment processed and reservation confirmed successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error processing payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignHousekeepingTask() {
        try {
            // Fetch rooms not in 'Cleaning' or 'Maintenance' status and housekeepers
            JComboBox<String> roomDropdown = fetchDropdown(
                    "SELECT Room_ID, Room_Type FROM Room WHERE Hotel_ID = ? AND Room_Status NOT IN ('Cleaning', 'Maintenance')",
                    "Room_ID", "Room_Type", hotelID
            );
            JComboBox<String> housekeeperDropdown = fetchDropdown(
                    "SELECT s.Staff_ID, s.Staff_Name FROM Staff s JOIN Housekeeper h ON s.Staff_ID = h.Staff_ID WHERE s.Hotel_ID = ?",
                    "Staff_ID", "Staff_Name", hotelID
            );

            // Input field for task schedule
            JTextField scheduleField = new JTextField("YYYY-MM-DD");

            // Create the assignment panel
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Select Room:"));
            panel.add(roomDropdown);
            panel.add(new JLabel("Select Housekeeper:"));
            panel.add(housekeeperDropdown);
            panel.add(new JLabel("Task Schedule:"));
            panel.add(scheduleField);

            // Display the assignment dialog
            int result = JOptionPane.showConfirmDialog(this, panel, "Assign Housekeeping Task", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract selected room and housekeeper details
                int roomID = Integer.parseInt(roomDropdown.getSelectedItem().toString().split(":")[0].trim());
                int housekeeperID = Integer.parseInt(housekeeperDropdown.getSelectedItem().toString().split(":")[0].trim());
                String schedule = scheduleField.getText();

                // Insert task into housekeeping table
                String insertTaskQuery = """
                INSERT INTO Housekeeping (Housekeeper_Staff_ID, Room_ID, Schedule, Task_Status)
                VALUES (?, ?, ?, 'Pending')
            """;
                PreparedStatement stmt = connection.prepareStatement(insertTaskQuery);
                stmt.setInt(1, housekeeperID);
                stmt.setInt(2, roomID);
                stmt.setString(3, schedule);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Task assigned successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error assigning task: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewHousekeepingTasks() {
        try {
            // Query to strictly fetch housekeeping tasks for the current hotel
            String query = """
            SELECT hk.Task_ID, hk.Room_ID, r.Room_Type, hk.Housekeeper_Staff_ID, s.Staff_Name, hk.Schedule, hk.Task_Status
            FROM Housekeeping hk
            JOIN Room r ON hk.Room_ID = r.Room_ID
            LEFT JOIN Staff s ON hk.Housekeeper_Staff_ID = s.Staff_ID
            WHERE r.Hotel_ID = ? AND (s.Hotel_ID = ? OR hk.Housekeeper_Staff_ID IS NULL)
        """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, hotelID); // Filter tasks by the hotel's Room records
            stmt.setInt(2, hotelID); // Ensure Staff is from the same hotel
            ResultSet rs = stmt.executeQuery();

            // Define table column names
            String[] columnNames = {"Task ID", "Room ID", "Room Type", "Housekeeper ID", "Housekeeper Name", "Schedule", "Task Status"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                int taskID = rs.getInt("Task_ID");
                int roomID = rs.getInt("Room_ID");
                String roomType = rs.getString("Room_Type") != null ? rs.getString("Room_Type") : "N/A";
                int housekeeperID = rs.getInt("Housekeeper_Staff_ID");
                String housekeeperName = rs.getString("Staff_Name") != null ? rs.getString("Staff_Name") : "Unassigned";
                Date schedule = rs.getDate("Schedule");
                String taskStatus = rs.getString("Task_Status");

                tableModel.addRow(new Object[]{
                        taskID,
                        roomID,
                        roomType,
                        housekeeperID,
                        housekeeperName,
                        schedule != null ? schedule.toString() : "N/A",
                        taskStatus
                });
            }

            // Handle no records found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No housekeeping records found for the current hotel.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Display the tasks in a JTable
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Housekeeping Records", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching housekeeping records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComboBox<String> fetchDropdown(String query, String idColumn, String displayColumn, Object... params) throws SQLException {
        JComboBox<String> dropdown = new JComboBox<>();
        PreparedStatement stmt = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) stmt.setObject(i + 1, params[i]);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) dropdown.addItem(rs.getString(idColumn) + ": " + rs.getString(displayColumn));
        return dropdown;
    }
}