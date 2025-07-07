package com.laplateforme.tracker.dao;

import com.laplateforme.tracker.database.DatabaseConnection;
import com.laplateforme.tracker.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StudentDAO {
    private final DatabaseConnection dbConnection;
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    public StudentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";
        try {
            int rowsAffected = dbConnection.executeUpdate(sql, 
                student.getFirstName(), student.getLastName(), student.getAge(), student.getGrade());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de l'étudiant", e);
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ? WHERE id = ?";
        try {
            int rowsAffected = dbConnection.executeUpdate(sql,
                student.getFirstName(), student.getLastName(), student.getAge(), 
                student.getGrade(), student.getId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'étudiant", e);
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try {
            int rowsAffected = dbConnection.executeUpdate(sql, id);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'étudiant", e);
            return false;
        }
    }

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        try (ResultSet rs = dbConnection.executeQuery(sql, id)) {
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'étudiant", e);
        }
        return null;
    }

    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM student ORDER BY id";
        return executeStudentQuery(sql);
    }

    public List<Student> searchStudents(String criteria, Object value) {
        String sql = "SELECT * FROM student WHERE " + criteria + " = ?";
        return executeStudentQuery(sql, value);
    }

    public List<Student> sortStudents(String field, String order) {
        String sql = "SELECT * FROM student ORDER BY " + field + " " + order;
        return executeStudentQuery(sql);
    }

    public List<Student> getStudentsPaginated(int offset, int limit) {
        String sql = "SELECT * FROM student ORDER BY id LIMIT ? OFFSET ?";
        return executeStudentQuery(sql, limit, offset);
    }

    public int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM student";
        try (ResultSet rs = dbConnection.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des étudiants", e);
        }
        return 0;
    }

    public double getAverageGrade() {
        String sql = "SELECT AVG(grade) FROM student";
        try (ResultSet rs = dbConnection.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul de la moyenne", e);
        }
        return 0.0;
    }

    private List<Student> executeStudentQuery(String sql, Object... params) {
        List<Student> students = new ArrayList<>();
        try (ResultSet rs = dbConnection.executeQuery(sql, params)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'exécution de la requête", e);
        }
        return students;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getInt("age"),
            rs.getDouble("grade")
        );
    }
}
