package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.QuizAttempt;

import java.sql.*;
import java.util.*;

public class QuizAttemptDAO {

    private static final double PASS_PERCENTAGE = 40.0;

    // ==========================================
    // RESULT CLASS FOR STARTING A QUIZ
    // ==========================================

    public static class StartAttemptResult {
        private final boolean success;
        private final int attemptId;
        private final String message;

        public StartAttemptResult(boolean success, int attemptId, String message) {
            this.success = success;
            this.attemptId = attemptId;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getAttemptId() {
            return attemptId;
        }

        public String getMessage() {
            return message;
        }
    }

    // ==========================================
    // QUESTION DATA
    // ==========================================

    public static class OptionData {
        private final int optionId;
        private final String optionLabel;
        private final String optionText;

        public OptionData(int optionId, String optionLabel, String optionText) {
            this.optionId = optionId;
            this.optionLabel = optionLabel;
            this.optionText = optionText;
        }

        public int getOptionId() {
            return optionId;
        }

        public String getOptionLabel() {
            return optionLabel;
        }

        public String getOptionText() {
            return optionText;
        }
    }

    public static class QuestionData {
        private final int questionId;
        private final String questionText;
        private final double marks;
        private final List<OptionData> options;

        public QuestionData(int questionId, String questionText, double marks) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.marks = marks;
            this.options = new ArrayList<>();
        }

        public int getQuestionId() {
            return questionId;
        }

        public String getQuestionText() {
            return questionText;
        }

        public double getMarks() {
            return marks;
        }

        public List<OptionData> getOptions() {
            return options;
        }
    }

    // ==========================================
    // ASSIGNMENT DETAILS
    // ==========================================

    public static class AssignmentInfo {
        private final int quizId;
        private final String quizTitle;
        private final int totalMarks;
        private final int durationMinutes;

        public AssignmentInfo(
                int quizId,
                String quizTitle,
                int totalMarks,
                int durationMinutes
        ) {
            this.quizId = quizId;
            this.quizTitle = quizTitle;
            this.totalMarks = totalMarks;
            this.durationMinutes = durationMinutes;
        }

        public int getQuizId() {
            return quizId;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }
    }


    // ==========================================
    // QUIZ REVIEW DATA
    // ==========================================

    public static class ReviewQuestionData {
        private final int questionId;
        private final String questionText;
        private final double questionMarks;
        private final String selectedOptionLabel;
        private final String selectedOptionText;
        private final String correctOptionLabel;
        private final String correctOptionText;
        private final boolean correct;
        private final double marksAwarded;

        public ReviewQuestionData(
                int questionId,
                String questionText,
                double questionMarks,
                String selectedOptionLabel,
                String selectedOptionText,
                String correctOptionLabel,
                String correctOptionText,
                boolean correct,
                double marksAwarded
        ) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.questionMarks = questionMarks;
            this.selectedOptionLabel = selectedOptionLabel;
            this.selectedOptionText = selectedOptionText;
            this.correctOptionLabel = correctOptionLabel;
            this.correctOptionText = correctOptionText;
            this.correct = correct;
            this.marksAwarded = marksAwarded;
        }

        public int getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public double getQuestionMarks() { return questionMarks; }
        public String getSelectedOptionLabel() { return selectedOptionLabel; }
        public String getSelectedOptionText() { return selectedOptionText; }
        public String getCorrectOptionLabel() { return correctOptionLabel; }
        public String getCorrectOptionText() { return correctOptionText; }
        public boolean isCorrect() { return correct; }
        public double getMarksAwarded() { return marksAwarded; }
    }

    // ==========================================
    // START QUIZ ATTEMPT
    // ==========================================

    public StartAttemptResult startAttempt(int assignmentId, int studentId) {

        String assignmentSql =
                "SELECT qa.quiz_id, qa.start_date, qa.deadline, " +
                        "qa.allow_retake, qa.status AS assignment_status, " +
                        "q.status AS quiz_status " +
                        "FROM quiz_assignments qa " +
                        "JOIN quizzes q ON qa.quiz_id = q.quiz_id " +
                        "WHERE qa.assignment_id = ? AND qa.student_id = ? " +
                        "FOR UPDATE";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int quizId;
                boolean allowRetake;
                Timestamp startDate;
                Timestamp deadline;
                String assignmentStatus;
                String quizStatus;

                try (PreparedStatement ps =
                             con.prepareStatement(assignmentSql)) {

                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return new StartAttemptResult(
                                    false, -1,
                                    "This quiz is not assigned to your account."
                            );
                        }

                        quizId = rs.getInt("quiz_id");
                        startDate = rs.getTimestamp("start_date");
                        deadline = rs.getTimestamp("deadline");
                        allowRetake = rs.getBoolean("allow_retake");
                        assignmentStatus =
                                rs.getString("assignment_status");
                        quizStatus = rs.getString("quiz_status");
                    }
                }

                Timestamp now = new Timestamp(System.currentTimeMillis());

                if (!"ACTIVE".equalsIgnoreCase(quizStatus)) {
                    con.rollback();
                    return new StartAttemptResult(
                            false, -1,
                            "This quiz is currently inactive."
                    );
                }

                if (startDate != null && now.before(startDate)) {
                    con.rollback();
                    return new StartAttemptResult(
                            false, -1,
                            "The quiz has not started yet."
                    );
                }

                if (deadline != null && now.after(deadline)) {
                    con.rollback();
                    return new StartAttemptResult(
                            false, -1,
                            "The deadline for this quiz has passed."
                    );
                }

                if (!"PENDING".equalsIgnoreCase(assignmentStatus)
                        && !("COMPLETED".equalsIgnoreCase(assignmentStatus)
                        && allowRetake)) {

                    con.rollback();

                    return new StartAttemptResult(
                            false, -1,
                            "This assignment cannot be attempted again."
                    );
                }

                // Check for an unfinished attempt.

                String unfinishedSql =
                        "SELECT attempt_id FROM quiz_attempts " +
                                "WHERE assignment_id = ? " +
                                "AND student_id = ? AND submitted_at IS NULL " +
                                "LIMIT 1";

                try (PreparedStatement ps =
                             con.prepareStatement(unfinishedSql)) {

                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            con.rollback();

                            return new StartAttemptResult(
                                    false, -1,
                                    "An unfinished quiz attempt already exists."
                            );
                        }
                    }
                }

                // Check previous attempts if retakes are disabled.

                if (!allowRetake) {

                    String previousSql =
                            "SELECT COUNT(*) FROM quiz_attempts " +
                                    "WHERE assignment_id = ? AND student_id = ?";

                    try (PreparedStatement ps =
                                 con.prepareStatement(previousSql)) {

                        ps.setInt(1, assignmentId);
                        ps.setInt(2, studentId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (rs.next() && rs.getInt(1) > 0) {
                                con.rollback();

                                return new StartAttemptResult(
                                        false, -1,
                                        "You have already attempted this quiz."
                                );
                            }
                        }
                    }
                }

                // Create new attempt.

                String insertSql =
                        "INSERT INTO quiz_attempts " +
                                "(assignment_id, student_id, started_at, " +
                                "marks_obtained, percentage, result_status) " +
                                "VALUES (?, ?, CURRENT_TIMESTAMP, 0, 0, 'PENDING')";

                int attemptId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertSql,
                        Statement.RETURN_GENERATED_KEYS
                )) {

                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Unable to create quiz attempt."
                            );
                        }

                        attemptId = rs.getInt(1);
                    }
                }

                con.commit();

                return new StartAttemptResult(
                        true,
                        attemptId,
                        "Quiz started successfully."
                );

            } catch (SQLException e) {

                con.rollback();
                e.printStackTrace();

                return new StartAttemptResult(
                        false, -1,
                        "Database error: " + e.getMessage()
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            e.printStackTrace();

            return new StartAttemptResult(
                    false, -1,
                    "Unable to connect to the database."
            );
        }
    }

    // ==========================================
    // GET ASSIGNMENT / QUIZ DETAILS
    // ==========================================

    public AssignmentInfo getAssignmentInfo(
            int assignmentId,
            int studentId
    ) {

        String sql =
                "SELECT q.quiz_id, q.quiz_title, " +
                        "q.total_marks, q.duration_minutes " +
                        "FROM quiz_assignments qa " +
                        "JOIN quizzes q ON qa.quiz_id = q.quiz_id " +
                        "WHERE qa.assignment_id = ? AND qa.student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);
            ps.setInt(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new AssignmentInfo(
                            rs.getInt("quiz_id"),
                            rs.getString("quiz_title"),
                            rs.getInt("total_marks"),
                            rs.getInt("duration_minutes")
                    );
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ==========================================
    // GET QUESTIONS AND OPTIONS
    // ==========================================

    public List<QuestionData> getQuestionsForQuiz(int quizId) {

        List<QuestionData> questions = new ArrayList<>();

        String sql =
                "SELECT q.question_id, q.question_text, q.marks, " +
                        "o.option_id, o.option_label, o.option_text " +
                        "FROM questions q " +
                        "LEFT JOIN quiz_options o " +
                        "ON q.question_id = o.question_id " +
                        "WHERE q.quiz_id = ? " +
                        "ORDER BY q.question_id, o.option_label";

        Map<Integer, QuestionData> questionMap =
                new LinkedHashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int questionId = rs.getInt("question_id");

                    QuestionData question =
                            questionMap.get(questionId);

                    if (question == null) {

                        question = new QuestionData(
                                questionId,
                                rs.getString("question_text"),
                                rs.getDouble("marks")
                        );

                        questionMap.put(questionId, question);
                    }

                    int optionId = rs.getInt("option_id");

                    if (!rs.wasNull()) {

                        question.getOptions().add(
                                new OptionData(
                                        optionId,
                                        rs.getString("option_label"),
                                        rs.getString("option_text")
                                )
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        questions.addAll(questionMap.values());

        return questions;
    }

    // ==========================================
    // SUBMIT QUIZ AND CALCULATE RESULT
    // ==========================================

    public boolean submitAttempt(
            int attemptId,
            int assignmentId,
            int studentId,
            Map<Integer, Integer> answers
    ) {

        String attemptSql =
                "SELECT qa.quiz_id, q.total_marks " +
                        "FROM quiz_attempts a " +
                        "JOIN quiz_assignments qa " +
                        "ON a.assignment_id = qa.assignment_id " +
                        "JOIN quizzes q ON qa.quiz_id = q.quiz_id " +
                        "WHERE a.attempt_id = ? " +
                        "AND a.assignment_id = ? " +
                        "AND a.student_id = ? " +
                        "AND a.submitted_at IS NULL " +
                        "FOR UPDATE";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int quizId;
                double totalMarks;

                try (PreparedStatement ps =
                             con.prepareStatement(attemptSql)) {

                    ps.setInt(1, attemptId);
                    ps.setInt(2, assignmentId);
                    ps.setInt(3, studentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        quizId = rs.getInt("quiz_id");
                        totalMarks = rs.getDouble("total_marks");
                    }
                }

                String questionsSql =
                        "SELECT q.question_id, q.marks, " +
                                "(SELECT o.option_id FROM quiz_options o " +
                                "WHERE o.question_id = q.question_id " +
                                "AND o.is_correct = 1 LIMIT 1) AS correct_option_id " +
                                "FROM questions q WHERE q.quiz_id = ?";

                double marksObtained = 0;

                try (PreparedStatement ps =
                             con.prepareStatement(questionsSql)) {

                    ps.setInt(1, quizId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {

                            int questionId =
                                    rs.getInt("question_id");

                            double questionMarks =
                                    rs.getDouble("marks");

                            int correctOptionId =
                                    rs.getInt("correct_option_id");

                            Integer selectedOptionId =
                                    answers.get(questionId);

                            boolean isCorrect = false;
                            double marksAwarded = 0;

                            if (selectedOptionId != null) {

                                // Verify the option belongs to this question.

                                String validateSql =
                                        "SELECT COUNT(*) FROM quiz_options " +
                                                "WHERE option_id = ? AND question_id = ?";

                                try (PreparedStatement validatePs =
                                             con.prepareStatement(validateSql)) {

                                    validatePs.setInt(1, selectedOptionId);
                                    validatePs.setInt(2, questionId);

                                    try (ResultSet validateRs =
                                                 validatePs.executeQuery()) {

                                        if (!validateRs.next()
                                                || validateRs.getInt(1) == 0) {

                                            throw new SQLException(
                                                    "Invalid answer selected."
                                            );
                                        }
                                    }
                                }

                                isCorrect =
                                        selectedOptionId == correctOptionId;

                                if (isCorrect) {
                                    marksAwarded = questionMarks;
                                }
                            }

                            marksObtained += marksAwarded;

                            String answerSql =
                                    "INSERT INTO student_answers " +
                                            "(attempt_id, question_id, " +
                                            "selected_option_id, is_correct, marks_awarded) " +
                                            "VALUES (?, ?, ?, ?, ?) " +
                                            "ON DUPLICATE KEY UPDATE " +
                                            "selected_option_id = VALUES(selected_option_id), " +
                                            "is_correct = VALUES(is_correct), " +
                                            "marks_awarded = VALUES(marks_awarded)";

                            try (PreparedStatement answerPs =
                                         con.prepareStatement(answerSql)) {

                                answerPs.setInt(1, attemptId);
                                answerPs.setInt(2, questionId);

                                if (selectedOptionId == null) {
                                    answerPs.setNull(3, Types.INTEGER);
                                } else {
                                    answerPs.setInt(3, selectedOptionId);
                                }

                                answerPs.setBoolean(4, isCorrect);
                                answerPs.setDouble(5, marksAwarded);

                                answerPs.executeUpdate();
                            }
                        }
                    }
                }

                double percentage = totalMarks > 0
                        ? (marksObtained / totalMarks) * 100
                        : 0;

                String resultStatus =
                        percentage >= PASS_PERCENTAGE ? "PASS" : "FAIL";

                String updateAttemptSql =
                        "UPDATE quiz_attempts SET " +
                                "submitted_at = CURRENT_TIMESTAMP, " +
                                "marks_obtained = ?, " +
                                "percentage = ?, " +
                                "result_status = ? " +
                                "WHERE attempt_id = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(updateAttemptSql)) {

                    ps.setDouble(1, marksObtained);
                    ps.setDouble(2, percentage);
                    ps.setString(3, resultStatus);
                    ps.setInt(4, attemptId);

                    ps.executeUpdate();
                }

                String updateAssignmentSql =
                        "UPDATE quiz_assignments " +
                                "SET status = 'COMPLETED' " +
                                "WHERE assignment_id = ? AND student_id = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(updateAssignmentSql)) {

                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);

                    ps.executeUpdate();
                }

                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();
                e.printStackTrace();

                return false;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    // ==========================================
    // GET DETAILED REVIEW FOR A SUBMITTED ATTEMPT
    // ==========================================

    public List<ReviewQuestionData> getQuizReview(int attemptId, int studentId) {

        List<ReviewQuestionData> review = new ArrayList<>();

        String sql =
                "SELECT q.question_id, q.question_text, q.marks AS question_marks, " +
                        "selected.option_label AS selected_label, " +
                        "selected.option_text AS selected_text, " +
                        "correct.option_label AS correct_label, " +
                        "correct.option_text AS correct_text, " +
                        "sa.is_correct, sa.marks_awarded " +
                        "FROM quiz_attempts a " +
                        "JOIN student_answers sa ON a.attempt_id = sa.attempt_id " +
                        "JOIN questions q ON sa.question_id = q.question_id " +
                        "LEFT JOIN quiz_options selected " +
                        "ON sa.selected_option_id = selected.option_id " +
                        "LEFT JOIN quiz_options correct " +
                        "ON correct.question_id = q.question_id AND correct.is_correct = 1 " +
                        "WHERE a.attempt_id = ? AND a.student_id = ? " +
                        "AND a.submitted_at IS NOT NULL " +
                        "ORDER BY q.question_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attemptId);
            ps.setInt(2, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    review.add(new ReviewQuestionData(
                            rs.getInt("question_id"),
                            rs.getString("question_text"),
                            rs.getDouble("question_marks"),
                            rs.getString("selected_label"),
                            rs.getString("selected_text"),
                            rs.getString("correct_label"),
                            rs.getString("correct_text"),
                            rs.getBoolean("is_correct"),
                            rs.getDouble("marks_awarded")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return review;
    }

    // ==========================================
    // GET ALL STUDENT QUIZ ATTEMPTS (ADMIN)
    // ==========================================

    public List<QuizAttempt> getAllAttempts() {

        List<QuizAttempt> attempts = new ArrayList<>();

        String sql =
                "SELECT * FROM quiz_attempts ORDER BY attempt_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                attempts.add(new QuizAttempt(
                        rs.getInt("attempt_id"),
                        rs.getInt("assignment_id"),
                        rs.getInt("student_id"),
                        rs.getTimestamp("started_at"),
                        rs.getTimestamp("submitted_at"),
                        rs.getDouble("marks_obtained"),
                        rs.getDouble("percentage"),
                        rs.getString("result_status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attempts;
    }

    // ==========================================
    // GET RESULTS FOR A SPECIFIC STUDENT
    // ==========================================

    public List<Object[]> getResultsForStudent(int studentId) {

        List<Object[]> results = new ArrayList<>();

        String sql =
                "SELECT qa.attempt_id, " +
                        "s.full_name, " +
                        "s.registration_number, " +
                        "q.quiz_title, " +
                        "qa.marks_obtained, " +
                        "q.total_marks, " +
                        "qa.percentage, " +
                        "qa.result_status, " +
                        "qa.started_at, " +
                        "qa.submitted_at " +
                        "FROM quiz_attempts qa " +
                        "JOIN students s ON qa.student_id = s.student_id " +
                        "JOIN quiz_assignments a ON qa.assignment_id = a.assignment_id " +
                        "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
                        "WHERE qa.student_id = ? " +
                        "ORDER BY qa.attempt_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    results.add(new Object[]{
                            rs.getInt("attempt_id"),
                            rs.getString("full_name"),
                            rs.getString("registration_number"),
                            rs.getString("quiz_title"),
                            rs.getDouble("marks_obtained"),
                            rs.getDouble("total_marks"),
                            rs.getDouble("percentage"),
                            rs.getString("result_status"),
                            rs.getTimestamp("started_at"),
                            rs.getTimestamp("submitted_at")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // ==========================================
    // UPDATE RESULT STATUS (ADMIN)
    // ==========================================

    public boolean updateResultStatus(int attemptId, String status) {

        String sql =
                "UPDATE quiz_attempts SET result_status=? " +
                        "WHERE attempt_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, attemptId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // GET ALL STUDENT RESULTS (ADMIN)
    // ==========================================

    public List<Object[]> getAllResultDetails() {

        List<Object[]> results = new ArrayList<>();

        String sql =
                "SELECT qa.attempt_id, " +
                        "s.full_name, " +
                        "s.registration_number, " +
                        "q.quiz_title, " +
                        "qa.marks_obtained, " +
                        "q.total_marks, " +
                        "qa.percentage, " +
                        "qa.result_status, " +
                        "qa.started_at, " +
                        "qa.submitted_at " +
                        "FROM quiz_attempts qa " +
                        "JOIN students s ON qa.student_id = s.student_id " +
                        "JOIN quiz_assignments a ON qa.assignment_id = a.assignment_id " +
                        "JOIN quizzes q ON a.quiz_id = q.quiz_id " +
                        "ORDER BY qa.attempt_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                results.add(new Object[]{
                        rs.getInt("attempt_id"),
                        rs.getString("full_name"),
                        rs.getString("registration_number"),
                        rs.getString("quiz_title"),
                        rs.getDouble("marks_obtained"),
                        rs.getDouble("total_marks"),
                        rs.getDouble("percentage"),
                        rs.getString("result_status"),
                        rs.getTimestamp("started_at"),
                        rs.getTimestamp("submitted_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}