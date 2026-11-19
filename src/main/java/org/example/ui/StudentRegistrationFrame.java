package org.example.ui;

import org.example.database.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentRegistrationFrame extends JFrame {

    private JTextField nameField, regField, emailField, branchField;
    private JTextField semesterField;
    private JPasswordField passwordField, confirmPasswordField;

    public StudentRegistrationFrame() {

        setTitle("Student Registration");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("STUDENT REGISTRATION");
        title.setBounds(90, 25, 350, 35);
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(new Color(30, 60, 120));

        addLabel(panel, "Full Name", 60, 90);
        nameField = addField(panel, 60, 115);

        addLabel(panel, "Registration Number", 60, 165);
        regField = addField(panel, 60, 190);

        addLabel(panel, "Email Address", 60, 240);
        emailField = addField(panel, 60, 265);

        addLabel(panel, "Branch", 60, 315);
        branchField = addField(panel, 60, 340);

        addLabel(panel, "Semester", 60, 390);
        semesterField = addField(panel, 60, 415);

        addLabel(panel, "Password", 60, 465);
        passwordField = new JPasswordField();
        passwordField.setBounds(60, 490, 370, 35);
        panel.add(passwordField);

        addLabel(panel, "Confirm Password", 60, 535);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(60, 560, 370, 35);
        panel.add(confirmPasswordField);

        JButton registerButton = new JButton("REGISTER");
        registerButton.setBounds(60, 615, 170, 35);
        registerButton.setBackground(new Color(35, 85, 160));
        registerButton.setForeground(Color.WHITE);

        JButton backButton = new JButton("BACK TO LOGIN");
        backButton.setBounds(245, 615, 185, 35);

        registerButton.addActionListener(e -> registerStudent());

        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        panel.add(title);
        panel.add(registerButton);
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 300, 25);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label);
    }

    private JTextField addField(JPanel panel, int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 370, 35);
        panel.add(field);
        return field;
    }

    private void registerStudent() {

        String name = nameField.getText().trim();
        String reg = regField.getText().trim();
        String email = emailField.getText().trim();
        String branch = branchField.getText().trim();
        String semesterText = semesterField.getText().trim();

        String password = new String(passwordField.getPassword());
        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || reg.isEmpty() || email.isEmpty()
                || branch.isEmpty() || semesterText.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill all fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid email address.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must contain at least 6 characters.");
            return;
        }

        int semester;

        try {
            semester = Integer.parseInt(semesterText);

            if (semester < 1 || semester > 8) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid semester (1-8).");
            return;
        }

        String hashedPassword = BCrypt.hashpw(
                password, BCrypt.gensalt()
        );

        String sql = "INSERT INTO students "
                + "(full_name, registration_number, email, password, branch, semester) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, reg);
            pst.setString(3, email);
            pst.setString(4, hashedPassword);
            pst.setString(5, branch);
            pst.setInt(6, semester);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Registration Successful!");

            dispose();
            new LoginFrame();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "Registration Failed: " + ex.getMessage());

            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentRegistrationFrame::new);
    }
}