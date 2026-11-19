package org.example.ui;

import org.example.dao.QuizAttemptDAO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizAttemptFrame extends JFrame {

    private final int assignmentId;
    private final int studentId;
    private final Runnable onSubmitted;

    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    private int attemptId;
    private int currentQuestion = 0;
    private int remainingSeconds;

    private List<QuizAttemptDAO.QuestionData> questions;

    private final Map<Integer, Integer> answers = new HashMap<>();

    private JLabel titleLabel;
    private JLabel timerLabel;
    private JLabel questionNumberLabel;
    private JLabel questionLabel;

    private JPanel optionsPanel;

    private ButtonGroup optionGroup;

    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;

    private Timer timer;

    public QuizAttemptFrame(
            int assignmentId,
            int studentId,
            Runnable onSubmitted
    ) {

        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.onSubmitted = onSubmitted;

        initializeQuiz();
    }

    // ==========================================
    // INITIALIZE QUIZ
    // ==========================================

    private void initializeQuiz() {

        QuizAttemptDAO.AssignmentInfo info =
                attemptDAO.getAssignmentInfo(
                        assignmentId,
                        studentId
                );

        if (info == null) {

            showError("Unable to find this quiz assignment.");
            return;
        }

        questions = attemptDAO.getQuestionsForQuiz(
                info.getQuizId()
        );

        if (questions.isEmpty()) {

            showError(
                    "No questions are available in this quiz."
            );

            return;
        }

        for (QuizAttemptDAO.QuestionData question : questions) {

            if (question.getOptions().size() != 4) {

                showError(
                        "Question " + question.getQuestionId()
                                + " does not have exactly four options."
                );

                return;
            }
        }

        QuizAttemptDAO.StartAttemptResult result =
                attemptDAO.startAttempt(
                        assignmentId,
                        studentId
                );

        if (!result.isSuccess()) {

            showError(result.getMessage());
            return;
        }

        attemptId = result.getAttemptId();

        remainingSeconds =
                info.getDurationMinutes() * 60;

        if (remainingSeconds <= 0) {

            remainingSeconds = 60;
        }

        createInterface(info.getQuizTitle());

        displayQuestion();

        startTimer();

        setVisible(true);
    }

    // ==========================================
    // CREATE QUIZ INTERFACE
    // ==========================================

    private void createInterface(String quizTitle) {

        setTitle("Quiz Attempt - " + quizTitle);

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setSize(1050, 720);

        setMinimumSize(
                new Dimension(850, 600)
        );

        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(
                new BorderLayout(20, 20)
        );

        mainPanel.setBackground(
                new Color(240, 243, 248)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        // ======================================
        // TOP HEADER
        // ======================================

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setBackground(
                new Color(25, 45, 80)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 22, 18, 22
                )
        );

        titleLabel = new JLabel(quizTitle);

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 23)
        );

        titleLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("Time: 00:00");

        timerLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        timerLabel.setForeground(Color.WHITE);

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        headerPanel.add(
                timerLabel,
                BorderLayout.EAST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // ======================================
        // QUESTION AREA
        // ======================================

        JPanel centerPanel = new JPanel(
                new BorderLayout(15, 15)
        );

        centerPanel.setBackground(
                new Color(240, 243, 248)
        );

        JPanel questionCard = new JPanel(
                new BorderLayout(15, 20)
        );

        questionCard.setBackground(Color.WHITE);

        questionCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                30, 30, 30, 30
                        )
                )
        );

        questionNumberLabel = new JLabel();

        questionNumberLabel.setFont(
                new Font("Arial", Font.BOLD, 17)
        );

        questionNumberLabel.setForeground(
                new Color(55, 125, 220)
        );

        questionCard.add(
                questionNumberLabel,
                BorderLayout.NORTH
        );

        questionLabel = new JLabel();

        questionLabel.setFont(
                new Font("Arial", Font.BOLD, 21)
        );

        questionLabel.setForeground(
                new Color(25, 45, 80)
        );

        questionCard.add(
                questionLabel,
                BorderLayout.CENTER
        );

        optionsPanel = new JPanel();

        optionsPanel.setLayout(
                new BoxLayout(
                        optionsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        optionsPanel.setBackground(Color.WHITE);

        questionCard.add(
                optionsPanel,
                BorderLayout.SOUTH
        );

        centerPanel.add(
                questionCard,
                BorderLayout.CENTER
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        // ======================================
        // BOTTOM NAVIGATION
        // ======================================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 20, 10)
        );

        bottomPanel.setBackground(
                new Color(240, 243, 248)
        );

        previousButton = createButton("PREVIOUS");

        nextButton = createButton("NEXT");

        submitButton = createButton("SUBMIT QUIZ");

        previousButton.addActionListener(
                e -> showPreviousQuestion()
        );

        nextButton.addActionListener(
                e -> showNextQuestion()
        );

        submitButton.addActionListener(
                e -> confirmSubmission()
        );

        bottomPanel.add(previousButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(submitButton);

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        setContentPane(mainPanel);
    }

    // ==========================================
    // DISPLAY QUESTION
    // ==========================================

    private void displayQuestion() {

        QuizAttemptDAO.QuestionData question =
                questions.get(currentQuestion);

        questionNumberLabel.setText(
                "QUESTION " + (currentQuestion + 1)
                        + " OF " + questions.size()
                        + "     |     MARKS: " + question.getMarks()
        );

        questionLabel.setText(
                "<html><div style='width:750px;'>"
                        + question.getQuestionText()
                        + "</div></html>"
        );

        optionsPanel.removeAll();

        optionGroup = new ButtonGroup();

        Integer selectedOption =
                answers.get(question.getQuestionId());

        for (QuizAttemptDAO.OptionData option :
                question.getOptions()) {

            JRadioButton radioButton = new JRadioButton(
                    option.getOptionLabel()
                            + ". "
                            + option.getOptionText()
            );

            radioButton.setFont(
                    new Font("Arial", Font.PLAIN, 17)
            );

            radioButton.setBackground(Color.WHITE);

            radioButton.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            radioButton.setBorder(
                    BorderFactory.createEmptyBorder(
                            12, 5, 12, 5
                    )
            );

            radioButton.setActionCommand(
                    String.valueOf(option.getOptionId())
            );

            if (selectedOption != null
                    && selectedOption == option.getOptionId()) {

                radioButton.setSelected(true);
            }

            optionGroup.add(radioButton);

            optionsPanel.add(radioButton);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();

        previousButton.setEnabled(
                currentQuestion > 0
        );

        nextButton.setEnabled(
                currentQuestion < questions.size() - 1
        );
    }

    // ==========================================
    // SAVE CURRENT ANSWER
    // ==========================================

    private void saveCurrentAnswer() {

        if (optionGroup == null) {
            return;
        }

        ButtonModel selected =
                optionGroup.getSelection();

        QuizAttemptDAO.QuestionData question =
                questions.get(currentQuestion);

        if (selected != null) {

            answers.put(
                    question.getQuestionId(),
                    Integer.parseInt(selected.getActionCommand())
            );

        } else {

            answers.remove(question.getQuestionId());
        }
    }

    // ==========================================
    // NEXT QUESTION
    // ==========================================

    private void showNextQuestion() {

        saveCurrentAnswer();

        if (currentQuestion < questions.size() - 1) {

            currentQuestion++;

            displayQuestion();
        }
    }

    // ==========================================
    // PREVIOUS QUESTION
    // ==========================================

    private void showPreviousQuestion() {

        saveCurrentAnswer();

        if (currentQuestion > 0) {

            currentQuestion--;

            displayQuestion();
        }
    }

    // ==========================================
    // START COUNTDOWN TIMER
    // ==========================================

    private void startTimer() {

        updateTimerLabel();

        timer = new Timer(1000, e -> {

            remainingSeconds--;

            updateTimerLabel();

            if (remainingSeconds <= 0) {

                timer.stop();

                JOptionPane.showMessageDialog(
                        this,
                        "Time is over. Your quiz will be submitted automatically.",
                        "Time Over",
                        JOptionPane.INFORMATION_MESSAGE
                );

                submitQuiz();
            }
        });

        timer.start();
    }

    // ==========================================
    // UPDATE TIMER LABEL
    // ==========================================

    private void updateTimerLabel() {

        int minutes = remainingSeconds / 60;

        int seconds = remainingSeconds % 60;

        timerLabel.setText(
                String.format(
                        "Time: %02d:%02d",
                        minutes,
                        seconds
                )
        );

        if (remainingSeconds <= 300) {

            timerLabel.setForeground(
                    Color.RED
            );

        } else {

            timerLabel.setForeground(
                    Color.WHITE
            );
        }
    }

    // ==========================================
    // CONFIRM SUBMISSION
    // ==========================================

    private void confirmSubmission() {

        saveCurrentAnswer();

        int unanswered = 0;

        for (QuizAttemptDAO.QuestionData question : questions) {

            if (!answers.containsKey(question.getQuestionId())) {

                unanswered++;
            }
        }

        String message =
                "Are you sure you want to submit your quiz?\n\n"
                        + "Total Questions: " + questions.size()
                        + "\nUnanswered Questions: " + unanswered;

        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            submitQuiz();
        }
    }

    // ==========================================
    // SUBMIT QUIZ TO DATABASE
    // ==========================================

    private void submitQuiz() {

        if (timer != null) {
            timer.stop();
        }

        saveCurrentAnswer();

        submitButton.setEnabled(false);

        boolean success = attemptDAO.submitAttempt(
                attemptId,
                assignmentId,
                studentId,
                answers
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Your quiz has been submitted successfully!\n"
                            + "You can check your result in My Results.",
                    "Quiz Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (onSubmitted != null) {
                onSubmitted.run();
            }

        } else {

            submitButton.setEnabled(true);

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to submit the quiz. Please try again.",
                    "Submission Error",
                    JOptionPane.ERROR_MESSAGE
            );

            if (remainingSeconds > 0 && timer != null) {
                timer.start();
            }
        }
    }

    // ==========================================
    // CREATE BUTTON
    // ==========================================

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        button.setBackground(
                new Color(55, 125, 220)
        );

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 25, 12, 25
                )
        );

        return button;
    }

    // ==========================================
    // SHOW ERROR
    // ==========================================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                null,
                message,
                "Quiz Error",
                JOptionPane.ERROR_MESSAGE
        );

        dispose();
    }
}