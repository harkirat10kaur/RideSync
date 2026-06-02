package com.cabbooking.ui;

import javax.swing.*;
import java.awt.*;

public class StyledPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(15, 32, 39),
                getWidth(), getHeight(), new Color(44, 83, 100)
        );

        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}