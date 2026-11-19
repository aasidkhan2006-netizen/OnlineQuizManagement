package org.example.ui;

import org.example.dao.QuizAttemptDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class QuizReviewFrame extends JFrame {

    private final int attemptId;
    private final int studentId;
    private final String quizTitle;

    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    private JPanel questionsPanel;

    public QuizReviewFrame(
            int attemptId,
            int studentId,
            String quizTitle
    ) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.quizTitle = quizTitle;

        initializeUI();
        loadReview();
    }

    private void initializeUI() {

        setTitle("Quiz Review - " + quizTitle);
        setSize(1000, 750);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 251));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("QUIZ REVIEW");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(new Color(30, 55, 95));

        JLabel subtitleLabel = new JLabel(quizTitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitleLabel);

        headerPanel.add(heading, BorderLayout.WEST);

        JButton closeButton = new JButton("CLOSE");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(45, 90, 160));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(e -> dispose());

        headerPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // QUESTIONS AREA
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(
                questionsPanel, BoxLayout.Y_AXIS
        ));
        questionsPanel.setBackground(new Color(245, 247, 251));

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(
                new Color(245, 247, 251)
        );

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadReview() {

        List<QuizAttemptDAO.ReviewQuestionData> review =
                attemptDAO.getQuizReview(attemptId, studentId);

        questionsPanel.removeAll();

        if (review == null || review.isEmpty()) {

            JLabel emptyLabel = new JLabel(
                    "<html><div style='text-align:center;'>"
                            + "No review details are available.<br><br>"
                            + "Please check whether this quiz has been submitted."
                            + "</div></html>"
            );

            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 17));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            questionsPanel.add(Box.createVerticalStrut(100));
            questionsPanel.add(emptyLabel);

        } else {

            int questionNumber = 1;

            for (QuizAttemptDAO.ReviewQuestionData question : review) {

                questionsPanel.add(
                        createQuestionCard(question, questionNumber++)
                );

                questionsPanel.add(Box.createVerticalStrut(15));
            }
        }

        questionsPanel.revalidate();
        questionsPanel.repaint();
    }

    private JPanel createQuestionCard(
            QuizAttemptDAO.ReviewQuestionData question,
            int questionNumber
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 235)
                ),
                new EmptyBorder(20, 22, 20, 22)
        ));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // QUESTION HEADING
        JLabel questionLabel = new JLabel(
                "Question " + questionNumber
                        + "  |  " + question.getQuestionMarks() + " Marks"
        );

        questionLabel.setFont(new Font("Arial", Font.BOLD, 15));
        questionLabel.setForeground(new Color(45, 90, 160));

        card.add(questionLabel);
        card.add(Box.createVerticalStrut(12));

        // QUESTION TEXT
        JTextArea questionText = new JTextArea(
                question.getQuestionText()
        );

        questionText.setFont(new Font("Arial", Font.BOLD, 17));
        questionText.setForeground(new Color(35, 45, 60));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setFocusable(false);
        questionText.setOpaque(false);

        card.add(questionText);
        card.add(Box.createVerticalStrut(18));

        // SELECTED ANSWER
        String selectedAnswer;

        if (question.getSelectedOptionText() == null) {
            selectedAnswer = "Not answered";
        } else {
            selectedAnswer =
                    question.getSelectedOptionLabel()
                            + ". " + question.getSelectedOptionText();
        }

        JLabel selectedLabel = new JLabel(
                "<html><b>Your Answer:</b> "
                        + escapeHtml(selectedAnswer)
                        + "</html>"
        );

        selectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectedLabel.setForeground(new Color(65, 75, 90));

        card.add(selectedLabel);
        card.add(Box.createVerticalStrut(10));

        // CORRECT ANSWER
        String correctAnswer;

        if (question.getCorrectOptionText() == null) {
            correctAnswer = "Correct answer not available";
        } else {
            correctAnswer =
                    question.getCorrectOptionLabel()
                            + ". " + question.getCorrectOptionText();
        }

        JLabel correctLabel = new JLabel(
                "<html><b>Correct Answer:</b> "
                        + escapeHtml(correctAnswer)
                        + "</html>"
        );

        correctLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        correctLabel.setForeground(new Color(35, 145, 90));

        card.add(correctLabel);
        card.add(Box.createVerticalStrut(15));

        // RESULT STATUS
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(
                question.isCorrect()
                        ? "✓ CORRECT"
                        : "✗ INCORRECT"
        );

        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel.setForeground(
                question.isCorrect()
                        ? new Color(30, 145, 85)
                        : new Color(210, 65, 65)
        );

        JLabel marksLabel = new JLabel(
                "Marks Awarded: "
                        + question.getMarksAwarded()
                        + " / " + question.getQuestionMarks()
        );

        marksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        marksLabel.setForeground(new Color(45, 90, 160));

        resultPanel.add(statusLabel, BorderLayout.WEST);
        resultPanel.add(marksLabel, BorderLayout.EAST);

        card.add(resultPanel);

        return card;
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}