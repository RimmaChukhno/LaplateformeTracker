package com.laplateforme.tracker.test;

import org.junit.jupiter.api.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {
    private static Thread serverThread;

    @BeforeAll
    public static void startServer() {
        serverThread = new Thread(() -> {
            try {
                com.laplateforme.tracker.server.TrackerHttpServer.main(new String[]{});
            } catch (Exception e) {
                // Ignore if already started
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        try {
            Thread.sleep(2000); // Wait for server to start
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        URL url = new URL("http://localhost:8080/health");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        assertEquals(200, status);
        assertEquals("OK", content.toString());
    }
} 