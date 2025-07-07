package com.laplateforme.tracker.dao;

import com.laplateforme.tracker.database.DatabaseConnection;
import com.laplateforme.tracker.model.User;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserDAO {
    private final DatabaseConnection dbConnection;
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try {
            int rowsAffected = dbConnection.executeUpdate(sql, user.getUsername(), user.getPassword());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (ResultSet rs = dbConnection.executeQuery(sql, username)) {
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user", e);
        }
        return null;
    }
}