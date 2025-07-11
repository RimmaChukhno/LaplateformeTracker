package com.laplateforme.tracker.controller;

import com.laplateforme.tracker.dao.StudentDAO;
import com.laplateforme.tracker.model.Student;
import com.laplateforme.tracker.utils.FileManager;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StudentController implements HttpHandler {
    private final StudentDAO studentDAO;
    private final FileManager fileManager;

    public StudentController() {
        this.studentDAO = new StudentDAO();
        this.fileManager = new FileManager();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        switch (method) {
            case "GET":
                if (path.equals("/students")) {
                    if (query != null && query.contains("search")) {
                        handleSearchStudents(exchange, query);
                    } else if (query != null && query.contains("sort")) {
                        handleSortStudents(exchange, query);
                    } else if (query != null && query.contains("page")) {
                        handlePaginatedStudents(exchange, query);
                    } else {
                        handleGetAllStudents(exchange);
                    }
                } else if (path.equals("/students/statistics")) {
                    handleGetStatistics(exchange);
                } else if (path.equals("/students/export")) {
                    handleExportStudents(exchange, query);
                } else if (path.startsWith("/students/")) {
                    handleGetStudentById(exchange);
                }
                break;
            case "POST":
                if (path.equals("/students")) {
                    handleAddStudent(exchange);
                } else if (path.equals("/students/import")) {
                    handleImportStudents(exchange);
                }
                break;
            case "PUT":
                if (path.startsWith("/students/")) {
                    handleUpdateStudent(exchange);
                }
                break;
            case "DELETE":
                if (path.startsWith("/students/")) {
                    handleDeleteStudent(exchange);
                }
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetAllStudents(HttpExchange exchange) throws IOException {
        List<Student> students = studentDAO.getAllStudents();
        sendResponse(exchange, 200, students.toString());
    }

    private void handleGetStudentById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            Student student = studentDAO.getStudentById(id);
            if (student != null) {
                sendResponse(exchange, 200, student.toString());
            } else {
                sendResponse(exchange, 404, "Student not found");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid student ID");
        }
    }

    private void handleSearchStudents(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        String criteria = params.get("search");
        String value = params.get("value");

        if (criteria == null || value == null) {
            sendResponse(exchange, 400, "Missing search criteria or value");
            return;
        }

        // Validate criteria to prevent SQL injection
        if (!criteria.matches("first_name|last_name|age|grade")) {
            sendResponse(exchange, 400, "Invalid search criteria");
            return;
        }

        Object searchValue = value;
        try {
            if (criteria.equals("age")) {
                searchValue = Integer.parseInt(value);
            } else if (criteria.equals("grade")) {
                searchValue = Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid value for numeric field");
            return;
        }

        List<Student> results = studentDAO.searchStudents(criteria, searchValue);
        sendResponse(exchange, 200, results.toString());
    }

    private void handleSortStudents(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        String field = params.get("sort");
        String order = params.get("order");

        if (field == null || order == null) {
            sendResponse(exchange, 400, "Missing sort field or order");
            return;
        }

        // Validate field and order to prevent SQL injection
        if (!field.matches("id|first_name|last_name|age|grade")) {
            sendResponse(exchange, 400, "Invalid sort field");
            return;
        }

        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            sendResponse(exchange, 400, "Invalid sort order (use asc or desc)");
            return;
        }

        List<Student> results = studentDAO.sortStudents(field, order.toUpperCase());
        sendResponse(exchange, 200, results.toString());
    }

    private void handlePaginatedStudents(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        String pageStr = params.get("page");
        String sizeStr = params.get("size");

        if (pageStr == null || sizeStr == null) {
            sendResponse(exchange, 400, "Missing page or size parameter");
            return;
        }

        try {
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);

            if (page < 1 || size < 1) {
                sendResponse(exchange, 400, "Page and size must be positive integers");
                return;
            }

            List<Student> results = studentDAO.getStudentsPaginated((page - 1) * size, size);
            sendResponse(exchange, 200, results.toString());
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid page or size value");
        }
    }

    private void handleGetStatistics(HttpExchange exchange) throws IOException {
        double averageGrade = studentDAO.getAverageGrade();
        int totalStudents = studentDAO.getStudentCount();

        String stats = String.format("{\"totalStudents\":%d,\"averageGrade\":%.2f}",
                totalStudents, averageGrade);
        sendResponse(exchange, 200, stats);
    }

    private void handleAddStudent(HttpExchange exchange) throws IOException {
        // Parse request body
        String body = new String(exchange.getRequestBody().readAllBytes());
        System.out.println("Add Student - Request body: " + body);

        Map<String, String> params = parseQuery(body);
        System.out.println("Add Student - Parsed params: " + params);

        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        String ageStr = params.get("age");
        String gradeStr = params.get("grade");

        if (firstName == null || lastName == null || ageStr == null || gradeStr == null) {
            System.out.println("Add Student - Missing required fields");
            sendResponse(exchange, 400, "Missing required fields");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            double grade = Double.parseDouble(gradeStr);

            Student student = new Student(firstName, lastName, age, grade);
            System.out.println("Add Student - Created student: " + student);

            boolean success = studentDAO.addStudent(student);
            System.out.println("Add Student - DAO result: " + success);

            if (success) {
                sendResponse(exchange, 201, "Student added successfully");
            } else {
                sendResponse(exchange, 500, "Failed to add student");
            }
        } catch (NumberFormatException e) {
            System.out.println("Add Student - Number format exception: " + e.getMessage());
            sendResponse(exchange, 400, "Invalid age or grade value");
        }
    }

    private void handleUpdateStudent(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            System.out.println("Update Student - ID: " + id);

            // Parse request body
            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Update Student - Request body: " + body);

            Map<String, String> params = parseQuery(body);
            System.out.println("Update Student - Parsed params: " + params);

            String firstName = params.get("firstName");
            String lastName = params.get("lastName");
            String ageStr = params.get("age");
            String gradeStr = params.get("grade");

            if (firstName == null || lastName == null || ageStr == null || gradeStr == null) {
                System.out.println("Update Student - Missing required fields");
                sendResponse(exchange, 400, "Missing required fields");
                return;
            }

            int age = Integer.parseInt(ageStr);
            double grade = Double.parseDouble(gradeStr);

            Student student = new Student(id, firstName, lastName, age, grade);
            System.out.println("Update Student - Created student: " + student);

            boolean success = studentDAO.updateStudent(student);
            System.out.println("Update Student - DAO result: " + success);

            if (success) {
                sendResponse(exchange, 200, "Student updated successfully");
            } else {
                sendResponse(exchange, 404, "Student not found or update failed");
            }
        } catch (NumberFormatException e) {
            System.out.println("Update Student - Number format exception: " + e.getMessage());
            sendResponse(exchange, 400, "Invalid ID, age or grade value");
        }
    }

    private void handleDeleteStudent(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            boolean success = studentDAO.deleteStudent(id);
            if (success) {
                sendResponse(exchange, 200, "Student deleted successfully");
            } else {
                sendResponse(exchange, 404, "Student not found");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid student ID");
        }
    }

    private void handleExportStudents(HttpExchange exchange, String query) throws IOException {
        Map<String, String> params = parseQuery(query);
        String format = params.get("format");
        String filename = params.get("filename");

        if (format == null || filename == null) {
            sendResponse(exchange, 400, "Missing format or filename parameter");
            return;
        }

        List<Student> students = studentDAO.getAllStudents();
        boolean success = false;

        switch (format.toLowerCase()) {
            case "csv":
                success = fileManager.exportToCSV(students, filename);
                break;
            case "json":
                success = fileManager.exportToJSON(students, filename);
                break;
            default:
                sendResponse(exchange, 400, "Unsupported format. Use 'csv' or 'json'");
                return;
        }

        if (success) {
            sendResponse(exchange, 200, "Export completed successfully");
        } else {
            sendResponse(exchange, 500, "Export failed");
        }
    }

    private void handleImportStudents(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> params = parseQuery(body);

        String format = params.get("format");
        String filename = params.get("filename");

        if (format == null || filename == null) {
            sendResponse(exchange, 400, "Missing format or filename parameter");
            return;
        }

        boolean success = false;
        int importedCount = 0;

        switch (format.toLowerCase()) {
            case "csv":
                List<Student> csvStudents = fileManager.importFromCSV(filename);
                if (csvStudents != null) {
                    for (Student student : csvStudents) {
                        if (studentDAO.addStudent(student)) {
                            importedCount++;
                        }
                    }
                    success = true;
                }
                break;
            case "json":
                List<Student> jsonStudents = fileManager.importFromJSON(filename);
                if (jsonStudents != null) {
                    for (Student student : jsonStudents) {
                        if (studentDAO.addStudent(student)) {
                            importedCount++;
                        }
                    }
                    success = true;
                }
                break;
            default:
                sendResponse(exchange, 400, "Unsupported format. Use 'csv' or 'json'");
                return;
        }

        if (success) {
            String response = String.format("Import completed. %d students imported successfully", importedCount);
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 500, "Import failed");
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        // Don't force JSON content-type for all responses
        // exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}