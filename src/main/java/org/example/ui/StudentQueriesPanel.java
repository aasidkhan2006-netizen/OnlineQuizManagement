package org.example.ui;

import org.example.dao.StudentQueryDAO;
import org.example.dao.QueryReplyDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class StudentQueriesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private StudentQueryDAO queryDAO;
    private QueryReplyDAO replyDAO;

    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Color BACKGROUND = new Color(244, 247, 252);
    private final Color BLUE = new Color(45, 105, 210);
    private final Color DARK_BLUE = new Color(30, 75, 155);
    private final Color GREEN = new Color(35, 160, 100);
    private final Color ORANGE = new Color(235, 145, 45);
    private final Color RED = new Color(220, 65, 65);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 75);

    public StudentQueriesPanel() {

        queryDAO = new StudentQueryDAO();
        replyDAO = new QueryReplyDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel heading = new JLabel("Student Queries");
        heading.setFont(new Font("Arial", Font.BOLD, 27));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "View student messages, reply to queries and manage their status"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(120, 130, 145));

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );

        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 231, 240)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchLabel.setForeground(TEXT);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 34));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ================= TABLE =================

        String[] columns = {
                "Query ID",
                "Student Name",
                "Registration No.",
                "Subject",
                "Message",
                "Status",
                "Created At"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(38);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(235, 239, 245));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Table header styling
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setBackground(
                new Color(225, 237, 255)
        );

        table.getTableHeader().setForeground(DARK_BLUE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        int[] widths = {
                85, 170, 150, 150, 300, 120, 180
        };

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // Alternating row colours
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {

                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                if (isSelected) {
                    component.setBackground(new Color(220, 235, 255));
                } else if (row % 2 == 0) {
                    component.setBackground(WHITE);
                } else {
                    component.setBackground(new Color(247, 249, 253));
                }

                component.setForeground(TEXT);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                return component;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(225, 231, 240)
        ));
        scrollPane.getViewport().setBackground(WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // ================= BUTTONS =================

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );

        buttons.setOpaque(false);

        JButton refreshButton = createButton("Refresh", BLUE);
        JButton viewButton = createButton("View Message", DARK_BLUE);
        JButton replyButton = createButton("Reply", GREEN);
        JButton statusButton = createButton("Change Status", ORANGE);
        JButton deleteButton = createButton("Delete", RED);

        buttons.add(refreshButton);
        buttons.add(viewButton);
        buttons.add(replyButton);
        buttons.add(statusButton);
        buttons.add(deleteButton);

        add(buttons, BorderLayout.SOUTH);

        // ================= ACTION LISTENERS =================

        refreshButton.addActionListener(e -> loadQueries());
        viewButton.addActionListener(e -> viewMessage());
        replyButton.addActionListener(e -> replyToQuery());
        statusButton.addActionListener(e -> changeStatus());
        deleteButton.addActionListener(e -> deleteQuery());

        // Search functionality
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    private void filterQueries() {

                        String text = searchField.getText().trim();

                        if (text.isEmpty()) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + Pattern.quote(text)
                                    )
                            );
                        }
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterQueries();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterQueries();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterQueries();
                    }
                }
        );

        loadQueries();
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(135, 40));

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

    // ================= LOAD QUERIES =================

    public void loadQueries() {

        model.setRowCount(0);

        try {

            List<Object[]> queries = queryDAO.getAllQueryDetails();

            for (Object[] row : queries) {
                model.addRow(row);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading queries: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= GET SELECTED QUERY ID =================

    private int getSelectedQueryId() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a query first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return -1;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        return Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
        );
    }

    // ================= VIEW MESSAGE AND REPLIES =================

    private void viewMessage() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        int selectedRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(selectedRow);

        String student = model.getValueAt(modelRow, 1).toString();
        String subject = model.getValueAt(modelRow, 3).toString();
        String message = model.getValueAt(modelRow, 4).toString();

        StringBuilder details = new StringBuilder();

        details.append("Student: ").append(student);
        details.append("\nSubject: ").append(subject);
        details.append("\n\nMessage:\n").append(message);
        details.append("\n\nConversation:\n");

        List<String> replies = replyDAO.getReplies(queryId);

        if (replies.isEmpty()) {

            details.append("No replies yet.");

        } else {

            for (String reply : replies) {
                details.append("\n").append(reply).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(details.toString());

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(248, 250, 253));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Query Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= REPLY TO QUERY =================

    private void replyToQuery() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        JTextArea replyArea = new JTextArea(5, 30);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        replyArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(replyArea);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Enter Your Reply",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String reply = replyArea.getText().trim();

        if (reply.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Reply cannot be empty.",
                    "Empty Reply",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            replyDAO.addAdminReply(queryId, reply);

            queryDAO.updateQueryStatus(queryId, "IN_PROGRESS");

            JOptionPane.showMessageDialog(
                    this,
                    "Reply sent successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadQueries();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error sending reply: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= CHANGE STATUS =================

    private void changeStatus() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        int selectedRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(selectedRow);

        String currentStatus = model.getValueAt(modelRow, 5).toString();

        String[] statuses = {
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
                statuses,
                currentStatus
        );

        if (status != null) {

            try {

                queryDAO.updateQueryStatus(queryId, status);

                JOptionPane.showMessageDialog(
                        this,
                        "Status updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadQueries();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error updating status: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ================= DELETE QUERY =================

    private void deleteQuery() {

        int queryId = getSelectedQueryId();

        if (queryId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this query?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                queryDAO.deleteQuery(queryId);

                JOptionPane.showMessageDialog(
                        this,
                        "Query deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadQueries();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error deleting query: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}