package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/quiz_management";

    private static final String USER = "root";

    // Password is stored in a Windows environment variable
    private static final String PASSWORD =
            System.getenv("QUIZ_DB_PASSWORD");

    public static Connection getConnection() {

        try {
            if (PASSWORD == null || PASSWORD.isBlank()) {
                throw new SQLException(
                        "QUIZ_DB_PASSWORD environment variable is not set."
                );
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException | SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

            return null;
        }
    }
}