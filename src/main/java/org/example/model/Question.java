package org.example.model;

public class Question {

    private int questionId;
    private int quizId;
    private String questionText;
    private int marks;

    public Question(int questionId, int quizId,
                    String questionText, int marks) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.marks = marks;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getMarks() {
        return marks;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}