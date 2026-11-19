package org.example.ui;

import org.example.dao.QuizDAO;
import org.example.model.Quiz;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManageQuizzesPanel extends JPanel {

    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private final QuizDAO quizDAO = new QuizDAO();

    // THEME COLORS
    private final Color PRIMARY = new Color(45, 105, 190);
    private final Color DARK = new Color(30, 45, 70);
    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color BORDER = new Color(220, 226, 235);

    public ManageQuizzesPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Manage Quizzes");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(DARK);

        JLabel subtitle = new JLabel(
                "Create, update, search and manage quizzes"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        titlePanel.add(heading);
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CENTER PANEL =================

        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setBackground(BACKGROUND);

        // SEARCH PANEL

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchPanel.setBackground(Color.WHITE);

        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );

        JLabel searchLabel = new JLabel("Search Quiz:");

        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchLabel.setForeground(DARK);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(260, 36));

        JButton searchButton = createButton("Search", PRIMARY);
        JButton refreshButton = createButton(
                "Refresh",
                new Color(90, 105, 125)
        );

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE =================

        String[] columns = {
                "Quiz ID",
                "Quiz Title",
                "Subject",
                "Description",
                "Questions",
                "Total Marks",
                "Duration (Min)",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        quizTable = new JTable(tableModel);

        quizTable.setRowHeight(35);
        quizTable.setFont(new Font("Arial", Font.PLAIN, 13));

        quizTable.setForeground(DARK);
        quizTable.setBackground(Color.WHITE);

        quizTable.setSelectionBackground(
                new Color(210, 228, 250)
        );

        quizTable.setSelectionForeground(DARK);

        quizTable.setShowGrid(true);
        quizTable.setGridColor(new Color(235, 238, 243));

        quizTable.setIntercellSpacing(new Dimension(0, 1));
        quizTable.setFillsViewportHeight(true);

        // TABLE HEADER

        JTableHeader tableHeader = quizTable.getTableHeader();

        tableHeader.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        tableHeader.setBackground(
                new Color(230, 237, 248)
        );

        tableHeader.setForeground(DARK);

        tableHeader.setPreferredSize(
                new Dimension(0, 42)
        );

        // TABLE CELL DESIGN

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {

                        Component component =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 8, 0, 8
                                )
                        );

                        if (!isSelected) {

                            component.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : new Color(247, 249, 252)
                            );

                            component.setForeground(DARK);
                        }

                        return component;
                    }
                };

        quizTable.setDefaultRenderer(Object.class, renderer);

        JScrollPane scrollPane = new JScrollPane(quizTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ================= BOTTOM BUTTONS =================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 10)
        );

        buttonPanel.setBackground(BACKGROUND);

        JButton addButton = createButton(
                "+ Add Quiz",
                new Color(40, 150, 100)
        );

        JButton editButton = createButton(
                "Edit Quiz",
                PRIMARY
        );

        JButton deleteButton = createButton(
                "Delete Quiz",
                new Color(205, 65, 65)
        );

        JButton statusButton = createButton(
                "Change Status",
                new Color(210, 145, 40)
        );

        addButton.setPreferredSize(new Dimension(125, 38));
        editButton.setPreferredSize(new Dimension(125, 38));
        deleteButton.setPreferredSize(new Dimension(125, 38));
        statusButton.setPreferredSize(new Dimension(145, 38));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(statusButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ================= BUTTON ACTIONS =================

        addButton.addActionListener(e -> addQuiz());
        editButton.addActionListener(e -> editQuiz());
        deleteButton.addActionListener(e -> deleteQuiz());
        statusButton.addActionListener(e -> changeStatus());

        searchButton.addActionListener(
                e -> loadQuizzes(searchField.getText())
        );

        searchField.addActionListener(
                e -> loadQuizzes(searchField.getText())
        );

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadQuizzes("");
        });

        loadQuizzes("");
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));

        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setPreferredSize(
                new Dimension(125, 38)
        );

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // ================= LOAD QUIZZES =================

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

    // ================= ADD QUIZ =================

    private void addQuiz() {

        JTextField titleField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField questionsField = new JTextField();
        JTextField marksField = new JTextField();
        JTextField durationField = new JTextField();

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel panel = createFormPanel(6);

        addFormRow(panel, "Quiz Title:", titleField);
        addFormRow(panel, "Subject:", subjectField);
        addFormRow(panel, "Description:",
                new JScrollPane(descriptionArea));
        addFormRow(panel, "Total Questions:", questionsField);
        addFormRow(panel, "Total Marks:", marksField);
        addFormRow(panel, "Duration (Minutes):", durationField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Quiz",
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
                    title,
                    subject,
                    description,
                    questions,
                    marks,
                    duration
            );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quiz added successfully!"
                );

                loadQuizzes("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to add quiz."
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers."
            );
        }
    }

    // ================= EDIT QUIZ =================

    private void editQuiz() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a quiz first."
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
                tableModel.getValueAt(row, 3).toString(),
                3,
                20
        );

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JTextField questionsField = new JTextField(
                tableModel.getValueAt(row, 4).toString()
        );

        JTextField marksField = new JTextField(
                tableModel.getValueAt(row, 5).toString()
        );

        JTextField durationField = new JTextField(
                tableModel.getValueAt(row, 6).toString()
        );

        String currentStatus =
                tableModel.getValueAt(row, 7).toString();

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"ACTIVE", "INACTIVE"}
        );

        statusBox.setSelectedItem(currentStatus);

        JPanel panel = createFormPanel(7);

        addFormRow(panel, "Quiz Title:", titleField);
        addFormRow(panel, "Subject:", subjectField);
        addFormRow(panel, "Description:",
                new JScrollPane(descriptionArea));
        addFormRow(panel, "Total Questions:", questionsField);
        addFormRow(panel, "Total Marks:", marksField);
        addFormRow(panel, "Duration (Minutes):", durationField);
        addFormRow(panel, "Status:", statusBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Quiz",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String title = titleField.getText().trim();
            String subject = subjectField.getText().trim();

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
                        this,
                        "Quiz updated successfully!"
                );

                loadQuizzes("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update quiz."
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers."
            );
        }
    }

    // ================= DELETE QUIZ =================

    private void deleteQuiz() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a quiz first."
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
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = quizDAO.deleteQuiz(quizId);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quiz deleted successfully!"
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

    // ================= CHANGE QUIZ STATUS =================

    private void changeStatus() {

        int row = quizTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a quiz first."
            );

            return;
        }

        int quizId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String currentStatus =
                tableModel.getValueAt(row, 7).toString();

        String newStatus = currentStatus.equals("ACTIVE")
                ? "INACTIVE" : "ACTIVE";

        boolean success = quizDAO.updateQuiz(
                quizId,
                tableModel.getValueAt(row, 1).toString(),
                tableModel.getValueAt(row, 2).toString(),
                tableModel.getValueAt(row, 3).toString(),
                Integer.parseInt(
                        tableModel.getValueAt(row, 4).toString()
                ),
                Double.parseDouble(
                        tableModel.getValueAt(row, 5).toString()
                ),
                Integer.parseInt(
                        tableModel.getValueAt(row, 6).toString()
                ),
                newStatus
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quiz status changed to " + newStatus
            );

            loadQuizzes("");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to change quiz status."
            );
        }
    }

    // ================= FORM DESIGN =================

    private JPanel createFormPanel(int rows) {

        JPanel panel = new JPanel(
                new GridLayout(rows, 2, 12, 12)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        return panel;
    }

    private void addFormRow(
            JPanel panel,
            String labelText,
            JComponent field
    ) {

        JLabel label = new JLabel(labelText);

        label.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        label.setForeground(DARK);

        field.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        panel.add(label);
        panel.add(field);
    }
}