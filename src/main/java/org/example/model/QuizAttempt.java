package org.example.model;

import java.sql.Timestamp;

public class QuizAttempt {

    private int attemptId;
    private int assignmentId;
    private int studentId;
    private Timestamp startedAt;
    private Timestamp submittedAt;
    private double marksObtained;
    private double percentage;
    private String resultStatus;

    public QuizAttempt(int attemptId, int assignmentId, int studentId,
                       Timestamp startedAt, Timestamp submittedAt,
                       double marksObtained, double percentage,
                       String resultStatus) {

        this.attemptId = attemptId;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.marksObtained = marksObtained;
        this.percentage = percentage;
        this.resultStatus = resultStatus;
    }

    public int getAttemptId() { return attemptId; }

    public int getAssignmentId() { return assignmentId; }

    public int getStudentId() { return studentId; }

    public Timestamp getStartedAt() { return startedAt; }

    public Timestamp getSubmittedAt() { return submittedAt; }

    public double getMarksObtained() { return marksObtained; }

    public double getPercentage() { return percentage; }

    public String getResultStatus() { return resultStatus; }
}