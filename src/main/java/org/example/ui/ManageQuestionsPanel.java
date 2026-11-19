package org.example.ui;

import org.example.dao.QuestionDAO;
import org.example.model.Question;
import org.example.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class ManageQuestionsPanel extends JPanel {

    private JComboBox<QuizItem> quizDropdown;
    private JTable questionTable;
    private DefaultTableModel tableModel;

    private final QuestionDAO questionDAO = new QuestionDAO();

    public ManageQuestionsPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 243, 248));

        JLabel heading = new JLabel("Manage Questions");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 10, 25)
        );

        add(heading, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(new Color(240, 243, 248));
        main.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        // TOP PANEL

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel quizLabel = new JLabel("Select Quiz:");
        quizLabel.setFont(new Font("Arial", Font.BOLD, 14));

        quizDropdown = new JComboBox<>();

        quizDropdown.setPreferredSize(new Dimension(280, 35));

        JButton addButton = new JButton("Add Question");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(refreshButton);

        topPanel.add(quizLabel);
        topPanel.add(quizDropdown);
        topPanel.add(addButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);

        // TABLE

        tableModel = new DefaultTableModel(
                new String[]{"Question ID", "Question", "Marks"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        questionTable = new JTable(tableModel);
        questionTable.setRowHeight(35);
        questionTable.setFont(new Font("Arial", Font.PLAIN, 13));
        questionTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        JScrollPane scrollPane = new JScrollPane(questionTable);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        main.add(centerPanel, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        // ACTIONS

        quizDropdown.addActionListener(e -> loadQuestions());

        addButton.addActionListener(e -> addQuestion());

        deleteButton.addActionListener(e -> deleteQuestion());

        refreshButton.addActionListener(e -> {
            loadQuizzes();
            loadQuestions();
        });

        loadQuizzes();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(55, 125, 220));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // QUIZ DROPDOWN

    public void loadQuizzes() {

        quizDropdown.removeAllItems();

        String sql = "SELECT quiz_id, quiz_title FROM quizzes ORDER BY quiz_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                quizDropdown.addItem(
                        new QuizItem(
                                rs.getInt("quiz_id"),
                                rs.getString("quiz_title")
                        )
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load quizzes: " + e.getMessage()
            );
        }

        loadQuestions();
    }

    // LOAD QUESTIONS

    public void loadQuestions() {

        tableModel.setRowCount(0);

        QuizItem selectedQuiz =
                (QuizItem) quizDropdown.getSelectedItem();

        if (selectedQuiz == null) {
            return;
        }

        List<Question> questions =
                questionDAO.getQuestionsByQuiz(selectedQuiz.id);

        for (Question q : questions) {
            tableModel.addRow(new Object[]{
                    q.getQuestionId(),
                    q.getQuestionText(),
                    q.getMarks()
            });
        }
    }

    // ADD QUESTION

    private void addQuestion() {

        QuizItem selectedQuiz =
                (QuizItem) quizDropdown.getSelectedItem();

        if (selectedQuiz == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please create a quiz first."
            );
            return;
        }

        JTextField questionField = new JTextField(25);
        JTextField optionA = new JTextField(25);
        JTextField optionB = new JTextField(25);
        JTextField optionC = new JTextField(25);
        JTextField optionD = new JTextField(25);

        JTextField marksField = new JTextField("5", 5);

        JComboBox<String> correctAnswer =
                new JComboBox<>(new String[]{"A", "B", "C", "D"});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Question:"));
        form.add(questionField);

        form.add(new JLabel("Option A:"));
        form.add(optionA);

        form.add(new JLabel("Option B:"));
        form.add(optionB);

        form.add(new JLabel("Option C:"));
        form.add(optionC);

        form.add(new JLabel("Option D:"));
        form.add(optionD);

        form.add(new JLabel("Marks:"));
        form.add(marksField);

        form.add(new JLabel("Correct Answer:"));
        form.add(correctAnswer);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add New Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String question = questionField.getText().trim();

        String a = optionA.getText().trim();
        String b = optionB.getText().trim();
        String c = optionC.getText().trim();
        String d = optionD.getText().trim();

        int marks;

        try {
            marks = Integer.parseInt(marksField.getText().trim());

            if (marks <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid positive marks."
            );
            return;
        }

        if (question.isEmpty() || a.isEmpty() ||
                b.isEmpty() || c.isEmpty() || d.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );
            return;
        }

        boolean success = questionDAO.addQuestion(
                selectedQuiz.id,
                question,
                marks,
                a,
                b,
                c,
                d,
                (String) correctAnswer.getSelectedItem()
        );

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Question added successfully!"
            );

            loadQuestions();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add question. Check the console for details."
            );
        }
    }

    // DELETE QUESTION

    private void deleteQuestion() {

        int selectedRow = questionTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a question first."
            );
            return;
        }

        int questionId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this question?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            if (questionDAO.deleteQuestion(questionId)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Question deleted successfully!"
                );

                loadQuestions();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete question."
                );
            }
        }
    }

    // QUIZ ITEM

    private static class QuizItem {

        int id;
        String title;

        QuizItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}