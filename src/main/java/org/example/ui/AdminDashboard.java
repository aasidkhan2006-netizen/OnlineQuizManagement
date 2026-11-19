package org.example.ui;

import org.example.dao.DashboardDAO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JLabel studentValue;
    private JLabel quizValue;
    private JLabel assignmentValue;
    private JLabel queryValue;

    private CardLayout cardLayout;
    private JPanel contentCards;
    private JPanel sidebar;

    private ManageStudentsPanel studentsPanel;
    private ManageQuizzesPanel quizzesPanel;
    private AssignQuizPanel assignQuizPanel;
    private StudentResultsPanel resultsPanel;
    private StudentQueriesPanel queriesPanel;
    private ManageQuestionsPanel questionsPanel;

    private JButton activeButton;

    public AdminDashboard() {

        setTitle("Online Quiz Management System - Admin");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ================= SIDEBAR =================

        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(245, 750));
        sidebar.setBackground(new Color(25, 45, 80));
        sidebar.setLayout(new GridLayout(9, 1, 5, 5));

        JLabel logo = new JLabel("  QUIZ MANAGEMENT");

        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 19));

        sidebar.add(logo);

        // ================= CARD LAYOUT =================

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);

        // DASHBOARD

        JPanel dashboardContent = createDashboardContent();
        contentCards.add(dashboardContent, "Dashboard");

        // STUDENTS

        studentsPanel = new ManageStudentsPanel();
        contentCards.add(studentsPanel, "Manage Students");

        // QUIZZES

        quizzesPanel = new ManageQuizzesPanel();
        contentCards.add(quizzesPanel, "Manage Quizzes");

        // MANAGE QUESTIONS

        questionsPanel = new ManageQuestionsPanel();
        contentCards.add(questionsPanel, "Manage Questions");

        // ASSIGN QUIZZES

        assignQuizPanel = new AssignQuizPanel();
        contentCards.add(assignQuizPanel, "Assign Quizzes");

        // STUDENT RESULTS

        resultsPanel = new StudentResultsPanel();
        contentCards.add(resultsPanel, "Student Results");

        // STUDENT QUERIES

        queriesPanel = new StudentQueriesPanel();
        contentCards.add(queriesPanel, "Student Queries");

        // ================= SIDEBAR BUTTONS =================

        String[] menuItems = {
                "Dashboard",
                "Manage Students",
                "Manage Quizzes",
                "Manage Questions",
                "Assign Quizzes",
                "Student Results",
                "Student Queries",
                "Logout"
        };

        for (String item : menuItems) {

            JButton button = new JButton(item);

            button.setForeground(Color.WHITE);
            button.setBackground(new Color(35, 60, 100));

            button.setFocusPainted(false);
            button.setBorderPainted(false);

            button.setFont(new Font("Arial", Font.BOLD, 14));

            button.setHorizontalAlignment(SwingConstants.LEFT);

            button.setBorder(
                    BorderFactory.createEmptyBorder(
                            12, 20, 12, 10
                    )
            );

            button.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            sidebar.add(button);

            button.addActionListener(e -> {

                // HIGHLIGHT SELECTED BUTTON

                if (activeButton != null) {
                    activeButton.setBackground(
                            new Color(35, 60, 100)
                    );
                }

                button.setBackground(
                        new Color(55, 125, 220)
                );

                activeButton = button;

                // NAVIGATION

                switch (item) {

                    case "Dashboard":

                        loadDashboardData();
                        cardLayout.show(contentCards, "Dashboard");
                        break;

                    case "Manage Students":

                        studentsPanel.loadStudents();
                        cardLayout.show(contentCards, "Manage Students");
                        break;

                    case "Manage Quizzes":

                        quizzesPanel.loadQuizzes("");
                        cardLayout.show(contentCards, "Manage Quizzes");
                        break;

                    case "Manage Questions":

                        questionsPanel.loadQuizzes();
                        cardLayout.show(contentCards, "Manage Questions");
                        break;

                    case "Assign Quizzes":

                        assignQuizPanel.loadDropdowns();
                        assignQuizPanel.loadAssignments();

                        cardLayout.show(contentCards, "Assign Quizzes");
                        break;

                    case "Student Results":

                        resultsPanel.loadResults();
                        cardLayout.show(contentCards, "Student Results");
                        break;

                    case "Student Queries":

                        queriesPanel.loadQueries();
                        cardLayout.show(contentCards, "Student Queries");
                        break;

                    case "Logout":

                        int choice = JOptionPane.showConfirmDialog(
                                this,
                                "Are you sure you want to logout?",
                                "Confirm Logout",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (choice == JOptionPane.YES_OPTION) {

                            dispose();
                            new LoginFrame();
                        }

                        break;
                }
            });
        }

        // ================= ADD COMPONENTS =================

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentCards, BorderLayout.CENTER);

        add(mainPanel);

        // SELECT DASHBOARD BY DEFAULT

        JButton dashboardButton =
                (JButton) sidebar.getComponent(1);

        dashboardButton.setBackground(
                new Color(55, 125, 220)
        );

        activeButton = dashboardButton;

        loadDashboardData();

        setVisible(true);
    }

    // ================= DASHBOARD CONTENT =================

    private JPanel createDashboardContent() {

        JPanel content = new JPanel(new BorderLayout());

        content.setBackground(
                new Color(240, 243, 248)
        );

        JLabel heading = new JLabel("Admin Dashboard");

        heading.setFont(
                new Font("Arial", Font.BOLD, 30)
        );

        heading.setForeground(
                new Color(35, 45, 65)
        );

        heading.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 20, 10
                )
        );

        content.add(heading, BorderLayout.NORTH);

        // CARDS CONTAINER

        JPanel cardContainer = new JPanel(new GridBagLayout());

        cardContainer.setBackground(
                new Color(240, 243, 248)
        );

        JPanel cards = new JPanel(
                new GridLayout(2, 2, 20, 20)
        );

        cards.setBackground(
                new Color(240, 243, 248)
        );

        cards.setPreferredSize(
                new Dimension(750, 420)
        );

        studentValue = new JLabel("0", SwingConstants.CENTER);
        quizValue = new JLabel("0", SwingConstants.CENTER);
        assignmentValue = new JLabel("0", SwingConstants.CENTER);
        queryValue = new JLabel("0", SwingConstants.CENTER);

        addCard(cards, "Total Students", studentValue);
        addCard(cards, "Total Quizzes", quizValue);
        addCard(cards, "Quiz Assignments", assignmentValue);
        addCard(cards, "Student Queries", queryValue);

        cardContainer.add(cards);

        content.add(cardContainer, BorderLayout.CENTER);

        return content;
    }

    // ================= DASHBOARD CARDS =================

    private void addCard(
            JPanel cards,
            String name,
            JLabel value
    ) {

        JPanel card = new JPanel(new BorderLayout(5, 15));

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),

                        BorderFactory.createEmptyBorder(
                                25, 15, 20, 15
                        )
                )
        );

        JLabel label = new JLabel(
                name,
                SwingConstants.CENTER
        );

        label.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        label.setForeground(
                new Color(45, 55, 75)
        );

        value.setFont(
                new Font("Arial", Font.BOLD, 38)
        );

        value.setForeground(
                new Color(55, 125, 220)
        );

        card.add(label, BorderLayout.CENTER);
        card.add(value, BorderLayout.SOUTH);

        cards.add(card);
    }

    // ================= LOAD STATISTICS =================

    private void loadDashboardData() {

        studentValue.setText(
                String.valueOf(DashboardDAO.getTotalStudents())
        );

        quizValue.setText(
                String.valueOf(DashboardDAO.getTotalQuizzes())
        );

        assignmentValue.setText(
                String.valueOf(DashboardDAO.getTotalAssignments())
        );

        queryValue.setText(
                String.valueOf(DashboardDAO.getTotalQueries())
        );
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                AdminDashboard::new
        );
    }
}