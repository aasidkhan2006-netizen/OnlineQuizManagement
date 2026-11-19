package org.example.model;

import java.sql.Timestamp;

public class StudentQuery {

    private int queryId;
    private int studentId;
    private String subject;
    private String message;
    private String status;
    private Timestamp createdAt;

    public StudentQuery(int queryId, int studentId,
                        String subject, String message,
                        String status, Timestamp createdAt) {

        this.queryId = queryId;
        this.studentId = studentId;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getQueryId() {
        return queryId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}