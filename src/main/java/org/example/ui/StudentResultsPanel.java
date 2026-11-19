package org.example.ui;

import org.example.dao.QuizAttemptDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;

public class StudentResultsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private QuizAttemptDAO attemptDAO;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Color BACKGROUND = new Color(244, 247, 252);
    private final Color BLUE = new Color(45, 105, 210);
    private final Color DARK_BLUE = new Color(30, 75, 155);
    private final Color GREEN = new Color(35, 160, 100);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 75);

    public StudentResultsPanel() {

        attemptDAO = new QuizAttemptDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel heading = new JLabel("Student Results");
        heading.setFont(new Font("Arial", Font.BOLD, 27));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "View student quiz performance and manage result status"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(120, 130, 145));

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
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
                "Attempt ID",
                "Student Name",
                "Registration No.",
                "Quiz Title",
                "Marks Obtained",
                "Total Marks",
                "Percentage",
                "Result Status",
                "Started At",
                "Submitted At"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(35);
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
                90, 170, 150, 190, 120,
                110, 110, 120, 180, 180
        };

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
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
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
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

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 5)
        );
        buttonPanel.setOpaque(false);

        JButton refreshButton = createButton(
                "Refresh",
                BLUE
        );

        JButton updateButton = createButton(
                "Update Status",
                GREEN
        );

        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ================= ACTION LISTENERS =================

        refreshButton.addActionListener(e -> loadResults());

        updateButton.addActionListener(e -> updateStatus());

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    private void filterResults() {

                        String text = searchField.getText().trim();

                        if (text.isEmpty()) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + java.util.regex.Pattern.quote(text)
                                    )
                            );
                        }
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterResults();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterResults();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        filterResults();
                    }
                }
        );

        loadResults();
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(145, 40));

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

    // ================= LOAD RESULTS =================

    public void loadResults() {

        model.setRowCount(0);

        try {

            for (Object[] row : attemptDAO.getAllResultDetails()) {
                model.addRow(row);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading results: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= UPDATE RESULT STATUS =================

    private void updateStatus() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a result first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Convert selected table row to model row
        int modelRow = table.convertRowIndexToModel(selectedRow);

        int attemptId = Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
        );

        String currentStatus = model.getValueAt(modelRow, 7).toString();

        String[] statuses = {
                "PASS",
                "FAIL",
                "PENDING"
        };

        String status = (String) JOptionPane.showInputDialog(
                this,
                "Select Result Status:",
                "Update Result Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                currentStatus
        );

        if (status != null) {

            boolean updated = attemptDAO.updateResultStatus(
                    attemptId,
                    status
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Result status updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadResults();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update result status.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}