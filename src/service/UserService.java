package com.cabbooking.service;

import com.cabbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserService {

    public boolean registerUser(
            String name,
            String email,
            String password) {

        String query =
                "INSERT INTO users(name,email,password) VALUES(?,?,?)";

        try(Connection con =
                    DBConnection.getConnection()) {

            PreparedStatement pst =
                    con.prepareStatement(query);

            pst.setString(1,name);
            pst.setString(2,email);
            pst.setString(3,password);

            pst.executeUpdate();

            return true;

        }
        catch(Exception e){

            e.printStackTrace();

            return false;
        }
    }
}