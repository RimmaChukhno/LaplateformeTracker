package com.laplateforme.tracker.service;

import com.laplateforme.tracker.dao.StudentDAO;
import com.laplateforme.tracker.model.Student;
import com.laplateforme.tracker.utils.FileManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {
    private final StudentDAO studentDAO;
    private final FileManager fileManager;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.fileManager = new FileManager();
    }

    public boolean addStudent(Student student) {
        if (isValidStudent(student)) {
            return studentDAO.addStudent(student);
        }
        return false;
    }

    public boolean updateStudent(Student student) {
        if (isValidStudent(student) && student.getId() > 0) {
            return studentDAO.updateStudent(student);
        }
        return false;
    }

    public boolean deleteStudent(int id) {
        return id > 0 && studentDAO.deleteStudent(id);
    }

    public Student getStudentById(int id) {
        return id > 0 ? studentDAO.getStudentById(id) : null;
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String criteria, Object value) {
        return studentDAO.searchStudents(criteria, value);
    }

    public List<Student> sortStudents(String field, String order) {
        return studentDAO.sortStudents(field, order);
    }

    public List<Student> getStudentsPaginated(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return studentDAO.getStudentsPaginated(offset, pageSize);
    }

    public double calculateClassAverage() {
        return studentDAO.getAverageGrade();
    }

    public Map<String, Integer> getAgeStatistics() {
        List<Student> students = getAllStudents();
        Map<String, Integer> ageStats = new HashMap<>();
        
        for (Student student : students) {
            String ageRange = getAgeRange(student.getAge());
            ageStats.put(ageRange, ageStats.getOrDefault(ageRange, 0) + 1);
        }
        
        return ageStats;
    }

    public boolean exportToCSV(String filename) {
        List<Student> students = getAllStudents();
        return fileManager.exportToCSV(students, filename);
    }

    public boolean importFromCSV(String filename) {
        List<Student> students = fileManager.importFromCSV(filename);
        if (students != null) {
            for (Student student : students) {
                addStudent(student);
            }
            return true;
        }
        return false;
    }

    public boolean exportToJSON(String filename) {
        List<Student> students = getAllStudents();
        return fileManager.exportToJSON(students, filename);
    }

    public boolean importFromJSON(String filename) {
        List<Student> students = fileManager.importFromJSON(filename);
        if (students != null) {
            for (Student student : students) {
                addStudent(student);
            }
            return true;
        }
        return false;
    }

    private boolean isValidStudent(Student student) {
        return student != null &&
               student.getFirstName() != null && !student.getFirstName().trim().isEmpty() &&
               student.getLastName() != null && !student.getLastName().trim().isEmpty() &&
               student.getAge() > 0 && student.getAge() < 150 &&
               student.getGrade() >= 0 && student.getGrade() <= 20;
    }

    private String getAgeRange(int age) {
        if (age < 18) return "Moins de 18 ans";
        else if (age < 25) return "18-24 ans";
        else if (age < 35) return "25-34 ans";
        else return "35 ans et plus";
    }
}