package com.cabbooking.gui;

import com.cabbooking.ui.CustomButton;
import com.cabbooking.ui.CustomTextField;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Login / Signup");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // FULL BACKGROUND
        JPanel bg = new JPanel();
        bg.setBackground(new Color(245, 250, 255));
        bg.setLayout(new GridBagLayout());

        // CENTER CARD
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420, 320));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Login / Signup");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your details to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        CustomTextField name = new CustomTextField(15);
        name.setBorder(BorderFactory.createTitledBorder("Full Name"));

        CustomTextField email = new CustomTextField(15);
        email.setBorder(BorderFactory.createTitledBorder("Email"));

        CustomButton btn = new CustomButton("Continue");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> {

            if (name.getText().isEmpty() || email.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            new BookingFrame(name.getText());
            dispose();
        });

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(name);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(email);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(btn);

        bg.add(card);
        add(bg);

        setVisible(true);
    }
}