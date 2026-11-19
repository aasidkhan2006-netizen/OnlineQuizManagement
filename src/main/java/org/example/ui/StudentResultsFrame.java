package org.example.ui;

import org.example.dao.QuizAttemptDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentResultsFrame extends JFrame {

    private JTable resultsTable;
    private DefaultTableModel tableModel;

    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    public StudentResultsFrame() {

        setTitle("Student Results - Online Quiz Management System");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel("STUDENT RESULTS", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(25, 55, 100));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));

        add(heading, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
                "Attempt ID",
                "Student Name",
                "Registration Number",
                "Quiz Title",
                "Marks Obtained",
                "Total Marks",
                "Percentage",
                "Result Status",
                "Started At",
                "Submitted At"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(28);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        resultsTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(resultsTable);

        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 15));

        JButton refreshButton = new JButton("REFRESH");
        JButton updateButton = new JButton("UPDATE STATUS");
        JButton backButton = new JButton("BACK");

        refreshButton.setBackground(new Color(40, 110, 190));
        updateButton.setBackground(new Color(220, 150, 35));
        backButton.setBackground(Color.GRAY);

        JButton[] buttons = {
                refreshButton, updateButton, backButton
        };

        for (JButton button : buttons) {
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        refreshButton.addActionListener(e -> loadResults());

        updateButton.addActionListener(e -> updateResultStatus());

        backButton.addActionListener(e -> dispose());

        // Load results
        loadResults();
    }

    // Load student results from database
    private void loadResults() {

        tableModel.setRowCount(0);

        List<Object[]> results = attemptDAO.getAllResultDetails();

        for (Object[] result : results) {

            tableModel.addRow(result);
        }
    }

    // Update result status
    private void updateResultStatus() {

        int row = resultsTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a result first.");
            return;
        }

        int attemptId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        String[] options = {"PASS", "FAIL", "PENDING"};

        String currentStatus =
                tableModel.getValueAt(row, 7).toString();

        String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Select Result Status:",
                "Update Result Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                currentStatus
        );

        if (newStatus == null) {
            return;
        }

        boolean success = attemptDAO.updateResultStatus(
                attemptId, newStatus);

        if (success) {

            JOptionPane.showMessageDialog(this,
                    "Result status updated successfully!");

            loadResults();

        } else {

            JOptionPane.showMessageDialog(this,
                    "Unable to update result status.");
        }
    }

    // Main method for testing
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new StudentResultsFrame().setVisible(true));
    }
}