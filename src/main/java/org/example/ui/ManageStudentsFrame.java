package org.example.ui;

import org.example.dao.StudentDAO;
import org.example.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStudentsFrame extends JFrame {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // DAO object
    private final StudentDAO studentDAO = new StudentDAO();

    public ManageStudentsFrame() {

        setTitle("Manage Students");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel("MANAGE STUDENTS");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );

        add(heading, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        searchField = new JTextField(25);

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Student Table
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
        studentTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 12)
        );

        JButton addButton = new JButton("Add Student");
        JButton editButton = new JButton("Edit Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addStudent());
        editButton.addActionListener(e -> editStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        searchButton.addActionListener(e -> searchStudents());
        searchField.addActionListener(e -> searchStudents());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });

        backButton.addActionListener(e -> dispose());

        // Load Student Data
        loadStudents();

        setVisible(true);
    }

    // LOAD ALL STUDENTS
    private void loadStudents() {

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

    // SEARCH STUDENTS
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

    // ADD STUDENT
    private void addStudent() {

        JTextField nameField = new JTextField();
        JTextField regField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField branchField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Registration Number:"));
        panel.add(regField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Branch:"));
        panel.add(branchField);

        panel.add(new JLabel("Semester:"));
        panel.add(semesterField);

        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Student",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {

            try {

                String name = nameField.getText().trim();
                String reg = regField.getText().trim();
                String email = emailField.getText().trim();
                String branch = branchField.getText().trim();
                String semesterText = semesterField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = new String(passwordField.getPassword());

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

    // EDIT STUDENT
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

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Registration Number:"));
        panel.add(regField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Branch:"));
        panel.add(branchField);

        panel.add(new JLabel("Semester:"));
        panel.add(semesterField);

        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Student",
                JOptionPane.OK_CANCEL_OPTION
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

    // DELETE STUDENT
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
                JOptionPane.YES_NO_OPTION
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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(ManageStudentsFrame::new);
    }
}