package org.example.ui;

import org.example.dao.QuizAssignmentDAO;
import org.example.dao.QuizAttemptDAO;
import org.example.dao.StudentQueryDAO;
import org.example.model.StudentQuery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final int studentId;
    private final String studentName;

    private CardLayout cardLayout;
    private JPanel contentCards;
    private JButton activeButton;

    private final Color NAVY = new Color(25, 45, 80);
    private final Color BLUE = new Color(55, 125, 220);
    private final Color BACKGROUND = new Color(240, 243, 248);

    private final QuizAssignmentDAO assignmentDAO =
            new QuizAssignmentDAO();

    private final QuizAttemptDAO attemptDAO =
            new QuizAttemptDAO();

    private final StudentQueryDAO queryDAO =
            new StudentQueryDAO();

    private DefaultTableModel quizTableModel;
    private JTable quizTable;

    private DefaultTableModel resultsTableModel;
    private JTable resultsTable;

    private DefaultTableModel queryTableModel;
    private JTable queryTable;

    private JTextField subjectField;
    private JTextArea messageArea;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public StudentDashboard(int studentId, String studentName) {

        this.studentId = studentId;
        this.studentName = studentName;

        setTitle("Online Quiz Management System - Student");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1200, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // SIDEBAR

        JPanel sidebar = new JPanel(
                new GridLayout(8, 1, 5, 5)
        );

        sidebar.setPreferredSize(new Dimension(245, 750));
        sidebar.setBackground(NAVY);

        JLabel logo = new JLabel("  QUIZ MANAGEMENT");
        logo.setFont(new Font("Arial", Font.BOLD, 19));
        logo.setForeground(Color.WHITE);

        sidebar.add(logo);

        // CONTENT AREA

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);

        contentCards.add(createDashboardPanel(), "Dashboard");
        contentCards.add(createAvailableQuizzesPanel(), "Available Quizzes");
        contentCards.add(createMyResultsPanel(), "My Results");
        contentCards.add(createAskQueryPanel(), "Ask a Query");
        contentCards.add(createMyQueriesPanel(), "My Queries");
        contentCards.add(createProfilePanel(), "My Profile");

        // SIDEBAR NAVIGATION

        String[] menuItems = {
                "Dashboard",
                "Available Quizzes",
                "My Results",
                "Ask a Query",
                "My Queries",
                "My Profile",
                "Logout"
        };

        for (String item : menuItems) {

            JButton button = new JButton(item);

            button.setForeground(Color.WHITE);
            button.setBackground(new Color(35, 60, 100));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);

            button.setBorder(
                    BorderFactory.createEmptyBorder(
                            12, 20, 12, 10
                    )
            );

            button.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            sidebar.add(button);

            button.addActionListener(e -> {

                if (item.equals("Logout")) {
                    logout();
                    return;
                }

                if (activeButton != null) {
                    activeButton.setBackground(
                            new Color(35, 60, 100)
                    );
                }

                button.setBackground(BLUE);
                activeButton = button;

                if (item.equals("Available Quizzes")) {
                    loadAvailableQuizzes();
                }

                if (item.equals("My Results")) {
                    loadMyResults();
                }

                if (item.equals("My Queries")) {
                    loadMyQueries();
                }

                cardLayout.show(contentCards, item);
            });
        }

        JButton dashboardButton =
                (JButton) sidebar.getComponent(1);

        dashboardButton.setBackground(BLUE);
        activeButton = dashboardButton;

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentCards, BorderLayout.CENTER);

        setContentPane(mainPanel);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    // ==========================================
    // DASHBOARD HOME
    // ==========================================

    private JPanel createDashboardPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);

        JLabel heading = new JLabel(
                "Welcome, " + studentName + "!"
        );

        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(NAVY);

        heading.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 20, 10
                )
        );

        panel.add(heading, BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel(
                new GridBagLayout()
        );

        cardsContainer.setBackground(BACKGROUND);

        JPanel cards = new JPanel(
                new GridLayout(2, 2, 20, 20)
        );

        cards.setBackground(BACKGROUND);
        cards.setPreferredSize(new Dimension(750, 420));

        cards.add(createStatCard("Assigned Quizzes", "—"));
        cards.add(createStatCard("Completed Quizzes", "—"));
        cards.add(createStatCard("Pending Quizzes", "—"));
        cards.add(createStatCard("Average Score", "—"));

        cardsContainer.add(cards);

        panel.add(cardsContainer, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // STATISTICS CARD
    // ==========================================

    private JPanel createStatCard(String title, String value) {

        JPanel card = new JPanel(
                new BorderLayout(5, 15)
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                25, 15, 20, 15
                        )
                )
        );

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        titleLabel.setForeground(
                new Color(45, 55, 75)
        );

        JLabel valueLabel = new JLabel(
                value,
                SwingConstants.CENTER
        );

        valueLabel.setFont(
                new Font("Arial", Font.BOLD, 38)
        );

        valueLabel.setForeground(BLUE);

        card.add(titleLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================
    // AVAILABLE QUIZZES PANEL
    // ==========================================

    private JPanel createAvailableQuizzesPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.add(
                createHeading("Available Quizzes"),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Assignment ID",
                "Quiz Title",
                "Subject",
                "Total Marks",
                "Duration",
                "Start Date",
                "Deadline",
                "Retake",
                "Assignment Status",
                "Quiz Status"
        };

        quizTableModel = new DefaultTableModel(
                columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        quizTable = new JTable(quizTableModel);

        styleTable(quizTable);

        panel.add(
                createTableCenter(quizTable),
                BorderLayout.CENTER
        );

        // BUTTON PANEL

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 10)
        );

        buttonPanel.setBackground(BACKGROUND);

        JButton refreshButton =
                createButton("REFRESH QUIZZES");

        JButton startButton =
                createButton("START QUIZ");

        refreshButton.addActionListener(
                e -> loadAvailableQuizzes()
        );

        startButton.addActionListener(
                e -> startSelectedQuiz()
        );

        buttonPanel.add(refreshButton);
        buttonPanel.add(startButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadAvailableQuizzes();

        return panel;
    }

    // ==========================================
    // LOAD QUIZZES FROM MYSQL
    // ==========================================

    private void loadAvailableQuizzes() {

        quizTableModel.setRowCount(0);

        List<Object[]> quizzes =
                assignmentDAO.getAssignedQuizDetailsForStudent(
                        studentId
                );

        for (Object[] quiz : quizzes) {
            quizTableModel.addRow(quiz);
        }
    }

    // ==========================================
    // START SELECTED QUIZ
    // ==========================================

    private void startSelectedQuiz() {

        int selectedRow = quizTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a quiz from the table first.",
                    "No Quiz Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int modelRow =
                quizTable.convertRowIndexToModel(selectedRow);

        int assignmentId = Integer.parseInt(
                quizTableModel.getValueAt(modelRow, 0).toString()
        );

        String quizTitle =
                quizTableModel.getValueAt(modelRow, 1).toString();

        String assignmentStatus =
                quizTableModel.getValueAt(modelRow, 8).toString();

        String quizStatus =
                quizTableModel.getValueAt(modelRow, 9).toString();

        if (!"ACTIVE".equalsIgnoreCase(quizStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This quiz is currently inactive.",
                    "Quiz Unavailable",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!"PENDING".equalsIgnoreCase(assignmentStatus)
                && !"COMPLETED".equalsIgnoreCase(assignmentStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This quiz cannot be attempted.",
                    "Quiz Unavailable",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to start this quiz?\n\n"
                        + "Quiz: " + quizTitle
                        + "\n\nOnce started, the timer will begin.",
                "Start Quiz",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        setEnabled(false);

        SwingUtilities.invokeLater(() -> {

            try {

                new QuizAttemptFrame(
                        assignmentId,
                        studentId,
                        () -> {

                            loadAvailableQuizzes();
                            loadMyResults();

                            setEnabled(true);
                            toFront();
                            requestFocus();
                        }
                );

                // If quiz screen fails to open, allow dashboard use.
                // Successful quiz screen remains a separate window.
                setEnabled(true);

            } catch (Exception ex) {

                setEnabled(true);

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to open quiz:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        });
    }

    // ==========================================
    // MY RESULTS PANEL
    // ==========================================

    private JPanel createMyResultsPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.add(
                createHeading("My Results"),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Attempt ID",
                "Quiz Title",
                "Marks Obtained",
                "Total Marks",
                "Percentage",
                "Result Status",
                "Started At",
                "Submitted At"
        };

        resultsTableModel = new DefaultTableModel(
                columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(resultsTableModel);

        styleTable(resultsTable);

        panel.add(
                createTableCenter(resultsTable),
                BorderLayout.CENTER
        );

        JButton refreshButton =
                createButton("REFRESH RESULTS");

        refreshButton.addActionListener(
                e -> loadMyResults()
        );

        JButton reviewButton =
                createButton("VIEW REVIEW");

        reviewButton.addActionListener(
                e -> openSelectedQuizReview()
        );

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 10)
        );
        bottomPanel.setBackground(BACKGROUND);
        bottomPanel.add(reviewButton);
        bottomPanel.add(refreshButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // OPEN REVIEW FOR SELECTED SUBMITTED QUIZ
    // ==========================================

    private void openSelectedQuizReview() {

        int selectedRow = resultsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a submitted quiz result first.",
                    "No Result Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = resultsTable.convertRowIndexToModel(selectedRow);

        int attemptId = Integer.parseInt(
                resultsTableModel.getValueAt(modelRow, 0).toString()
        );

        String quizTitle = String.valueOf(
                resultsTableModel.getValueAt(modelRow, 1)
        );

        new QuizReviewFrame(attemptId, studentId, quizTitle)
                .setVisible(true);
    }

    // ==========================================
    // LOAD RESULTS FROM MYSQL
    // ==========================================

    private void loadMyResults() {

        resultsTableModel.setRowCount(0);

        List<Object[]> results =
                attemptDAO.getResultsForStudent(studentId);

        for (Object[] result : results) {

            resultsTableModel.addRow(
                    new Object[]{
                            result[0],
                            result[3],
                            result[4],
                            result[5],
                            result[6],
                            result[7],
                            result[8],
                            result[9]
                    }
            );
        }
    }

    // ==========================================
    // ASK A QUERY PANEL
    // ==========================================

    private JPanel createAskQueryPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.add(
                createHeading("Ask a Query"),
                BorderLayout.NORTH
        );

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                30, 40, 30, 40
                        )
                )
        );

        formPanel.setPreferredSize(
                new Dimension(600, 480)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel subjectLabel = new JLabel("Query Subject");
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(subjectLabel, gbc);

        subjectField = new JTextField();
        subjectField.setFont(new Font("Arial", Font.PLAIN, 15));
        subjectField.setPreferredSize(new Dimension(400, 40));

        gbc.gridy = 1;

        formPanel.add(subjectField, gbc);

        JLabel messageLabel = new JLabel("Your Message");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridy = 2;

        formPanel.add(messageLabel, gbc);

        messageArea = new JTextArea(8, 30);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 15));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JScrollPane messageScroll =
                new JScrollPane(messageArea);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        formPanel.add(messageScroll, gbc);

        JButton submitButton =
                createButton("SUBMIT QUERY");

        submitButton.setPreferredSize(
                new Dimension(180, 45)
        );

        submitButton.addActionListener(
                e -> submitQuery()
        );

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        formPanel.add(submitButton, gbc);

        JPanel centerPanel = new JPanel(
                new GridBagLayout()
        );

        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(formPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // SUBMIT QUERY TO MYSQL
    // ==========================================

    private void submitQuery() {

        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();

        if (subject.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the query subject.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (message.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your message.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        boolean success = queryDAO.addQuery(
                studentId,
                subject,
                message
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Your query has been submitted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            subjectField.setText("");
            messageArea.setText("");

            loadMyQueries();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to submit query. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // MY QUERIES PANEL
    // ==========================================

    private JPanel createMyQueriesPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        panel.setBackground(BACKGROUND);

        panel.add(
                createHeading("My Queries"),
                BorderLayout.NORTH
        );

        String[] columns = {
                "Query ID",
                "Subject",
                "Message",
                "Status",
                "Created At"
        };

        queryTableModel = new DefaultTableModel(
                columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        queryTable = new JTable(queryTableModel);

        styleTable(queryTable);

        queryTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(350);

        panel.add(
                createTableCenter(queryTable),
                BorderLayout.CENTER
        );

        JButton refreshButton =
                createButton("REFRESH QUERIES");

        refreshButton.addActionListener(
                e -> loadMyQueries()
        );

        panel.add(
                createBottomPanel(refreshButton),
                BorderLayout.SOUTH
        );

        loadMyQueries();

        return panel;
    }

    // ==========================================
    // LOAD STUDENT QUERIES FROM MYSQL
    // ==========================================

    private void loadMyQueries() {

        queryTableModel.setRowCount(0);

        List<StudentQuery> queries =
                queryDAO.getQueriesForStudent(studentId);

        for (StudentQuery query : queries) {

            queryTableModel.addRow(
                    new Object[]{
                            query.getQueryId(),
                            query.getSubject(),
                            query.getMessage(),
                            query.getStatus(),
                            query.getCreatedAt()
                    }
            );
        }
    }

    // ==========================================
    // STUDENT PROFILE
    // ==========================================

    private JPanel createProfilePanel() {

        JPanel panel = createSectionPanel("My Profile");

        JPanel profileCard = new JPanel(
                new GridLayout(2, 1, 10, 10)
        );

        profileCard.setBackground(Color.WHITE);

        profileCard.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        profileCard.setPreferredSize(
                new Dimension(500, 180)
        );

        JLabel nameLabel = new JLabel(
                "Student Name: " + studentName
        );

        nameLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JLabel idLabel = new JLabel(
                "Student ID: " + studentId
        );

        idLabel.setFont(
                new Font("Arial", Font.PLAIN, 17)
        );

        profileCard.add(nameLabel);
        profileCard.add(idLabel);

        JPanel center = new JPanel(
                new GridBagLayout()
        );

        center.setBackground(BACKGROUND);
        center.add(profileCard);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // COMMON SECTION PANEL
    // ==========================================

    private JPanel createSectionPanel(String title) {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(BACKGROUND);

        panel.add(
                createHeading(title),
                BorderLayout.NORTH
        );

        return panel;
    }

    // ==========================================
    // COMMON HEADING
    // ==========================================

    private JLabel createHeading(String title) {

        JLabel heading = new JLabel(title);

        heading.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        heading.setForeground(NAVY);

        heading.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 10, 10
                )
        );

        return heading;
    }

    // ==========================================
    // COMMON TABLE STYLE
    // ==========================================

    private void styleTable(JTable table) {

        table.setRowHeight(32);

        table.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
    }

    // ==========================================
    // COMMON TABLE CENTER PANEL
    // ==========================================

    private JPanel createTableCenter(JTable table) {

        JPanel centerPanel = new JPanel(
                new BorderLayout()
        );

        centerPanel.setBackground(BACKGROUND);

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 25, 10, 25
                )
        );

        centerPanel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        return centerPanel;
    }

    // ==========================================
    // COMMON BUTTON
    // ==========================================

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);

        button.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // ==========================================
    // COMMON BOTTOM PANEL
    // ==========================================

    private JPanel createBottomPanel(JButton button) {

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.setBackground(BACKGROUND);
        bottomPanel.add(button);

        return bottomPanel;
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            new LoginFrame();
        }
    }

    // ==========================================
    // MAIN METHOD FOR TESTING
    // ==========================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new StudentDashboard(
                        1,
                        "Test Student"
                )
        );
    }
}
