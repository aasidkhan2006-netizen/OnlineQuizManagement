package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.StudentQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentQueryDAO {

    // Get all student queries (Admin)
    public List<StudentQuery> getAllQueries() {

        List<StudentQuery> queries = new ArrayList<>();

        String sql = "SELECT * FROM student_queries ORDER BY query_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                queries.add(new StudentQuery(
                        rs.getInt("query_id"),
                        rs.getInt("student_id"),
                        rs.getString("subject"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queries;
    }

    // Get queries for a specific student
    public List<StudentQuery> getQueriesForStudent(int studentId) {

        List<StudentQuery> queries = new ArrayList<>();

        String sql = "SELECT * FROM student_queries " +
                "WHERE student_id = ? ORDER BY query_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    queries.add(new StudentQuery(
                            rs.getInt("query_id"),
                            rs.getInt("student_id"),
                            rs.getString("subject"),
                            rs.getString("message"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queries;
    }

    // Update query status (Admin)
    public boolean updateQueryStatus(int queryId, String status) {

        String sql = "UPDATE student_queries SET status=? WHERE query_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, queryId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete query (Admin)
    public boolean deleteQuery(int queryId) {

        String sql = "DELETE FROM student_queries WHERE query_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, queryId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a new student query
    public boolean addQuery(int studentId, String subject, String message) {

        String sql = "INSERT INTO student_queries " +
                "(student_id, subject, message) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, subject);
            ps.setString(3, message);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all queries with student details (Admin)
    public List<Object[]> getAllQueryDetails() {

        List<Object[]> queries = new ArrayList<>();

        String sql = "SELECT sq.query_id, s.full_name, " +
                "s.registration_number, sq.subject, " +
                "sq.message, sq.status, sq.created_at " +
                "FROM student_queries sq " +
                "JOIN students s ON sq.student_id = s.student_id " +
                "ORDER BY sq.query_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                queries.add(new Object[]{
                        rs.getInt("query_id"),
                        rs.getString("full_name"),
                        rs.getString("registration_number"),
                        rs.getString("subject"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queries;
    }
}