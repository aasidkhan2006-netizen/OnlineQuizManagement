package org.example.ui;

import org.example.dao.StudentDAO;
import org.example.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManageStudentsPanel extends JPanel {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private final StudentDAO studentDAO = new StudentDAO();

    // THEME COLORS
    private final Color PRIMARY = new Color(45, 105, 190);
    private final Color DARK = new Color(30, 45, 70);
    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color BORDER = new Color(220, 226, 235);

    public ManageStudentsPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND);

        setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Manage Students");

        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(DARK);

        JLabel subtitle = new JLabel(
                "Add, update, search and manage student records"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(BACKGROUND);

        titlePanel.add(heading);
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CENTER PANEL =================

        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setBackground(BACKGROUND);

        // SEARCH PANEL

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        searchPanel.setBackground(Color.WHITE);

        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );

        JLabel searchLabel = new JLabel("Search Student:");

        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchLabel.setForeground(DARK);

        searchField = new JTextField(25);

        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(260, 36));

        JButton searchButton = createButton("Search", PRIMARY);
        JButton refreshButton = createButton("Refresh", new Color(90, 105, 125));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE =================

        String[] columns = {
                "Student ID",
                "Full Name",
                "Registration Number",
                "Email",
                "Branch",
                "Semester",
                "Phone Number"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);

        studentTable.setRowHeight(35);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 13));

        studentTable.setForeground(DARK);
        studentTable.setBackground(Color.WHITE);

        studentTable.setSelectionBackground(
                new Color(210, 228, 250)
        );

        studentTable.setSelectionForeground(DARK);

        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(235, 238, 243));

        studentTable.setIntercellSpacing(new Dimension(0, 1));

        studentTable.setFillsViewportHeight(true);

        // TABLE HEADER

        JTableHeader tableHeader = studentTable.getTableHeader();

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

        // CENTER TABLE CONTENT

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
                                BorderFactory.createEmptyBorder(0, 8, 0, 8)
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

        studentTable.setDefaultRenderer(Object.class, renderer);

        JScrollPane scrollPane = new JScrollPane(studentTable);

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
                "+ Add Student",
                new Color(40, 150, 100)
        );

        JButton editButton = createButton(
                "Edit Student",
                PRIMARY
        );

        JButton deleteButton = createButton(
                "Delete Student",
                new Color(205, 65, 65)
        );

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ================= BUTTON ACTIONS =================

        addButton.addActionListener(e -> addStudent());

        editButton.addActionListener(e -> editStudent());

        deleteButton.addActionListener(e -> deleteStudent());

        searchButton.addActionListener(e -> searchStudents());

        searchField.addActionListener(e -> searchStudents());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });

        loadStudents();
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

    // ================= LOAD STUDENTS =================

    public void loadStudents() {

        tableModel.setRowCount(0);

        List<Student> students = studentDAO.getAllStudents();

        for (Student student : students) {

            Object[] row = {
                    student.getStudentId(),
                    student.getFullName(),
                    student.getRegistrationNumber(),
                    student.getEmail(),
                    student.getBranch(),
                    student.getSemester(),
                    student.getPhoneNumber()
            };

            tableModel.addRow(row);
        }
    }

    // ================= SEARCH STUDENTS =================

    private void searchStudents() {

        String keyword = searchField.getText().trim();

        tableModel.setRowCount(0);

        List<Student> students = studentDAO.searchStudent(keyword);

        for (Student student : students) {

            Object[] row = {
                    student.getStudentId(),
                    student.getFullName(),
                    student.getRegistrationNumber(),
                    student.getEmail(),
                    student.getBranch(),
                    student.getSemester(),
                    student.getPhoneNumber()
            };

            tableModel.addRow(row);
        }
    }

    // ================= ADD STUDENT =================

    private void addStudent() {

        JTextField nameField = new JTextField();
        JTextField regField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField branchField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField phoneField = new JTextField();

        JPasswordField passwordField = new JPasswordField();

        JPanel panel = createFormPanel(7);

        addFormRow(panel, "Full Name:", nameField);
        addFormRow(panel, "Registration Number:", regField);
        addFormRow(panel, "Email:", emailField);
        addFormRow(panel, "Branch:", branchField);
        addFormRow(panel, "Semester:", semesterField);
        addFormRow(panel, "Phone Number:", phoneField);
        addFormRow(panel, "Password:", passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            try {

                String name = nameField.getText().trim();
                String reg = regField.getText().trim();
                String email = emailField.getText().trim();
                String branch = branchField.getText().trim();
                String semesterText = semesterField.getText().trim();
                String phone = phoneField.getText().trim();

                String password =
                        new String(passwordField.getPassword());

                if (name.isEmpty() || reg.isEmpty()
                        || email.isEmpty() || password.isEmpty()
                        || semesterText.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please fill all required fields."
                    );

                    return;
                }

                int semester = Integer.parseInt(semesterText);

                boolean success = studentDAO.addStudent(
                        name,
                        reg,
                        email,
                        password,
                        branch,
                        semester,
                        phone
                );

                if (success) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Student added successfully!"
                    );

                    loadStudents();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to add student."
                    );
                }

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Semester must be a number."
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }
    }

    // ================= EDIT STUDENT =================

    private void editStudent() {

        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first."
            );

            return;
        }

        int studentId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        JTextField nameField = new JTextField(
                tableModel.getValueAt(selectedRow, 1).toString()
        );

        JTextField regField = new JTextField(
                tableModel.getValueAt(selectedRow, 2).toString()
        );

        JTextField emailField = new JTextField(
                tableModel.getValueAt(selectedRow, 3).toString()
        );

        JTextField branchField = new JTextField(
                tableModel.getValueAt(selectedRow, 4).toString()
        );

        JTextField semesterField = new JTextField(
                tableModel.getValueAt(selectedRow, 5).toString()
        );

        JTextField phoneField = new JTextField(
                tableModel.getValueAt(selectedRow, 6).toString()
        );

        JPanel panel = createFormPanel(6);

        addFormRow(panel, "Full Name:", nameField);
        addFormRow(panel, "Registration Number:", regField);
        addFormRow(panel, "Email:", emailField);
        addFormRow(panel, "Branch:", branchField);
        addFormRow(panel, "Semester:", semesterField);
        addFormRow(panel, "Phone Number:", phoneField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            try {

                String name = nameField.getText().trim();
                String reg = regField.getText().trim();
                String email = emailField.getText().trim();
                String branch = branchField.getText().trim();
                String semesterText = semesterField.getText().trim();
                String phone = phoneField.getText().trim();

                if (name.isEmpty() || reg.isEmpty()
                        || email.isEmpty() || semesterText.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please fill all required fields."
                    );

                    return;
                }

                int semester = Integer.parseInt(semesterText);

                boolean success = studentDAO.updateStudent(
                        studentId,
                        name,
                        reg,
                        email,
                        branch,
                        semester,
                        phone
                );

                if (success) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Student updated successfully!"
                    );

                    loadStudents();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to update student."
                    );
                }

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Semester must be a number."
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }
    }

    // ================= DELETE STUDENT =================

    private void deleteStudent() {

        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first."
            );

            return;
        }

        int studentId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = studentDAO.deleteStudent(studentId);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Student deleted successfully!"
                );

                loadStudents();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete student."
                );
            }
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