package org.example.ui;

import org.example.database.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private JToggleButton studentButton;
    private JToggleButton adminButton;

    private JCheckBox showPassword;
    private JButton registerButton;

    private final Color BLUE = new Color(43, 105, 210);
    private final Color DARK_BLUE = new Color(25, 55, 105);
    private final Color BACKGROUND = new Color(235, 241, 250);
    private final Color TEXT = new Color(40, 50, 70);
    private final Color MUTED = new Color(120, 130, 145);

    public LoginFrame() {

        setTitle("Online Quiz Assessment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 650));
        setResizable(true);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BACKGROUND);

        background.add(createLoginCard());

        setContentPane(background);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);

        SwingUtilities.invokeLater(() ->
                setExtendedState(JFrame.MAXIMIZED_BOTH)
        );
    }

    // ==========================================
    // LOGIN CARD
    // ==========================================

    private JPanel createLoginCard() {

        JPanel card = new JPanel(new GridBagLayout());

        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(215, 225, 240), 1
                ),
                new EmptyBorder(45, 65, 40, 65)
        ));

        card.setPreferredSize(new Dimension(780, 820));

        JPanel content = new JPanel();

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        // PROJECT TITLE

        JLabel title = new JLabel("ONLINE QUIZ ASSESSMENT");

        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(DARK_BLUE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Examination & Assessment Management System"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // LOGIN HEADING

        JLabel heading = new JLabel("Welcome Back");

        heading.setFont(new Font("Arial", Font.BOLD, 29));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
                "Sign in to access your account"
        );

        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setForeground(MUTED);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ROLE SELECTION

        JLabel roleLabel = createLabel("SELECT YOUR ROLE");

        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        studentButton = new JToggleButton("STUDENT");
        adminButton = new JToggleButton("ADMIN");

        ButtonGroup roleGroup = new ButtonGroup();

        roleGroup.add(studentButton);
        roleGroup.add(adminButton);

        studentButton.setSelected(true);

        styleRoleButton(studentButton);
        styleRoleButton(adminButton);

        JPanel rolePanel = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        rolePanel.setBackground(Color.WHITE);
        rolePanel.setMaximumSize(new Dimension(520, 52));
        rolePanel.setPreferredSize(new Dimension(520, 52));
        rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rolePanel.add(studentButton);
        rolePanel.add(adminButton);

        // USERNAME

        JLabel usernameLabel = createLabel(
                "USERNAME / REGISTRATION NUMBER"
        );

        usernameField = new JTextField();

        styleTextField(usernameField);

        // PASSWORD

        JLabel passwordLabel = createLabel("PASSWORD");

        passwordField = new JPasswordField();

        styleTextField(passwordField);

        // SHOW PASSWORD

        showPassword = new JCheckBox("Show Password");

        showPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showPassword.setForeground(MUTED);
        showPassword.setBackground(Color.WHITE);
        showPassword.setFocusPainted(false);
        showPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPassword.addActionListener(e -> {

            passwordField.setEchoChar(
                    showPassword.isSelected() ? (char) 0 : '\u2022'
            );

        });

        // SIGN IN AND CLEAR

        JButton loginButton = createButton(
                "SIGN IN",
                BLUE,
                Color.WHITE
        );

        JButton clearButton = createButton(
                "CLEAR",
                new Color(235, 241, 250),
                DARK_BLUE
        );

        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(520, 52));
        buttonPanel.setPreferredSize(new Dimension(520, 52));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        // REGISTRATION BUTTON
        // Its size and position never change.

        registerButton = new JButton(
                "New Student? Create an Account"
        );

        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setForeground(BLUE);
        registerButton.setBackground(Color.WHITE);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.setPreferredSize(new Dimension(400, 40));
        registerButton.setMinimumSize(new Dimension(400, 40));
        registerButton.setMaximumSize(new Dimension(400, 40));

        registerButton.addActionListener(e -> {

            dispose();
            new StudentRegistrationFrame();

        });

        // FIXED REGISTRATION AREA

        JPanel registrationPanel = new JPanel(
                new GridBagLayout()
        );

        registrationPanel.setBackground(Color.WHITE);

        registrationPanel.setPreferredSize(
                new Dimension(520, 45)
        );

        registrationPanel.setMinimumSize(
                new Dimension(520, 45)
        );

        registrationPanel.setMaximumSize(
                new Dimension(520, 45)
        );

        registrationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registrationPanel.add(registerButton);

        // ROLE ACTIONS

        studentButton.addActionListener(
                e -> updateRegistrationVisibility()
        );

        adminButton.addActionListener(
                e -> updateRegistrationVisibility()
        );

        // BUTTON ACTIONS

        loginButton.addActionListener(e -> login());

        clearButton.addActionListener(e -> {

            usernameField.setText("");
            passwordField.setText("");

            studentButton.setSelected(true);

            showPassword.setSelected(false);
            passwordField.setEchoChar('\u2022');

            updateRegistrationVisibility();

        });

        usernameField.addActionListener(
                e -> passwordField.requestFocus()
        );

        passwordField.addActionListener(e -> login());

        // ADD COMPONENTS

        content.add(title);
        content.add(Box.createVerticalStrut(10));

        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));

        content.add(heading);
        content.add(Box.createVerticalStrut(8));

        content.add(description);
        content.add(Box.createVerticalStrut(35));

        content.add(roleLabel);
        content.add(Box.createVerticalStrut(12));

        content.add(rolePanel);
        content.add(Box.createVerticalStrut(30));

        content.add(usernameLabel);
        content.add(Box.createVerticalStrut(10));

        content.add(usernameField);
        content.add(Box.createVerticalStrut(25));

        content.add(passwordLabel);
        content.add(Box.createVerticalStrut(10));

        content.add(passwordField);
        content.add(Box.createVerticalStrut(12));

        content.add(showPassword);
        content.add(Box.createVerticalStrut(30));

        content.add(buttonPanel);
        content.add(Box.createVerticalStrut(20));

        content.add(registrationPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        card.add(content, gbc);

        updateRegistrationVisibility();

        return card;
    }

    // ==========================================
    // LABEL DESIGN
    // ==========================================

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }

    // ==========================================
    // TEXT FIELD DESIGN
    // ==========================================

    private void styleTextField(JTextField field) {

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(TEXT);
        field.setBackground(new Color(249, 251, 254));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 222, 240)
                ),
                new EmptyBorder(12, 15, 12, 15)
        ));

        field.setPreferredSize(new Dimension(520, 52));
        field.setMinimumSize(new Dimension(520, 52));
        field.setMaximumSize(new Dimension(520, 52));

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // ==========================================
    // ROLE BUTTON DESIGN
    // ==========================================

    private void styleRoleButton(JToggleButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 15));

        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBackground(new Color(240, 244, 250));
        button.setForeground(TEXT);

        button.setBorder(BorderFactory.createLineBorder(
                new Color(215, 225, 240)
        ));

        button.addChangeListener(e -> {

            if (button.isSelected()) {

                button.setBackground(BLUE);
                button.setForeground(Color.WHITE);

                button.setBorder(
                        BorderFactory.createLineBorder(BLUE)
                );

            } else {

                button.setBackground(new Color(240, 244, 250));
                button.setForeground(TEXT);

                button.setBorder(
                        BorderFactory.createLineBorder(
                                new Color(215, 225, 240)
                        )
                );
            }
        });
    }

    // ==========================================
    // BUTTON DESIGN
    // ==========================================

    private JButton createButton(
            String text,
            Color background,
            Color foreground
    ) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(background);
        button.setForeground(foreground);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ==========================================
    // REGISTRATION VISIBILITY
    // ==========================================

    private void updateRegistrationVisibility() {

        if (studentButton.isSelected()) {

            registerButton.setText(
                    "New Student? Create an Account"
            );

            registerButton.setEnabled(true);

        } else {

            // Keep the button present with the same dimensions.
            registerButton.setText(" ");
            registerButton.setEnabled(false);
        }

        registerButton.setVisible(true);

    }

    // ==========================================
    // LOGIN FUNCTION
    // ==========================================

    private void login() {

        String username = usernameField.getText().trim();

        String password = new String(
                passwordField.getPassword()
        );

        String role = studentButton.isSelected()
                ? "Student"
                : "Admin";

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql;

        if (role.equals("Admin")) {

            sql = "SELECT password FROM admins WHERE username = ?";

        } else {

            sql = "SELECT student_id, full_name, password " +
                    "FROM students WHERE registration_number = ?";

        }

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String storedPassword = rs.getString("password");

                    if (BCrypt.checkpw(password, storedPassword)) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Login Successful!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        if (role.equals("Admin")) {

                            dispose();
                            new AdminDashboard();

                        } else {

                            int studentId = rs.getInt("student_id");

                            String studentName = rs.getString("full_name");

                            dispose();

                            new StudentDashboard(
                                    studentId,
                                    studentName
                            );
                        }

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Incorrect Password!",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "User not found!",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // ==========================================
    // MAIN METHOD
    // ==========================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}