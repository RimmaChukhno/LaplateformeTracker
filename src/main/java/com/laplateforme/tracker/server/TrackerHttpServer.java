package com.laplateforme.tracker.server;

import com.laplateforme.tracker.controller.UserController;
import com.laplateforme.tracker.controller.StudentController;
import com.laplateforme.tracker.database.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class TrackerHttpServer {
    public static void main(String[] args) throws IOException {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.initDatabase();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/login", new UserController());
        server.createContext("/students", new StudentController());
        server.setExecutor(null);
        server.start();
        System.out.println("Serveur démarré sur le port 8080");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Welcome to the Student Tracker API";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Server is running";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
