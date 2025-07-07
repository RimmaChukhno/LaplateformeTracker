package com.laplateforme.tracker.database;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String TARGET_DB = "student_tracker";
    private static final String URL = "jdbc:postgresql://localhost:5432/" + TARGET_DB;
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "1111";
    private static DatabaseConnection instance;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            try {
                this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                if (e.getSQLState().equals("3D000")) {
                    LOGGER.info("Database '" + TARGET_DB + "' does not exist. Creating...");
                    try (Connection defaultConn = DriverManager.getConnection(DEFAULT_DB_URL, USERNAME, PASSWORD);
                            Statement stmt = defaultConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE " + TARGET_DB);
                        LOGGER.info("Database '" + TARGET_DB + "' created successfully.");
                    }
                    this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                } else {
                    throw e;
                }
            }
            LOGGER.info("Connexion à la base de données établie avec succès");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'obtention de la connexion", e);
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Connexion fermée avec succès");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la fermeture de la connexion", e);
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    public void initDatabase() {
        String createStudentTableSQL = """
                CREATE TABLE IF NOT EXISTS student (
                    id SERIAL PRIMARY KEY,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    age INTEGER NOT NULL CHECK (age > 0),
                    grade DECIMAL(4,2) NOT NULL CHECK (grade >= 0 AND grade <= 20)
                )
                """;

        String createUserTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL
                )
                """;

        try {
            executeUpdate(createStudentTableSQL);
            executeUpdate(createUserTableSQL);
            LOGGER.info("Tables 'student' and 'users' created or verified successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating tables", e);
            throw new RuntimeException("Unable to create tables", e);
        }
    }

    public static void main(String[] args) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.initDatabase();
    }
}
