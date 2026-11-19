package org.example;

import org.example.database.DBConnection;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            System.out.println("Online Quiz Management System Started!");
        } else {
            System.out.println("Unable to start application.");
        }
    }
}