package com.cabbooking.service;

import com.cabbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookingService {

    public void saveBooking(String name,
                            String location,
                            String vehicle,
                            double distance,
                            double fare) {

        String bookingId =
                "RS" + System.currentTimeMillis();

        String[] drivers = {
                "Aman",
                "Rahul",
                "Karan",
                "Vikram",
                "Rohit"
        };

        String driver =
                drivers[(int)(Math.random() * drivers.length)];

        String cabNumber =
                "PB65-" + (1000 + (int)(Math.random() * 9000));

        String query =
                "INSERT INTO bookings " +
                        "(name,location,vehicle,distance,fare," +
                        "booking_id,driver_name,cab_number,booking_time,status) " +
                        "VALUES (?,?,?,?,?,?,?,?,NOW(),?)";

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement pst =
                    con.prepareStatement(query);

            pst.setString(1, name);
            pst.setString(2, location);
            pst.setString(3, vehicle);
            pst.setDouble(4, distance);
            pst.setDouble(5, fare);

            pst.setString(6, bookingId);
            pst.setString(7, driver);
            pst.setString(8, cabNumber);
            pst.setString(9, "Confirmed");

            pst.executeUpdate();

            System.out.println("Booking Saved Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean cancelBooking(String bookingId) {

        String query =
                "UPDATE bookings SET status='Cancelled' WHERE booking_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, bookingId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    public String getBookingsJsonByUser(String userName) {

        StringBuilder json = new StringBuilder("[");

        String query =
                "SELECT * FROM bookings WHERE name=? ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, userName);

            ResultSet rs = pst.executeQuery();

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{")
                        .append("\"bookingId\":\"")
                        .append(rs.getString("booking_id"))
                        .append("\",")

                        .append("\"bookingTime\":\"")
                        .append(rs.getTimestamp("booking_time"))
                        .append("\",")

                        .append("\"vehicle\":\"")
                        .append(rs.getString("vehicle"))
                        .append("\",")

                        .append("\"driverName\":\"")
                        .append(rs.getString("driver_name"))
                        .append("\",")

                        .append("\"cabNumber\":\"")
                        .append(rs.getString("cab_number"))
                        .append("\",")

                        .append("\"fare\":")
                        .append(rs.getDouble("fare"))
                        .append(",")

                        .append("\"status\":\"")
                        .append(rs.getString("status"))
                        .append("\"")

                        .append("}");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        json.append("]");

        return json.toString();
    }
}
