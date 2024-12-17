package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserLoginMenu extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public UserLoginMenu() {
        setTitle("Hotel Management System - User Login");
        setSize(400, 300);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setResizable(false);
        setLayout(null);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        // Set bounds
        usernameLabel.setBounds(50, 50, 100, 30);
        usernameField.setBounds(150, 50, 200, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        passwordField.setBounds(150, 100, 200, 30);
        loginButton.setBounds(130, 150, 100, 30); // Login button's bounds
        exitButton.setBounds(240, 150, 100, 30);  // Exit button placed to the right of Login button

        // Add action listener for Exit button
        exitButton.addActionListener(e -> System.exit(0));

        // Add components to frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(exitButton);

        // Add action listener to login button
        loginButton.addActionListener(new LoginActionListener());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ActionListener for the Login button
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String role = authenticateUser(username, password);
            if (role != null) {
                openUserMenu(role);
            } else {
                JOptionPane.showMessageDialog(UserLoginMenu.this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Authenticate user and fetch role from the database
    private String authenticateUser(String username, String password) {
        String role = null;
        try (Connection conn = connectToDatabase()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    role = rs.getString("role");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return role;
    }

    // Open the respective user menu based on the role
    private void openUserMenu(String role) {
        switch (role) {
            case "guest":
                new GuestMenu();
                break;
            case "admin":
                new AdminMenu();
                break;
            case "receptionist":
                new ReceptionistMenu();
                break;
            case "housekeeping":
                new HousekeepingMenu();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid role!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose(); // Close the login window
    }

    // Database connection method
    private Connection connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/hotel_management";
            String user = "root";
            String password = "password";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

}