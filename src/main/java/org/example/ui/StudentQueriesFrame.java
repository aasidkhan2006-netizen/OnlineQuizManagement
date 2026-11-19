package org.example.ui;

import org.example.dao.StudentQueryDAO;
import org.example.dao.QueryReplyDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentQueriesFrame extends JFrame {

    private JTable queryTable;
    private DefaultTableModel tableModel;

    private final StudentQueryDAO queryDAO = new StudentQueryDAO();
    private final QueryReplyDAO replyDAO = new QueryReplyDAO();

    public StudentQueriesFrame() {

        setTitle("Student Queries - Online Quiz Management System");
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel("STUDENT QUERIES", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(25, 55, 100));
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        add(heading, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "Query ID",
                "Student Name",
                "Registration Number",
                "Subject",
                "Message",
                "Status",
                "Created At"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        queryTable = new JTable(tableModel);
        queryTable.setRowHeight(30);
        queryTable.setFont(new Font("Arial", Font.PLAIN, 12));

        queryTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(queryTable);

        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 15));

        JButton refreshButton = new JButton("REFRESH");
        JButton viewButton = new JButton("VIEW MESSAGE");
        JButton replyButton = new JButton("REPLY");
        JButton statusButton = new JButton("CHANGE STATUS");
        JButton deleteButton = new JButton("DELETE");
        JButton backButton = new JButton("BACK");

        JButton[] buttons = {
                refreshButton, viewButton, replyButton,
                statusButton, deleteButton, backButton
        };

        for (JButton button : buttons) {
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 11));
            buttonPanel.add(button);
        }

        refreshButton.setBackground(new Color(40, 110, 190));
        viewButton.setBackground(new Color(70, 90, 120));
        replyButton.setBackground(new Color(35, 145, 80));
        statusButton.setBackground(new Color(220, 150, 35));
        deleteButton.setBackground(new Color(190, 55, 55));
        backButton.setBackground(Color.GRAY);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        refreshButton.addActionListener(e -> loadQueries());

        viewButton.addActionListener(e -> viewMessage());

        replyButton.addActionListener(e -> replyToStudent());

        statusButton.addActionListener(e -> changeStatus());

        deleteButton.addActionListener(e -> deleteQuery());

        backButton.addActionListener(e -> dispose());

        // Load queries
        loadQueries();
    }

    // Load all queries
    private void loadQueries() {

        tableModel.setRowCount(0);

        List<Object[]> queries = queryDAO.getAllQueryDetails();

        for (Object[] query : queries) {
            tableModel.addRow(query);
        }
    }

    // Get selected query ID
    private int getSelectedQueryId() {

        int row = queryTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a query first.");
            return -1;
        }

        return Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());
    }

    // View student message and previous replies
    private void viewMessage() {

        int row = queryTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a query first.");
            return;
        }

        int queryId = getSelectedQueryId();

        String student = tableModel.getValueAt(row, 1).toString();
        String subject = tableModel.getValueAt(row, 3).toString();
        String message = tableModel.getValueAt(row, 4).toString();

        StringBuilder details = new StringBuilder();

        details.append("Student: ").append(student)
                .append("\nSubject: ").append(subject)
                .append("\n\nStudent Message:\n")
                .append(message)
                .append("\n\nPrevious Replies:\n");

        List<String> replies = replyDAO.getReplies(queryId);

        if (replies.isEmpty()) {
            details.append("No replies yet.");
        } else {
            for (String reply : replies) {
                details.append("\n").append(reply)
                        .append("\n");
            }
        }

        JTextArea textArea = new JTextArea(
                details.toString(), 15, 45);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(textArea),
                "Query Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Reply to student
    private void replyToStudent() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        JTextArea replyArea = new JTextArea(6, 35);

        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);

        int result = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(replyArea),
                "Reply to Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String message = replyArea.getText().trim();

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Reply cannot be empty.");
            return;
        }

        boolean success = replyDAO.addAdminReply(
                queryId, message);

        if (success) {

            queryDAO.updateQueryStatus(queryId, "IN_PROGRESS");

            JOptionPane.showMessageDialog(this,
                    "Reply sent successfully!");

            loadQueries();

        } else {

            JOptionPane.showMessageDialog(this,
                    "Unable to send reply.");
        }
    }

    // Change query status
    private void changeStatus() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        String[] options = {
                "PENDING",
                "IN_PROGRESS",
                "RESOLVED"
        };

        String status = (String) JOptionPane.showInputDialog(
                this,
                "Select Query Status:",
                "Change Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "PENDING"
        );

        if (status == null) return;

        boolean success = queryDAO.updateQueryStatus(
                queryId, status);

        if (success) {

            JOptionPane.showMessageDialog(this,
                    "Query status updated successfully!");

            loadQueries();

        } else {

            JOptionPane.showMessageDialog(this,
                    "Unable to update status.");
        }
    }

    // Delete query
    private void deleteQuery() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this query?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = queryDAO.deleteQuery(queryId);

            if (success) {

                JOptionPane.showMessageDialog(this,
                        "Query deleted successfully!");

                loadQueries();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Unable to delete query. It may have related replies.");
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new StudentQueriesFrame().setVisible(true));
    }
}