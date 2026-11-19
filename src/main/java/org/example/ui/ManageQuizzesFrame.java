package org.example.ui;

import org.example.dao.QuizDAO;
import org.example.model.Quiz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageQuizzesFrame extends JPanel {

    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private final QuizDAO quizDAO = new QuizDAO();

    public ManageQuizzesFrame() {

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Heading
        JLabel heading = new JLabel("MANAGE QUIZZES", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(25, 55, 100));
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10)
        );

        add(heading, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "Quiz ID", "Quiz Title", "Subject",
                "Description", "Questions", "Total Marks",
                "Duration (Min)", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        quizTable = new JTable(tableModel);
        quizTable.setRowHeight(28);
        quizTable.setFont(new Font("Arial", Font.PLAIN, 13));

        quizTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(quizTable);

        add(scrollPane, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        searchField = new JTextField(22);

        JButton searchButton = new JButton("SEARCH");
        JButton refreshButton = new JButton("REFRESH");

        searchPanel.add(new JLabel("Search Quiz:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 15)
        );

        JButton addButton = new JButton("ADD QUIZ");
        JButton editButton = new JButton("EDIT QUIZ");
        JButton deleteButton = new JButton("DELETE QUIZ");
        JButton statusButton = new JButton("CHANGE STATUS");

        addButton.setBackground(new Color(35, 145, 80));
        editButton.setBackground(new Color(40, 110, 190));
        deleteButton.setBackground(new Color(190, 55, 55));
        statusButton.setBackground(new Color(220, 150, 35));

        JButton[] buttons = {
                addButton, editButton, deleteButton, statusButton
        };

        for (JButton button : buttons) {
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            buttonPanel.add(button);
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addQuiz());
        editButton.addActionListener(e -> editQuiz());
        deleteButton.addActionListener(e -> deleteQuiz());
        statusButton.addActionListener(e -> changeStatus());

        searchButton.addActionListener(e ->
                loadQuizzes(searchField.getText())
        );

        searchField.addActionListener(e ->
                loadQuizzes(searchField.getText())
        );

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadQuizzes("");
        });

        // Load database records
        loadQuizzes("");
    }

    // LOAD QUIZZES
    public void loadQuizzes(String keyword) {

        tableModel.setRowCount(0);

        List<Quiz> quizzes;

        if (keyword == null || keyword.trim().isEmpty()) {
            quizzes = quizDAO.getAllQuizzes();
        } else {
            quizzes = quizDAO.searchQuiz(keyword.trim());
        }

        for (Quiz quiz : quizzes) {

            tableModel.addRow(new Object[]{
                    quiz.getQuizId(),
                    quiz.getQuizTitle(),
                    quiz.getSubject(),
                    quiz.getDescription(),
                    quiz.getTotalQuestions(),
                    quiz.getTotalMarks(),
                    quiz.getDurationMinutes(),
                    quiz.getStatus()
            });
        }
    }

    // ADD QUIZ
    private void addQuiz() {

        JTextField titleField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField questionsField = new JTextField();
        JTextField marksField = new JTextField();
        JTextField durationField = new JTextField();

        Object[] fields = {
                "Quiz Title:", titleField,
                "Subject:", subjectField,
                "Description:", new JScrollPane(descriptionArea),
                "Total Questions:", questionsField,
                "Total Marks:", marksField,
                "Duration (Minutes):", durationField
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Add New Quiz",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String title = titleField.getText().trim();
            String subject = subjectField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Quiz title and subject are required."
                );
                return;
            }

            int questions = Integer.parseInt(
                    questionsField.getText().trim()
            );

            double marks = Double.parseDouble(
                    marksField.getText().trim()
            );

            int duration = Integer.parseInt(
                    durationField.getText().trim()
            );

            if (questions <= 0 || marks <= 0 || duration <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Questions, marks and duration must be greater than zero."
                );
                return;
            }

            boolean success = quizDAO.addQuiz(
                    title, subject, description,
                    questions, marks, duration
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Quiz added successfully!"
                );
                loadQuizzes("");
            } else {
                JOptionPane.showMessageDialog(
                        this, "Unable to add quiz."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for questions, marks and duration."
            );
        }
    }

    // EDIT QUIZ
    private void editQuiz() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a quiz first."
            );
            return;
        }

        int quizId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        JTextField titleField = new JTextField(
                tableModel.getValueAt(row, 1).toString()
        );

        JTextField subjectField = new JTextField(
                tableModel.getValueAt(row, 2).toString()
        );

        JTextArea descriptionArea = new JTextArea(
                tableModel.getValueAt(row, 3).toString(), 3, 20
        );

        JTextField questionsField = new JTextField(
                tableModel.getValueAt(row, 4).toString()
        );

        JTextField marksField = new JTextField(
                tableModel.getValueAt(row, 5).toString()
        );

        JTextField durationField = new JTextField(
                tableModel.getValueAt(row, 6).toString()
        );

        String currentStatus = tableModel.getValueAt(row, 7).toString();

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"ACTIVE", "INACTIVE"}
        );

        statusBox.setSelectedItem(currentStatus);

        Object[] fields = {
                "Quiz Title:", titleField,
                "Subject:", subjectField,
                "Description:", new JScrollPane(descriptionArea),
                "Total Questions:", questionsField,
                "Total Marks:", marksField,
                "Duration (Minutes):", durationField,
                "Status:", statusBox
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Edit Quiz",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String title = titleField.getText().trim();
            String subject = subjectField.getText().trim();

            if (title.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Quiz title and subject are required."
                );
                return;
            }

            int questions = Integer.parseInt(
                    questionsField.getText().trim()
            );

            double marks = Double.parseDouble(
                    marksField.getText().trim()
            );

            int duration = Integer.parseInt(
                    durationField.getText().trim()
            );

            if (questions <= 0 || marks <= 0 || duration <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter positive values for questions, marks and duration."
                );
                return;
            }

            boolean success = quizDAO.updateQuiz(
                    quizId,
                    title,
                    subject,
                    descriptionArea.getText().trim(),
                    questions,
                    marks,
                    duration,
                    statusBox.getSelectedItem().toString()
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Quiz updated successfully!"
                );
                loadQuizzes("");
            } else {
                JOptionPane.showMessageDialog(
                        this, "Unable to update quiz."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Please enter valid numbers."
            );
        }
    }

    // DELETE QUIZ
    private void deleteQuiz() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a quiz first."
            );
            return;
        }

        int quizId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this quiz?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = quizDAO.deleteQuiz(quizId);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Quiz deleted successfully!"
                );
                loadQuizzes("");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete quiz. It may have related assignments or questions."
                );
            }
        }
    }

    // CHANGE QUIZ STATUS
    private void changeStatus() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a quiz first."
            );
            return;
        }

        int quizId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String currentStatus = tableModel.getValueAt(row, 7).toString();

        String newStatus = currentStatus.equals("ACTIVE")
                ? "INACTIVE" : "ACTIVE";

        boolean success = quizDAO.updateQuiz(
                quizId,
                tableModel.getValueAt(row, 1).toString(),
                tableModel.getValueAt(row, 2).toString(),
                tableModel.getValueAt(row, 3).toString(),
                Integer.parseInt(tableModel.getValueAt(row, 4).toString()),
                Double.parseDouble(tableModel.getValueAt(row, 5).toString()),
                Integer.parseInt(tableModel.getValueAt(row, 6).toString()),
                newStatus
        );

        if (success) {
            JOptionPane.showMessageDialog(
                    this, "Quiz status changed to " + newStatus
            );
            loadQuizzes("");
        } else {
            JOptionPane.showMessageDialog(
                    this, "Unable to change quiz status."
            );
        }
    }
}