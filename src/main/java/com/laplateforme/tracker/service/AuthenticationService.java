package com.laplateforme.tracker.service;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private final Map<String, String> users;
    private String currentUser;

    public AuthenticationService() {
        this.users = new HashMap<>();
        // Utilisateurs par défaut
        users.put("admin", "admin123");
        users.put("user", "user123");
    }

    public boolean login(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            currentUser = username;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public boolean addUser(String username, String password) {
        if (username != null && password != null && !users.containsKey(username)) {
            users.put(username, password);
            return true;
        }
        return false;
    }
}
