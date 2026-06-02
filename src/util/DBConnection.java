package com.cabbooking.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            System.out.println("Trying database connection...");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cab_booking",
                    "root",
                    "root"
            );

            System.out.println("DATABASE CONNECTED SUCCESSFULLY");

            return con;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR:");
            e.printStackTrace();

            return null;
        }
    }
}