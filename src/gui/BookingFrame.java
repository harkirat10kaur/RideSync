package com.cabbooking.gui;

import com.cabbooking.model.*;
import com.cabbooking.service.BookingService;

import javax.swing.*;
import java.awt.*;

public class BookingFrame extends JFrame {

    private JTextField pickupField, destinationField;
    private JComboBox<String> vehicleBox;
    private JLabel distanceLabel, fareLabel;

    private double distance = 0;
    private double fare = 0;

    private String userName;

    public BookingFrame(String userName) {
        this.userName = userName;

        setTitle("Book Cab");

        // ✅ FULL SCREEN
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();

        // ===== CARD PANEL =====
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(400, 420));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel title = new JLabel("Book Your Ride");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(120, 10, 200, 30);

        JLabel welcome = new JLabel("Welcome, " + userName);
        welcome.setBounds(20, 40, 300, 20);

        // ===== INPUTS =====
        JLabel pickupLabel = new JLabel("Pickup Location");
        pickupLabel.setBounds(20, 70, 200, 20);

        pickupField = new JTextField();
        pickupField.setBounds(20, 90, 350, 30);

        JLabel destLabel = new JLabel("Destination");
        destLabel.setBounds(20, 130, 200, 20);

        destinationField = new JTextField();
        destinationField.setBounds(20, 150, 350, 30);

        vehicleBox = new JComboBox<>(new String[]{
                "Mini (₹10/km)",
                "Sedan (₹15/km)",
                "SUV (₹20/km)"
        });
        vehicleBox.setBounds(20, 200, 350, 30);

        // ===== FIND RIDE BUTTON =====
        JButton findBtn = new JButton("Find Ride");
        findBtn.setBounds(120, 250, 150, 40);
        findBtn.setBackground(new Color(0, 150, 255));
        findBtn.setForeground(Color.WHITE);

        // ===== RESULT LABELS =====
        distanceLabel = new JLabel("Distance: -- km");
        distanceLabel.setBounds(20, 300, 200, 20);

        fareLabel = new JLabel("Fare: ₹0");
        fareLabel.setBounds(20, 320, 200, 20);

        // ===== CONFIRM BUTTON =====
        JButton confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setBounds(100, 350, 200, 40);
        confirmBtn.setBackground(new Color(0, 150, 255));
        confirmBtn.setForeground(Color.WHITE);

        // ===== FIND RIDE LOGIC =====
        findBtn.addActionListener(e -> {

            String pickup = pickupField.getText();
            String dest = destinationField.getText();

            if (pickup.isEmpty() || dest.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter pickup & destination");
                return;
            }

            // Dummy distance logic
            distance = 5 + (int)(Math.random() * 10);

            Vehicle vehicle;

            String selected = vehicleBox.getSelectedItem().toString();

            if (selected.contains("Mini")) {
                vehicle = new Mini();
            } else if (selected.contains("Sedan")) {
                vehicle = new Sedan();
            } else {
                vehicle = new SUV();
            }

            fare = vehicle.calculateFare(distance);

            distanceLabel.setText("Distance: " + distance + " km");
            fareLabel.setText("Fare: ₹" + fare);
        });

        // ===== CONFIRM BOOKING =====
        confirmBtn.addActionListener(e -> {

            if (pickupField.getText().isEmpty() || destinationField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields");
                return;
            }

            if (distance == 0) {
                JOptionPane.showMessageDialog(this, "Click 'Find Ride' first");
                return;
            }

            String vehicleType = vehicleBox.getSelectedItem().toString();

            new BookingService().saveBooking(
                    userName,
                    pickupField.getText(),
                    vehicleType,
                    distance,
                    fare
            );

            JOptionPane.showMessageDialog(this, "Ride Booked Successfully 🚖");
        });

        // ===== ADD COMPONENTS TO CARD =====
        card.add(title);
        card.add(welcome);
        card.add(pickupLabel);
        card.add(pickupField);
        card.add(destLabel);
        card.add(destinationField);
        card.add(vehicleBox);
        card.add(findBtn);
        card.add(distanceLabel);
        card.add(fareLabel);
        card.add(confirmBtn);

        // ===== IMAGE (NORMAL, RIGHT SIDE) =====
        JLabel imageLabel = new JLabel();
        try {
            ImageIcon img = new ImageIcon(getClass().getResource("/com/cabbooking/resources/car.png"));
            Image scaled = img.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel.setText("Add car.png in resources");
        }

        // ===== LAYOUT POSITIONING =====
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 50, 20, 50);
        mainPanel.add(card, gbc);

        gbc.gridx = 1;
        mainPanel.add(imageLabel, gbc);

        add(mainPanel);
        setVisible(true);
    }
}