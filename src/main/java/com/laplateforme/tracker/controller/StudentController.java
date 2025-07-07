package com.laplateforme.tracker.controller;

import com.laplateforme.tracker.dao.StudentDAO;
import com.laplateforme.tracker.model.Student;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StudentController implements HttpHandler {
    private final StudentDAO studentDAO;

    public StudentController() {
        this.studentDAO = new StudentDAO();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                if (path.equals("/students")) {
                    handleGetAllStudents(exchange);
                } else if (path.startsWith("/students/")) {
                    handleGetStudentById(exchange);
                }
                break;
            case "POST":
                if (path.equals("/students")) {
                    handleAddStudent(exchange);
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
        int id = Integer.parseInt(pathParts[pathParts.length - 1]);
        Student student = studentDAO.getStudentById(id);
        if (student != null) {
            sendResponse(exchange, 200, student.toString());
        } else {
            sendResponse(exchange, 404, "Student not found");
        }
    }

    private void handleAddStudent(HttpExchange exchange) throws IOException {



        // Parse request body
        String body = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> params = parseQuery(body);

        String firstName = params.get("firstName");
        String lastName = params.get("lastName");
        int age = Integer.parseInt(params.get("age"));
        double grade = Double.parseDouble(params.get("grade"));

        Student student = new Student(firstName, lastName, age, grade);
        boolean success = studentDAO.addStudent(student);

        if (success) {
            sendResponse(exchange, 201, "Student added");
        } else {
            sendResponse(exchange, 500, "Failed to add student");
        }
    }

    private void handleUpdateStudent(HttpExchange exchange) throws IOException {
        // Parse request body and update Student object
        // Update student in database
        sendResponse(exchange, 200, "Student updated");
    }

    private void handleDeleteStudent(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[pathParts.length - 1]);
        boolean success = studentDAO.deleteStudent(id);
        if (success) {
            sendResponse(exchange, 200, "Student deleted");
        } else {
            sendResponse(exchange, 404, "Student not found");
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}