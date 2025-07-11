package com.laplateforme.tracker.utils;

import com.laplateforme.tracker.model.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FileManager {
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    public boolean exportToCSV(List<Student> students, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Prénom,Nom,Âge,Note");
            for (Student student : students) {
                writer.printf("%d,%s,%s,%d,%.2f%n",
                    student.getId(), student.getFirstName(), student.getLastName(),
                    student.getAge(), student.getGrade());
            }
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'export CSV", e);
            return false;
        }
    }

    public List<Student> importFromCSV(String filename) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Student student = new Student(
                        Integer.parseInt(parts[0]),
                        parts[1], parts[2],
                        Integer.parseInt(parts[3]),
                        Double.parseDouble(parts[4])
                    );
                    students.add(student);
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'import CSV", e);
            return null;
        }
        return students;
    }

    public boolean exportToJSON(List<Student> students, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("[");
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                writer.printf("  {\"id\":%d,\"firstName\":\"%s\",\"lastName\":\"%s\",\"age\":%d,\"grade\":%.2f}",
                    student.getId(), student.getFirstName(), student.getLastName(),
                    student.getAge(), student.getGrade());
                if (i < students.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("]");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'export JSON", e);
            return false;
        }
    }

    public List<Student> importFromJSON(String filename) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            String jsonContent = content.toString().trim();
            
            // Basic JSON parsing for array format: [{"id":1,"firstName":"John",...}]
            if (jsonContent.startsWith("[") && jsonContent.endsWith("]")) {
                String arrayContent = jsonContent.substring(1, jsonContent.length() - 1);
                String[] objects = arrayContent.split("\\},\\s*\\{");
                
                for (int i = 0; i < objects.length; i++) {
                    String obj = objects[i];
                    if (i == 0) obj = obj.substring(1); // Remove first {
                    if (i == objects.length - 1) obj = obj.substring(0, obj.length() - 1); // Remove last }
                    else obj = obj + "}"; // Add back }
                    
                    Student student = parseStudentFromJSON(obj);
                    if (student != null) {
                        students.add(student);
                    }
                }
            }
            
            LOGGER.info("Import JSON: " + students.size() + " étudiants importés");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'import JSON", e);
            return null;
        }
        return students;
    }
    
    private Student parseStudentFromJSON(String jsonObject) {
        try {
            // Extract values using regex for simple JSON parsing
            String idMatch = extractValue(jsonObject, "id");
            String firstNameMatch = extractValue(jsonObject, "firstName");
            String lastNameMatch = extractValue(jsonObject, "lastName");
            String ageMatch = extractValue(jsonObject, "age");
            String gradeMatch = extractValue(jsonObject, "grade");
            
            if (idMatch != null && firstNameMatch != null && lastNameMatch != null && 
                ageMatch != null && gradeMatch != null) {
                
                return new Student(
                    Integer.parseInt(idMatch),
                    firstNameMatch,
                    lastNameMatch,
                    Integer.parseInt(ageMatch),
                    Double.parseDouble(gradeMatch)
                );
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Erreur de parsing JSON pour l'objet: " + jsonObject);
        }
        return null;
    }
    
    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"?([^\",\\}]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}