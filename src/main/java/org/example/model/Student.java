package org.example.model;

public class Student {

    private int studentId;
    private String fullName;
    private String registrationNumber;
    private String email;
    private String branch;
    private int semester;
    private String phoneNumber;

    public Student(int studentId, String fullName,
                   String registrationNumber, String email,
                   String branch, int semester,
                   String phoneNumber) {

        this.studentId = studentId;
        this.fullName = fullName;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.branch = branch;
        this.semester = semester;
        this.phoneNumber = phoneNumber;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getBranch() {
        return branch;
    }

    public int getSemester() {
        return semester;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}