package org.example.dao;

import org.example.database.DBConnection;
import org.example.model.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 1. GET ALL STUDENTS
    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY student_id";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return students;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("registration_number"),
                        rs.getString("email"),
                        rs.getString("branch"),
                        rs.getInt("semester"),
                        rs.getString("phone_number")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // 2. ADD STUDENT
    public boolean addStudent(String name, String regNo,
                              String email, String password,
                              String branch, int semester,
                              String phone) {

        String sql = "INSERT INTO students " +
                "(full_name, registration_number, email, password, branch, semester, phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return false;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, regNo);
            ps.setString(3, email);
            ps.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(5, branch);
            ps.setInt(6, semester);
            ps.setString(7, phone);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. UPDATE STUDENT
    public boolean updateStudent(int id, String name,
                                 String regNo, String email,
                                 String branch, int semester,
                                 String phone) {

        String sql = "UPDATE students SET full_name=?, " +
                "registration_number=?, email=?, branch=?, " +
                "semester=?, phone_number=? WHERE student_id=?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return false;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, regNo);
            ps.setString(3, email);
            ps.setString(4, branch);
            ps.setInt(5, semester);
            ps.setString(6, phone);
            ps.setInt(7, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. DELETE STUDENT
    public boolean deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE student_id=?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return false;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. SEARCH STUDENT
    public List<Student> searchStudent(String keyword) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE " +
                "full_name LIKE ? OR registration_number LIKE ? OR email LIKE ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) return students;

            PreparedStatement ps = con.prepareStatement(sql);

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("registration_number"),
                        rs.getString("email"),
                        rs.getString("branch"),
                        rs.getInt("semester"),
                        rs.getString("phone_number")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
}