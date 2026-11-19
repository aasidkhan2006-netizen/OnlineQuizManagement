package org.example.ui;

import org.example.dao.QuizAssignmentDAO;
import org.example.dao.QuizDAO;
import org.example.dao.StudentDAO;
import org.example.model.Quiz;
import org.example.model.Student;
import org.example.model.QuizAssignment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

public class AssignQuizPanel extends JPanel {

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

    // THEME COLORS
    private final Color PRIMARY = new Color(45, 105, 190);
    private final Color DARK = new Color(30, 45, 70);
    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color BORDER = new Color(220, 226, 235);

    public AssignQuizPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Assign Quizzes");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(DARK);

        JLabel subtitle = new JLabel(
                "Assign quizzes to students and manage assignments"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        headerPanel.add(heading);
        headerPanel.add(subtitle);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CENTER PANEL =================

        JPanel centerPanel = new JPanel(new BorderLayout(12, 15));
        centerPanel.setBackground(BACKGROUND);

        // ================= ASSIGNMENT FORM =================

        JPanel formPanel = new JPanel(
                new GridLayout(5, 2, 15, 12)
        );

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(18, 20, 18, 20)
                )
        );

        quizBox = new JComboBox<>();
        studentBox = new JComboBox<>();

        startDateField = new JTextField();
        deadlineField = new JTextField();

        startDateField.setToolTipText(
                "Format: yyyy-MM-dd HH:mm:ss"
        );

        deadlineField.setToolTipText(
                "Format: yyyy-MM-dd HH:mm:ss"
        );

        retakeCheckBox = new JCheckBox("Allow Retake");
        retakeCheckBox.setBackground(Color.WHITE);
        retakeCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));

        styleInput(quizBox);
        styleInput(studentBox);
        styleInput(startDateField);
        styleInput(deadlineField);

        addFormRow(formPanel, "Select Quiz:", quizBox);
        addFormRow(formPanel, "Select Student:", studentBox);

        addFormRow(
                formPanel,
                "Start Date (yyyy-MM-dd HH:mm:ss):",
                startDateField
        );

        addFormRow(
                formPanel,
                "Deadline (yyyy-MM-dd HH:mm:ss):",
                deadlineField
        );

        addFormRow(
                formPanel,
                "Retake Permission:",
                retakeCheckBox
        );

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // ================= ASSIGNMENT TABLE =================

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

        assignmentTable.setRowHeight(35);
        assignmentTable.setFont(new Font("Arial", Font.PLAIN, 13));

        assignmentTable.setForeground(DARK);
        assignmentTable.setBackground(Color.WHITE);

        assignmentTable.setSelectionBackground(
                new Color(210, 228, 250)
        );

        assignmentTable.setSelectionForeground(DARK);

        assignmentTable.setShowGrid(true);
        assignmentTable.setGridColor(
                new Color(235, 238, 243)
        );

        assignmentTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        assignmentTable.setFillsViewportHeight(true);

        // TABLE HEADER

        JTableHeader tableHeader =
                assignmentTable.getTableHeader();

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

        assignmentTable.setDefaultRenderer(
                Object.class,
                renderer
        );

        JScrollPane scrollPane =
                new JScrollPane(assignmentTable);

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

        JButton assignButton = createButton(
                "+ Assign Quiz",
                new Color(40, 150, 100),
                135
        );

        JButton refreshButton = createButton(
                "Refresh",
                PRIMARY,
                120
        );

        JButton completeButton = createButton(
                "Mark Completed",
                new Color(210, 145, 40),
                150
        );

        JButton deleteButton = createButton(
                "Delete",
                new Color(205, 65, 65),
                120
        );

        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ================= BUTTON ACTIONS =================

        assignButton.addActionListener(e -> assignQuiz());

        refreshButton.addActionListener(e -> {
            loadDropdowns();
            loadAssignments();
        });

        completeButton.addActionListener(e -> markCompleted());

        deleteButton.addActionListener(e -> deleteAssignment());

        // ================= LOAD DATA =================

        loadDropdowns();
        loadAssignments();
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(
            String text,
            Color color,
            int width
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setPreferredSize(
                new Dimension(width, 38)
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setBackground(color.darker());
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {
                        button.setBackground(color);
                    }
                }
        );

        return button;
    }

    // ================= INPUT DESIGN =================

    private void styleInput(JComponent component) {

        component.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        component.setPreferredSize(
                new Dimension(250, 36)
        );
    }

    // ================= FORM DESIGN =================

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

        panel.add(label);
        panel.add(field);
    }

    // ================= LOAD QUIZZES AND STUDENTS =================

    public void loadDropdowns() {

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
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Quiz) {

                    Quiz quiz = (Quiz) value;

                    setText(
                            quiz.getQuizTitle()
                                    + " - "
                                    + quiz.getSubject()
                    );
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
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Student) {

                    Student student = (Student) value;

                    setText(
                            student.getFullName()
                                    + " ("
                                    + student.getRegistrationNumber()
                                    + ")"
                    );
                }

                return this;
            }
        });
    }

    // ================= ASSIGN QUIZ =================

    private void assignQuiz() {

        Quiz selectedQuiz =
                (Quiz) quizBox.getSelectedItem();

        Student selectedStudent =
                (Student) studentBox.getSelectedItem();

        if (selectedQuiz == null || selectedStudent == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one active quiz and one student first."
            );

            return;
        }

        try {

            Timestamp startDate = Timestamp.valueOf(
                    startDateField.getText().trim()
            );

            Timestamp deadline = Timestamp.valueOf(
                    deadlineField.getText().trim()
            );

            if (!deadline.after(startDate)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Deadline must be after the start date."
                );

                return;
            }

            boolean allowRetake =
                    retakeCheckBox.isSelected();

            boolean success = assignmentDAO.assignQuiz(
                    selectedQuiz.getQuizId(),
                    selectedStudent.getStudentId(),
                    startDate,
                    deadline,
                    allowRetake
            );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quiz assigned successfully!"
                );

                loadAssignments();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to assign quiz. It may already be assigned to this student."
                );
            }

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format. Use yyyy-MM-dd HH:mm:ss."
            );
        }
    }

    // ================= LOAD ASSIGNMENTS =================

    public void loadAssignments() {

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

    // ================= MARK COMPLETED =================

    private void markCompleted() {

        int row = assignmentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an assignment first."
            );

            return;
        }

        int assignmentId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        boolean success = assignmentDAO.updateStatus(
                assignmentId,
                "COMPLETED"
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Assignment marked as completed."
            );

            loadAssignments();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update assignment."
            );
        }
    }

    // ================= DELETE ASSIGNMENT =================

    private void deleteAssignment() {

        int row = assignmentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an assignment first."
            );

            return;
        }

        int assignmentId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this assignment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success =
                    assignmentDAO.deleteAssignment(assignmentId);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Assignment deleted successfully."
                );

                loadAssignments();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete assignment. It may have related quiz attempts."
                );
            }
        }
    }
}