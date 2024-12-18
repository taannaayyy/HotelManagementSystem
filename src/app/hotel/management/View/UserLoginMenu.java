package app.hotel.management.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserLoginMenu extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public UserLoginMenu() {
        setTitle("Hotel Management System - User Login");
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);
        setLayout(null);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        // Set bounds for components
        int xStart = 50, yStart = 50, labelWidth = 100, fieldWidth = 200, height = 30;
        usernameLabel.setBounds(xStart, yStart, labelWidth, height);
        usernameField.setBounds(xStart + 100, yStart, fieldWidth, height);
        passwordLabel.setBounds(xStart, yStart + 50, labelWidth, height);
        passwordField.setBounds(xStart + 100, yStart + 50, fieldWidth, height);
        loginButton.setBounds(120, 150, 100, height);  // Login button
        exitButton.setBounds(230, 150, 100, height);   // Exit button

        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(exitButton);

        // Add action listeners
        loginButton.addActionListener(new LoginActionListener());
        exitButton.addActionListener(e -> System.exit(0));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate user and retrieve role, hotel information, and optionally guest name
            String[] authResult = authenticateUser(username, password);
            if (authResult != null) {
                String role = authResult[0];
                String hotelName = authResult[1];
                int userID = Integer.parseInt(authResult[2]);
                String guestName = authResult.length > 3 ? authResult[3] : null;

                // Personalized welcome message for guests
                if ("guest".equalsIgnoreCase(role) && guestName != null) {
                    JOptionPane.showMessageDialog(UserLoginMenu.this,
                            "Welcome, " + guestName + "! You are logged in as a Guest at " + hotelName + ".",
                            "Welcome", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(UserLoginMenu.this,
                            "Welcome, " + role + "! You are logged in to " + hotelName + ".",
                            "Welcome", JOptionPane.INFORMATION_MESSAGE);
                }

                // Open the corresponding menu based on the role
                switch (role.toLowerCase()) {
                    case "guest":
                        new GuestMenu(hotelName, userID);
                        break;
                    case "administrator":
                        new AdminMenu(hotelName);
                        break;
                    case "receptionist":
                        new ReceptionistMenu(hotelName);
                        break;
                    case "housekeeper":
                        new HousekeepingMenu(hotelName, userID);
                        break;
                    default:
                        JOptionPane.showMessageDialog(UserLoginMenu.this, "Invalid role!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                dispose(); // Close login window
            } else {
                JOptionPane.showMessageDialog(UserLoginMenu.this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String[] authenticateUser(String username, String password) {
        try (Connection conn = connectToDatabase()) {
            // Step 1: Authenticate user and fetch role, hotel name, and user ID
            String query = "SELECT ua.Role, h.Hotel_Name, ua.User_ID " +
                    "FROM user_account ua " +
                    "JOIN Hotel h ON ua.Hotel_ID = h.Hotel_ID " +
                    "WHERE ua.Username = ? AND ua.Password_Hash = ?";
            assert conn != null;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Role");
                String hotelName = rs.getString("Hotel_Name");
                int userID = rs.getInt("User_ID");

                String guestName = null;
                // Step 2: Fetch guest name if the role is "Guest"
                if (role.equalsIgnoreCase("Guest")) {
                    String guestQuery = "SELECT Guest_Name FROM guest WHERE User_ID = ?";
                    PreparedStatement guestStmt = conn.prepareStatement(guestQuery);
                    guestStmt.setInt(1, userID);
                    ResultSet guestRS = guestStmt.executeQuery();
                    if (guestRS.next()) {
                        guestName = guestRS.getString("Guest_Name");
                    }
                }
                return new String[]{role, hotelName, String.valueOf(userID), guestName};
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private Connection connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "Tny_0102032003";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

}