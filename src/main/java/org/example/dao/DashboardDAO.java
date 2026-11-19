package org.example.dao;

import org.example.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public static int getTotalStudents() {
        return getCount("SELECT COUNT(*) FROM students");
    }

    public static int getTotalQuizzes() {
        return getCount("SELECT COUNT(*) FROM quizzes");
    }

    public static int getTotalAssignments() {
        return getCount("SELECT COUNT(*) FROM quiz_assignments");
    }

    public static int getTotalQueries() {
        return getCount("SELECT COUNT(*) FROM student_queries");
    }

    private static int getCount(String sql) {

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                System.out.println("Database connection failed!");
                return 0;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching dashboard statistics!");
            e.printStackTrace();
        }

        return 0;
    }
}