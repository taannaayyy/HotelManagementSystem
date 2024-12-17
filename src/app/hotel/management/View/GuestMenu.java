package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

class GuestMenu extends JFrame {

    private String hotelName;
    private int userID;
    private int hotelID; // Hotel ID for filtering rooms
    private Connection connection;

    public GuestMenu(String hotelName, int userID) {
        this.hotelName = hotelName;
        this.userID = userID;
        connectToDatabase();
        fetchGuestHotelID();

        setTitle("Guest Menu - " + hotelName);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Create buttons for menu options
        JButton viewRoomsButton = new JButton("View Available Rooms");
        JButton addBookingButton = new JButton("Add New Booking");
        JButton viewBookingsButton = new JButton("View My Bookings");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton logoutButton = new JButton("Logout");

        // Set button bounds
        int xStart = 100, yStart = 50, buttonWidth = 200, buttonHeight = 30, yGap = 20;

        viewRoomsButton.setBounds(xStart, yStart, buttonWidth, buttonHeight);
        addBookingButton.setBounds(xStart, yStart + (buttonHeight + yGap), buttonWidth, buttonHeight);
        viewBookingsButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 2, buttonWidth, buttonHeight);
        cancelBookingButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 3, buttonWidth, buttonHeight);
        logoutButton.setBounds(xStart, yStart + (buttonHeight + yGap) * 4, buttonWidth, buttonHeight);

        // Add buttons to the frame
        add(viewRoomsButton);
        add(addBookingButton);
        add(viewBookingsButton);
        add(cancelBookingButton);
        add(logoutButton);

        // Add action listeners
        viewRoomsButton.addActionListener(e -> viewAvailableRooms());
        addBookingButton.addActionListener(e -> addNewBooking());
        viewBookingsButton.addActionListener(e -> viewMyBookings());
        cancelBookingButton.addActionListener(e -> cancelBooking());
        logoutButton.addActionListener(e -> {
            dispose();
            new UserLoginMenu();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "password";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchGuestHotelID() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT Hotel_ID FROM user_account WHERE User_ID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hotelID = rs.getInt("Hotel_ID");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch guest hotel: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewAvailableRooms() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM room WHERE Hotel_ID = ? AND Room_Status = 'Available'")) {
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder("Available Rooms:\n");
            while (rs.next()) {
                result.append("Room ID: ").append(rs.getInt("Room_ID"))
                        .append(", Type: ").append(rs.getString("Room_Type"))
                        .append(", Price: ").append(rs.getDouble("Room_Price")).append("\n");
            }
            if (result.length() == "Available Rooms:\n".length()) {
                JOptionPane.showMessageDialog(this, "No rooms are currently available.");
            } else {
                JOptionPane.showMessageDialog(this, result.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void addNewBooking() {
        try {
            // Fetch available rooms
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT Room_ID, Room_Type, Room_Price FROM room WHERE Hotel_ID = ? AND Room_Status = 'Available'");
            stmt.setInt(1, hotelID);
            ResultSet rs = stmt.executeQuery();

            // Create a combo box with available rooms
            DefaultComboBoxModel<String> roomModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int roomID = rs.getInt("Room_ID");
                String roomType = rs.getString("Room_Type");
                double roomPrice = rs.getDouble("Room_Price");
                roomModel.addElement("ID: " + roomID + " | Type: " + roomType + " | Price: $" + roomPrice);
            }

            if (roomModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "No rooms are currently available.", "No Rooms", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> roomComboBox = new JComboBox<>(roomModel);

            // Create date choosers for check-in and check-out
            JDateChooser checkInChooser = new JDateChooser();
            JDateChooser checkOutChooser = new JDateChooser();
            checkInChooser.setDateFormatString("yyyy-MM-dd");
            checkOutChooser.setDateFormatString("yyyy-MM-dd");

            // Add components to a panel
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Select Room:"));
            panel.add(roomComboBox);
            panel.add(new JLabel("Check-in Date:"));
            panel.add(checkInChooser);
            panel.add(new JLabel("Check-out Date:"));
            panel.add(checkOutChooser);

            // Show panel in a dialog
            int result = JOptionPane.showConfirmDialog(this, panel, "Book a Room", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Validate check-in and check-out dates
                Date checkInDate = checkInChooser.getDate();
                Date checkOutDate = checkOutChooser.getDate();

                if (checkInDate == null || checkOutDate == null) {
                    JOptionPane.showMessageDialog(this, "Both dates must be selected.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!checkOutDate.after(checkInDate)) {
                    JOptionPane.showMessageDialog(this, "Check-out date must be after the check-in date.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Extract Room ID from the selected item
                String selectedRoom = (String) roomComboBox.getSelectedItem();
                assert selectedRoom != null;
                int roomID = Integer.parseInt(selectedRoom.split("\\|")[0].split(":")[1].trim());

                // Format dates
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String checkIn = sdf.format(checkInDate);
                String checkOut = sdf.format(checkOutDate);

                // Add reservation record
                PreparedStatement bookingStmt = connection.prepareStatement(
                        "INSERT INTO reservation (Room_ID, Guest_ID, CheckInDate, CheckOutDate, Booking_Status) " +
                                "VALUES (?, ?, ?, ?, 'Booked')");
                bookingStmt.setInt(1, roomID);
                bookingStmt.setInt(2, userID);
                bookingStmt.setString(3, checkIn);
                bookingStmt.setString(4, checkOut);
                bookingStmt.executeUpdate();

                // Update room status to 'Occupied'
                PreparedStatement updateRoomStmt = connection.prepareStatement(
                        "UPDATE room SET Room_Status = 'Occupied' WHERE Room_ID = ?");
                updateRoomStmt.setInt(1, roomID);
                updateRoomStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room booked successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMyBookings() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT r.Reservation_ID, r.Room_ID, r.CheckInDate, r.CheckOutDate, r.Booking_Status, rm.Room_Type " +
                        "FROM reservation r " +
                        "JOIN room rm ON r.Room_ID = rm.Room_ID " +
                        "WHERE r.Guest_ID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder("Your Bookings:\n");
            while (rs.next()) {
                result.append("Reservation ID: ").append(rs.getInt("Reservation_ID"))
                        .append(", Room ID: ").append(rs.getInt("Room_ID"))
                        .append(", Room Type: ").append(rs.getString("Room_Type"))
                        .append(", Check-in: ").append(rs.getString("CheckInDate"))
                        .append(", Check-out: ").append(rs.getString("CheckOutDate"))
                        .append(", Status: ").append(rs.getString("Booking_Status")).append("\n");
            }
            if (result.length() == "Your Bookings:\n".length()) {
                JOptionPane.showMessageDialog(this, "You have no bookings.");
            } else {
                JOptionPane.showMessageDialog(this, result.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching bookings: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        try {
            // Fetch guest's bookings with status 'Booked'
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT r.Reservation_ID, r.Room_ID, r.CheckInDate, r.CheckOutDate, rm.Room_Type " +
                            "FROM reservation r " +
                            "JOIN room rm ON r.Room_ID = rm.Room_ID " +
                            "WHERE r.Guest_ID = ? AND r.Booking_Status = 'Booked'");
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            // Populate bookings into a combo box
            DefaultComboBoxModel<String> bookingModel = new DefaultComboBoxModel<>();
            while (rs.next()) {
                int reservationID = rs.getInt("Reservation_ID");
                int roomID = rs.getInt("Room_ID");
                String roomType = rs.getString("Room_Type");
                String checkIn = rs.getString("CheckInDate");
                String checkOut = rs.getString("CheckOutDate");
                bookingModel.addElement("Reservation ID: " + reservationID +
                        " | Room ID: " + roomID +
                        " | Type: " + roomType +
                        " | Check-in: " + checkIn +
                        " | Check-out: " + checkOut);
            }

            if (bookingModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "You have no active bookings to cancel.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<String> bookingComboBox = new JComboBox<>(bookingModel);

            // Show combo box in a dialog
            int result = JOptionPane.showConfirmDialog(this, bookingComboBox, "Select Booking to Cancel", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Extract Reservation ID from the selected item
                String selectedBooking = (String) bookingComboBox.getSelectedItem();
                assert selectedBooking != null;
                int reservationID = Integer.parseInt(selectedBooking.split("\\|")[0].split(":")[1].trim());
                int roomID = Integer.parseInt(selectedBooking.split("\\|")[1].split(":")[1].trim());

                // Update reservation status to 'Cancelled'
                PreparedStatement cancelStmt = connection.prepareStatement(
                        "UPDATE reservation SET Booking_Status = 'Cancelled' WHERE Reservation_ID = ?");
                cancelStmt.setInt(1, reservationID);
                cancelStmt.executeUpdate();

                // Update room status to 'Available'
                PreparedStatement updateRoomStmt = connection.prepareStatement(
                        "UPDATE room SET Room_Status = 'Available' WHERE Room_ID = ?");
                updateRoomStmt.setInt(1, roomID);
                updateRoomStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}