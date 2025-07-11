package com.laplateforme.tracker.gui;

import com.laplateforme.tracker.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ApiService {
  private static final String BASE_URL = "http://localhost:8080";
  private static final Logger LOGGER = Logger.getLogger(ApiService.class.getName());

  // Authentication
  public boolean login(String username, String password) {
    try {
      String postData = String.format("username=%s&password=%s",
          URLEncoder.encode(username, StandardCharsets.UTF_8),
          URLEncoder.encode(password, StandardCharsets.UTF_8));

      String response = makeRequest("POST", "/login", postData);
      return response != null && response.contains("successful");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Login failed", e);
      return false;
    }
  }

  public boolean register(String username, String password) {
    try {
      String postData = String.format("username=%s&password=%s",
          URLEncoder.encode(username, StandardCharsets.UTF_8),
          URLEncoder.encode(password, StandardCharsets.UTF_8));

      String response = makeRequest("POST", "/register", postData);

      if (response != null) {
        if (response.contains("Registration successful")) {
          return true;
        } else if (response.contains("User already exists")) {
          LOGGER.warning("Registration failed: User already exists");
          return false;
        } else if (response.contains("Username and password are required")) {
          LOGGER.warning("Registration failed: Missing required fields");
          return false;
        } else if (response.contains("Registration failed")) {
          LOGGER.warning("Registration failed: Server error");
          return false;
        }
      }

      LOGGER.warning("Registration failed: Unknown response: " + response);
      return false;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Registration failed", e);
      return false;
    }
  }

  // Student CRUD operations
  public ObservableList<Student> getAllStudents() {
    try {
      String response = makeRequest("GET", "/students", null);
      return parseStudentsFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to get all students", e);
      return FXCollections.observableArrayList();
    }
  }

  public Student getStudentById(int id) {
    try {
      String response = makeRequest("GET", "/students/" + id, null);
      return parseStudentFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to get student by ID: " + id, e);
      return null;
    }
  }

  public boolean addStudent(Student student) {
    try {
      String postData = String.format(Locale.US, "firstName=%s&lastName=%s&age=%d&grade=%.2f",
          URLEncoder.encode(student.getFirstName(), StandardCharsets.UTF_8),
          URLEncoder.encode(student.getLastName(), StandardCharsets.UTF_8),
          student.getAge(),
          student.getGrade());

      LOGGER.info("Sending add student request with data: " + postData);
      String response = makeRequest("POST", "/students", postData);
      LOGGER.info("Add student response: " + response);

      if (response != null) {
        if (response.contains("Student added successfully")) {
          LOGGER.info("Student added successfully");
          return true;
        } else if (response.contains("Missing required fields")) {
          LOGGER.warning("Add student failed: Missing required fields");
          return false;
        } else if (response.contains("Invalid age or grade value")) {
          LOGGER.warning("Add student failed: Invalid age or grade value");
          return false;
        } else if (response.contains("Failed to add student")) {
          LOGGER.warning("Add student failed: Server error");
          return false;
        }
      }

      LOGGER.warning("Add student failed: Unknown response: " + response);
      return false;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to add student", e);
      return false;
    }
  }

  public boolean updateStudent(Student student) {
    try {
      String postData = String.format("firstName=%s&lastName=%s&age=%d&grade=%.2f",
          URLEncoder.encode(student.getFirstName(), StandardCharsets.UTF_8),
          URLEncoder.encode(student.getLastName(), StandardCharsets.UTF_8),
          student.getAge(),
          student.getGrade());

      LOGGER.info("Sending update student request with data: " + postData + " for ID: " + student.getId());
      String response = makeRequest("PUT", "/students/" + student.getId(), postData);
      LOGGER.info("Update student response: " + response);

      if (response != null) {
        if (response.contains("Student updated successfully")) {
          LOGGER.info("Student updated successfully");
          return true;
        } else if (response.contains("Missing required fields")) {
          LOGGER.warning("Update student failed: Missing required fields");
          return false;
        } else if (response.contains("Invalid ID, age or grade value")) {
          LOGGER.warning("Update student failed: Invalid ID, age or grade value");
          return false;
        } else if (response.contains("Student not found or update failed")) {
          LOGGER.warning("Update student failed: Student not found");
          return false;
        }
      }

      LOGGER.warning("Update student failed: Unknown response: " + response);
      return false;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to update student", e);
      return false;
    }
  }

  public boolean deleteStudent(int id) {
    try {
      String response = makeRequest("DELETE", "/students/" + id, null);
      return response != null && response.contains("successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to delete student", e);
      return false;
    }
  }

  // Search and filtering
  public ObservableList<Student> searchStudents(String criteria, String value) {
    try {
      String query = String.format("search=%s&value=%s",
          URLEncoder.encode(criteria, StandardCharsets.UTF_8),
          URLEncoder.encode(value, StandardCharsets.UTF_8));

      String response = makeRequest("GET", "/students?" + query, null);
      return parseStudentsFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to search students", e);
      return FXCollections.observableArrayList();
    }
  }

  public ObservableList<Student> sortStudents(String field, String order) {
    try {
      String query = String.format("sort=%s&order=%s",
          URLEncoder.encode(field, StandardCharsets.UTF_8),
          URLEncoder.encode(order, StandardCharsets.UTF_8));

      String response = makeRequest("GET", "/students?" + query, null);
      return parseStudentsFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to sort students", e);
      return FXCollections.observableArrayList();
    }
  }

  public ObservableList<Student> getStudentsPaginated(int page, int size) {
    try {
      String query = String.format("page=%d&size=%d", page, size);
      String response = makeRequest("GET", "/students?" + query, null);
      return parseStudentsFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to get paginated students", e);
      return FXCollections.observableArrayList();
    }
  }

  // Statistics
  public Map<String, Object> getStatistics() {
    try {
      String response = makeRequest("GET", "/students/statistics", null);
      return parseStatisticsFromResponse(response);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to get statistics", e);
      return Map.of("totalStudents", 0, "averageGrade", 0.0);
    }
  }

  // Import/Export
  public boolean exportStudents(String format, String filename) {
    try {
      String query = String.format("format=%s&filename=%s",
          URLEncoder.encode(format, StandardCharsets.UTF_8),
          URLEncoder.encode(filename, StandardCharsets.UTF_8));

      String response = makeRequest("GET", "/students/export?" + query, null);
      return response != null && response.contains("successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to export students", e);
      return false;
    }
  }

  public boolean importStudents(String format, String filename) {
    try {
      String postData = String.format("format=%s&filename=%s",
          URLEncoder.encode(format, StandardCharsets.UTF_8),
          URLEncoder.encode(filename, StandardCharsets.UTF_8));

      String response = makeRequest("POST", "/students/import", postData);
      return response != null && response.contains("successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to import students", e);
      return false;
    }
  }

  // Health check
  public boolean isServerRunning() {
    try {
      String response = makeRequest("GET", "/health", null);
      return response != null && response.contains("running");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Server health check failed", e);
      return false;
    }
  }

  // Helper methods
  private String makeRequest(String method, String endpoint, String postData) throws IOException {
    URL url = new URL(BASE_URL + endpoint);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);

    if (postData != null) {
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Length", String.valueOf(postData.length()));

      try (OutputStream os = connection.getOutputStream()) {
        os.write(postData.getBytes(StandardCharsets.UTF_8));
      }
    }

    int responseCode = connection.getResponseCode();
    LOGGER.info("HTTP " + method + " " + endpoint + " - Response Code: " + responseCode);

    // Read response from either input stream (success) or error stream (error)
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(responseCode >= 200 && responseCode < 300
            ? connection.getInputStream()
            : connection.getErrorStream()))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      String responseText = response.toString();
      LOGGER.info("Response body: " + responseText);
      return responseText;
    }
  }

  private ObservableList<Student> parseStudentsFromResponse(String response) {
    ObservableList<Student> students = FXCollections.observableArrayList();
    if (response == null || response.isEmpty()) {
      return students;
    }

    // Simple parsing - in production, use proper JSON parsing
    String[] lines = response.split("Student\\{");
    for (String line : lines) {
      if (line.contains("id=") && line.contains("firstName=")) {
        Student student = parseStudentFromString(line);
        if (student != null) {
          students.add(student);
        }
      }
    }
    return students;
  }

  private Student parseStudentFromResponse(String response) {
    if (response == null || response.isEmpty()) {
      return null;
    }
    return parseStudentFromString(response);
  }

  private Student parseStudentFromString(String studentStr) {
    try {
      // Parse: Student{id=1, firstName='John', lastName='Doe', age=20, grade=15.50}
      String[] parts = studentStr.split(",");
      int id = Integer.parseInt(parts[0].split("=")[1]);
      String firstName = parts[1].split("=")[1].replace("'", "");
      String lastName = parts[2].split("=")[1].replace("'", "");
      int age = Integer.parseInt(parts[3].split("=")[1]);
      double grade = Double.parseDouble(parts[4].split("=")[1].replace("}", ""));

      return new Student(id, firstName, lastName, age, grade);
    } catch (Exception e) {
      LOGGER.warning("Failed to parse student: " + studentStr);
      return null;
    }
  }

  private Map<String, Object> parseStatisticsFromResponse(String response) {
    try {
      // Parse: {"totalStudents":25,"averageGrade":14.75}
      String totalStr = response.split("totalStudents\":")[1].split(",")[0];
      String avgStr = response.split("averageGrade\":")[1].split("}")[0];

      int totalStudents = Integer.parseInt(totalStr);
      double averageGrade = Double.parseDouble(avgStr);

      return Map.of("totalStudents", totalStudents, "averageGrade", averageGrade);
    } catch (Exception e) {
      LOGGER.warning("Failed to parse statistics: " + response);
      return Map.of("totalStudents", 0, "averageGrade", 0.0);
    }
  }
}