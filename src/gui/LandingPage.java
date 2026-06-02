package com.cabbooking.gui;

import com.cabbooking.ui.CustomButton;

import javax.swing.*;
import java.awt.*;

public class LandingPage extends JFrame {

    public LandingPage() {

        setTitle("Cab Booking");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // FULL BACKGROUND
        JPanel bg = new JPanel();
        bg.setBackground(new Color(245, 250, 255));
        bg.setLayout(new GridBagLayout());

        // CENTER CONTENT
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ===== IMAGE =====
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/com/cabbooking/resources/car.png")
            );
            Image img = icon.getImage().getScaledInstance(250, 180, Image.SCALE_SMOOTH);
            JLabel car = new JLabel(new ImageIcon(img));
            car.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(car);
        } catch (Exception e) {
            JLabel fallback = new JLabel("Add car.png");
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(fallback);
        }

        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // ===== TITLE =====
        JLabel title = new JLabel("CAB BOOKING");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== SUBTITLE =====
        JLabel subtitle = new JLabel("Fast • Safe • Affordable");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== BUTTON =====
        CustomButton btn = new CustomButton("Get Started");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        content.add(btn);

        bg.add(content);
        add(bg);

        setVisible(true);
    }
}