package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.QuizAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAssignmentDAO {

    // Get all quiz assignments (Admin)
    public List<QuizAssignment> getAllAssignments() {

        List<QuizAssignment> assignments = new ArrayList<>();

        String sql = "SELECT * FROM quiz_assignments ORDER BY assignment_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                assignments.add(new QuizAssignment(
                        rs.getInt("assignment_id"),
                        rs.getInt("quiz_id"),
                        rs.getInt("student_id"),
                        rs.getTimestamp("assigned_at"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("deadline"),
                        rs.getBoolean("allow_retake"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    // Get assignments for a specific student
    public List<QuizAssignment> getAssignmentsForStudent(int studentId) {

        List<QuizAssignment> assignments = new ArrayList<>();

        String sql = "SELECT * FROM quiz_assignments " +
                "WHERE student_id = ? " +
                "ORDER BY assignment_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    assignments.add(new QuizAssignment(
                            rs.getInt("assignment_id"),
                            rs.getInt("quiz_id"),
                            rs.getInt("student_id"),
                            rs.getTimestamp("assigned_at"),
                            rs.getTimestamp("start_date"),
                            rs.getTimestamp("deadline"),
                            rs.getBoolean("allow_retake"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    // Get complete quiz details assigned to a specific student
    public List<Object[]> getAssignedQuizDetailsForStudent(int studentId) {

        List<Object[]> quizList = new ArrayList<>();

        String sql = "SELECT " +
                "qa.assignment_id, " +
                "q.quiz_title, " +
                "q.subject, " +
                "q.total_marks, " +
                "q.duration_minutes, " +
                "qa.start_date, " +
                "qa.deadline, " +
                "qa.allow_retake, " +
                "qa.status AS assignment_status, " +
                "q.status AS quiz_status " +
                "FROM quiz_assignments qa " +
                "INNER JOIN quizzes q ON qa.quiz_id = q.quiz_id " +
                "WHERE qa.student_id = ? " +
                "ORDER BY qa.start_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    quizList.add(new Object[]{
                            rs.getInt("assignment_id"),
                            rs.getString("quiz_title"),
                            rs.getString("subject"),
                            rs.getInt("total_marks"),
                            rs.getInt("duration_minutes"),
                            rs.getTimestamp("start_date"),
                            rs.getTimestamp("deadline"),
                            rs.getBoolean("allow_retake"),
                            rs.getString("assignment_status"),
                            rs.getString("quiz_status")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizList;
    }
    // Assign quiz to student
    public boolean assignQuiz(int quizId, int studentId,
                              Timestamp startDate, Timestamp deadline,
                              boolean allowRetake) {

        String sql = "INSERT INTO quiz_assignments " +
                "(quiz_id, student_id, start_date, deadline, allow_retake) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            ps.setInt(2, studentId);
            ps.setTimestamp(3, startDate);
            ps.setTimestamp(4, deadline);
            ps.setBoolean(5, allowRetake);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update assignment status
    public boolean updateStatus(int assignmentId, String status) {

        String sql = "UPDATE quiz_assignments SET status=? " +
                "WHERE assignment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, assignmentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete assignment
    public boolean deleteAssignment(int assignmentId) {

        String sql = "DELETE FROM quiz_assignments WHERE assignment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}