package app.hotel.management.View;

import javax.swing.*;
import java.awt.*;

class AdminMenu extends JFrame {

    public AdminMenu() {
        setTitle("Admin Menu");

        setSize(400, 300);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setResizable(false);
        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}