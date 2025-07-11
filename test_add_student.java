import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class test_add_student {
  public static void main(String[] args) {
    try {
      // Test data
      String firstName = "John";
      String lastName = "Doe";
      int age = 20;
      double grade = 15.5;

      // Create POST data
      String postData = String.format("firstName=%s&lastName=%s&age=%d&grade=%.2f",
          URLEncoder.encode(firstName, StandardCharsets.UTF_8),
          URLEncoder.encode(lastName, StandardCharsets.UTF_8),
          age,
          grade);

      System.out.println("Sending POST data: " + postData);

      // Make request
      URL url = new URL("http://localhost:8080/students");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Length", String.valueOf(postData.length()));

      // Send data
      try (OutputStream os = connection.getOutputStream()) {
        os.write(postData.getBytes(StandardCharsets.UTF_8));
      }

      // Get response
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code: " + responseCode);

      // Read response
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(responseCode >= 200 && responseCode < 300
              ? connection.getInputStream()
              : connection.getErrorStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        System.out.println("Response: " + response.toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}