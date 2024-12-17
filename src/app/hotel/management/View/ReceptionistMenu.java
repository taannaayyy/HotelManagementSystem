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
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Button names
        String[] buttonNames = {
                "Add New Booking", "Modify Booking", "Delete Booking",
                "View Bookings", "Process Payment", "Assign Housekeeping Task",
                "View Housekeeping Records"
        };

        // Create buttons
        int xStart = 50, yStart = 50, buttonWidth = 250, buttonHeight = 30, yGap = 10;
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            int row = i % 4, col = i / 4; // Grid layout
            button.setBounds(xStart + (col * 300), yStart + (row * (buttonHeight + yGap)), buttonWidth, buttonHeight);
            add(button);
            attachButtonAction(button, buttonNames[i]);
        }

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(300, 400, 200, buttonHeight);
        logoutButton.addActionListener(e -> {
            dispose();
            new UserLoginMenu();
        });
        add(logoutButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "password"; // Replace with real credentials
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
            JOptionPane.showMessageDialog(this, "Error fetching hotel ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachButtonAction(JButton button, String actionName) {
        switch (actionName) {
            case "Add New Booking" -> button.addActionListener(e -> addBooking());
            case "Modify Booking" -> button.addActionListener(e -> modifyBooking());
            case "Delete Booking" -> button.addActionListener(e -> deleteBooking());
            case "View Bookings" -> button.addActionListener(e -> viewBookings());
            case "Process Payment" -> button.addActionListener(e -> processPayment());
            case "Assign Housekeeping Task" -> button.addActionListener(e -> assignHousekeepingTask());
            case "View Housekeeping Records" -> button.addActionListener(e -> viewHousekeepingRecords());
        }
    }

    // Action Implementations
    private void addBooking() {
        JTextField guestID = new JTextField();
        JTextField roomID = new JTextField();
        JTextField checkIn = new JTextField("YYYY-MM-DD");
        JTextField checkOut = new JTextField("YYYY-MM-DD");

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Guest ID:"));
        panel.add(guestID);
        panel.add(new JLabel("Room ID:"));
        panel.add(roomID);
        panel.add(new JLabel("Check-in Date:"));
        panel.add(checkIn);
        panel.add(new JLabel("Check-out Date:"));
        panel.add(checkOut);

        if (JOptionPane.showConfirmDialog(this, panel, "Add New Booking", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO reservation (Guest_ID, Room_ID, CheckInDate, CheckOutDate, Booking_Status) VALUES (?, ?, ?, ?, 'Pending')");
                stmt.setInt(1, Integer.parseInt(guestID.getText()));
                stmt.setInt(2, Integer.parseInt(roomID.getText()));
                stmt.setString(3, checkIn.getText());
                stmt.setString(4, checkOut.getText());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking added successfully!");
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void modifyBooking() {
        JTextField bookingID = new JTextField();
        JTextField newRoomID = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Booking ID:"));
        panel.add(bookingID);
        panel.add(new JLabel("New Room ID:"));
        panel.add(newRoomID);

        if (JOptionPane.showConfirmDialog(this, panel, "Modify Booking", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE reservation SET Room_ID = ? WHERE Reservation_ID = ?");
                stmt.setInt(1, Integer.parseInt(newRoomID.getText()));
                stmt.setInt(2, Integer.parseInt(bookingID.getText()));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking modified successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void deleteBooking() {
        JTextField bookingID = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Booking ID:"));
        panel.add(bookingID);

        if (JOptionPane.showConfirmDialog(this, panel, "Delete Booking", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM reservation WHERE Reservation_ID = ?");
                stmt.setInt(1, Integer.parseInt(bookingID.getText()));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewBookings() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM reservation");
            ResultSet rs = stmt.executeQuery();
            JTable table = new JTable(buildTableModel(rs));
            JOptionPane.showMessageDialog(this, new JScrollPane(table), "All Bookings", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void processPayment() {
        JOptionPane.showMessageDialog(this, "Processing Payment... Feature Under Construction.");
    }

    private void assignHousekeepingTask() {
        JOptionPane.showMessageDialog(this, "Assigning Housekeeping Task... Feature Under Construction.");
    }

    private void viewHousekeepingRecords() {
        JOptionPane.showMessageDialog(this, "Viewing Housekeeping Records... Feature Under Construction.");
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        DefaultTableModel model = new DefaultTableModel();
        for (int column = 1; column <= columnCount; column++) {
            model.addColumn(metaData.getColumnName(column));
        }
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        return model;
    }
}
