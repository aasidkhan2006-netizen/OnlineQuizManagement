package org.example.model;

import java.sql.Timestamp;

public class QuizAssignment {

    private int assignmentId;
    private int quizId;
    private int studentId;
    private Timestamp assignedAt;
    private Timestamp startDate;
    private Timestamp deadline;
    private boolean allowRetake;
    private String status;

    public QuizAssignment(int assignmentId, int quizId, int studentId,
                          Timestamp assignedAt, Timestamp startDate,
                          Timestamp deadline, boolean allowRetake,
                          String status) {

        this.assignmentId = assignmentId;
        this.quizId = quizId;
        this.studentId = studentId;
        this.assignedAt = assignedAt;
        this.startDate = startDate;
        this.deadline = deadline;
        this.allowRetake = allowRetake;
        this.status = status;
    }

    public int getAssignmentId() { return assignmentId; }
    public int getQuizId() { return quizId; }
    public int getStudentId() { return studentId; }
    public Timestamp getAssignedAt() { return assignedAt; }
    public Timestamp getStartDate() { return startDate; }
    public Timestamp getDeadline() { return deadline; }
    public boolean isAllowRetake() { return allowRetake; }
    public String getStatus() { return status; }
}