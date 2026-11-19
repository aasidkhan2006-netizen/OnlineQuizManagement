package org.example.ui;

import org.example.dao.QuizAssignmentDAO;
import org.example.dao.QuizDAO;
import org.example.dao.StudentDAO;
import org.example.model.Quiz;
import org.example.model.Student;
import org.example.model.QuizAssignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

public class AssignQuizFrame extends JFrame {

    private JComboBox<Quiz> quizBox;
    private JComboBox<Student> studentBox;
    private JTextField startDateField;
    private JTextField deadlineField;
    private JCheckBox retakeCheckBox;

    private JTable assignmentTable;
    private DefaultTableModel tableModel;

    private final QuizDAO quizDAO = new QuizDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final QuizAssignmentDAO assignmentDAO =
            new QuizAssignmentDAO();

    public AssignQuizFrame() {

        setTitle("Assign Quizzes - Online Quiz Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel("ASSIGN QUIZZES", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(25, 55, 100));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        add(heading, BorderLayout.NORTH);

        // Assignment form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                "New Quiz Assignment"));

        quizBox = new JComboBox<>();
        studentBox = new JComboBox<>();

        startDateField = new JTextField();
        deadlineField = new JTextField();

        retakeCheckBox = new JCheckBox("Allow Retake");

        formPanel.add(new JLabel("Select Quiz:"));
        formPanel.add(quizBox);

        formPanel.add(new JLabel("Select Student:"));
        formPanel.add(studentBox);

        formPanel.add(new JLabel("Start Date (yyyy-MM-dd HH:mm:ss):"));
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Deadline (yyyy-MM-dd HH:mm:ss):"));
        formPanel.add(deadlineField);

        formPanel.add(new JLabel("Retake Permission:"));
        formPanel.add(retakeCheckBox);

        // Assignment table
        String[] columns = {
                "Assignment ID",
                "Quiz ID",
                "Student ID",
                "Assigned At",
                "Start Date",
                "Deadline",
                "Retake",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        assignmentTable = new JTable(tableModel);
        assignmentTable.setRowHeight(26);

        JScrollPane scrollPane = new JScrollPane(assignmentTable);

        // Buttons
        JButton assignButton = new JButton("ASSIGN QUIZ");
        JButton refreshButton = new JButton("REFRESH");
        JButton completeButton = new JButton("MARK COMPLETED");
        JButton deleteButton = new JButton("DELETE");
        JButton backButton = new JButton("BACK");

        JButton[] buttons = {
                assignButton, refreshButton,
                completeButton, deleteButton, backButton
        };

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 12));

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setForeground(Color.WHITE);
            buttonPanel.add(button);
        }

        assignButton.setBackground(new Color(35, 145, 80));
        refreshButton.setBackground(new Color(40, 110, 190));
        completeButton.setBackground(new Color(220, 150, 35));
        deleteButton.setBackground(new Color(190, 55, 55));
        backButton.setBackground(Color.GRAY);

        // Main center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        assignButton.addActionListener(e -> assignQuiz());

        refreshButton.addActionListener(e -> {
            loadDropdowns();
            loadAssignments();
        });

        completeButton.addActionListener(e -> markCompleted());

        deleteButton.addActionListener(e -> deleteAssignment());

        backButton.addActionListener(e -> dispose());

        // Load data
        loadDropdowns();
        loadAssignments();
    }

    // Load quizzes and students
    private void loadDropdowns() {

        quizBox.removeAllItems();
        studentBox.removeAllItems();

        List<Quiz> quizzes = quizDAO.getAllQuizzes();

        for (Quiz quiz : quizzes) {
            if ("ACTIVE".equals(quiz.getStatus())) {
                quizBox.addItem(quiz);
            }
        }

        quizBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Quiz) {
                    Quiz quiz = (Quiz) value;
                    setText(quiz.getQuizTitle() + " - " + quiz.getSubject());
                }

                return this;
            }
        });

        List<Student> students = studentDAO.getAllStudents();

        for (Student student : students) {
            studentBox.addItem(student);
        }

        studentBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Student) {
                    Student student = (Student) value;
                    setText(student.getFullName()
                            + " (" + student.getRegistrationNumber() + ")");
                }

                return this;
            }
        });
    }

    // Assign quiz
    private void assignQuiz() {

        Quiz selectedQuiz = (Quiz) quizBox.getSelectedItem();
        Student selectedStudent = (Student) studentBox.getSelectedItem();

        if (selectedQuiz == null || selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one active quiz and one student first.");
            return;
        }

        try {

            Timestamp startDate = Timestamp.valueOf(
                    startDateField.getText().trim());

            Timestamp deadline = Timestamp.valueOf(
                    deadlineField.getText().trim());

            if (!deadline.after(startDate)) {
                JOptionPane.showMessageDialog(this,
                        "Deadline must be after the start date.");
                return;
            }

            boolean allowRetake = retakeCheckBox.isSelected();

            boolean success = assignmentDAO.assignQuiz(
                    selectedQuiz.getQuizId(),
                    selectedStudent.getStudentId(),
                    startDate,
                    deadline,
                    allowRetake
            );

            if (success) {

                JOptionPane.showMessageDialog(this,
                        "Quiz assigned successfully!");

                loadAssignments();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Unable to assign quiz. It may already be assigned to this student.");
            }

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use yyyy-MM-dd HH:mm:ss.");

        }
    }

    // Load assignments
    private void loadAssignments() {

        tableModel.setRowCount(0);

        List<QuizAssignment> assignments =
                assignmentDAO.getAllAssignments();

        for (QuizAssignment assignment : assignments) {

            tableModel.addRow(new Object[]{
                    assignment.getAssignmentId(),
                    assignment.getQuizId(),
                    assignment.getStudentId(),
                    assignment.getAssignedAt(),
                    assignment.getStartDate(),
                    assignment.getDeadline(),
                    assignment.isAllowRetake() ? "YES" : "NO",
                    assignment.getStatus()
            });
        }
    }

    // Mark assignment completed
    private void markCompleted() {

        int row = assignmentTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an assignment first.");
            return;
        }

        int assignmentId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        boolean success = assignmentDAO.updateStatus(
                assignmentId, "COMPLETED");

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Assignment marked as completed.");
            loadAssignments();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Unable to update assignment.");
        }
    }

    // Delete assignment
    private void deleteAssignment() {

        int row = assignmentTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an assignment first.");
            return;
        }

        int assignmentId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this assignment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success =
                    assignmentDAO.deleteAssignment(assignmentId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Assignment deleted successfully.");
                loadAssignments();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to delete assignment. It may have related quiz attempts.");
            }
        }
    }

    // Main method for testing
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new AssignQuizFrame().setVisible(true));
    }
}