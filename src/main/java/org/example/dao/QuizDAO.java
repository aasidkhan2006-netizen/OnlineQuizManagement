package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    // Fetch all quizzes
    public List<Quiz> getAllQuizzes() {

        List<Quiz> quizzes = new ArrayList<>();

        String sql = "SELECT * FROM quizzes ORDER BY quiz_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("quiz_title"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getInt("total_questions"),
                        rs.getDouble("total_marks"),
                        rs.getInt("duration_minutes"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizzes;
    }

    // Add a new quiz
    public boolean addQuiz(String title, String subject,
                           String description, int questions,
                           double marks, int duration) {

        String sql = "INSERT INTO quizzes " +
                "(quiz_title, subject, description, total_questions, " +
                "total_marks, duration_minutes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, subject);
            ps.setString(3, description);
            ps.setInt(4, questions);
            ps.setDouble(5, marks);
            ps.setInt(6, duration);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing quiz
    public boolean updateQuiz(int quizId, String title,
                              String subject, String description,
                              int questions, double marks,
                              int duration, String status) {

        String sql = "UPDATE quizzes SET quiz_title=?, subject=?, " +
                "description=?, total_questions=?, total_marks=?, " +
                "duration_minutes=?, status=? WHERE quiz_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, subject);
            ps.setString(3, description);
            ps.setInt(4, questions);
            ps.setDouble(5, marks);
            ps.setInt(6, duration);
            ps.setString(7, status);
            ps.setInt(8, quizId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a quiz
    public boolean deleteQuiz(int quizId) {

        String sql = "DELETE FROM quizzes WHERE quiz_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Unable to delete quiz. It may have related records.");
            e.printStackTrace();
            return false;
        }
    }

    // Search quizzes
    public List<Quiz> searchQuiz(String keyword) {

        List<Quiz> quizzes = new ArrayList<>();

        String sql = "SELECT * FROM quizzes WHERE quiz_title LIKE ? " +
                "OR subject LIKE ? ORDER BY quiz_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("quiz_title"),
                        rs.getString("subject"),
                        rs.getString("description"),
                        rs.getInt("total_questions"),
                        rs.getDouble("total_marks"),
                        rs.getInt("duration_minutes"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizzes;
    }
}