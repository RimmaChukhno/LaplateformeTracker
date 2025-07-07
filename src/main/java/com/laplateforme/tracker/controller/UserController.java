package com.laplateforme.tracker.controller;

import com.laplateforme.tracker.dao.UserDAO;
import com.laplateforme.tracker.model.User;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserController implements HttpHandler {
    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("POST") && path.equals("/login")) {
            handleLogin(exchange);
        } else {
            sendResponse(exchange, 404, "Not Found");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        // Parse request body
        String body = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> params = parseQuery(body);

        String username = params.get("username");
        String password = params.get("password");

        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            sendResponse(exchange, 200, "Login successful");
        } else {
            sendResponse(exchange, 401, "Invalid credentials");
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