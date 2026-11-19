package org.example.model;

public class Quiz {

    private int quizId;
    private String quizTitle;
    private String subject;
    private String description;
    private int totalQuestions;
    private double totalMarks;
    private int durationMinutes;
    private String status;

    public Quiz(int quizId, String quizTitle, String subject,
                String description, int totalQuestions,
                double totalMarks, int durationMinutes, String status) {

        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.subject = subject;
        this.description = description;
        this.totalQuestions = totalQuestions;
        this.totalMarks = totalMarks;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    public int getQuizId() { return quizId; }
    public String getQuizTitle() { return quizTitle; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getTotalMarks() { return totalMarks; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getStatus() { return status; }
}