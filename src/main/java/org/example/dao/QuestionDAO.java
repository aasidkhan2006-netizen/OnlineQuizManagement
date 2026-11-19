package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public boolean addQuestion(
            int quizId,
            String questionText,
            int marks,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctOption) {

        String questionSQL =
                "INSERT INTO questions (quiz_id, question_text, marks) VALUES (?, ?, ?)";

        String optionSQL =
                "INSERT INTO quiz_options (question_id, option_label, option_text, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int questionId;

                try (PreparedStatement ps =
                             con.prepareStatement(questionSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, quizId);
                    ps.setString(2, questionText);
                    ps.setInt(3, marks);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Question ID was not generated.");
                        }
                        questionId = rs.getInt(1);
                    }
                }

                String[] options = {
                        optionA, optionB, optionC, optionD
                };

                String[] labels = {"A", "B", "C", "D"};

                try (PreparedStatement ps = con.prepareStatement(optionSQL)) {

                    for (int i = 0; i < 4; i++) {
                        ps.setInt(1, questionId);
                        ps.setString(2, labels[i]);
                        ps.setString(3, options[i]);
                        ps.setBoolean(4, labels[i].equals(correctOption));
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Question> getQuestionsByQuiz(int quizId) {

        List<Question> questions = new ArrayList<>();

        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("question_id"),
                            rs.getInt("quiz_id"),
                            rs.getString("question_text"),
                            rs.getInt("marks")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    public boolean deleteQuestion(int questionId) {

        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}