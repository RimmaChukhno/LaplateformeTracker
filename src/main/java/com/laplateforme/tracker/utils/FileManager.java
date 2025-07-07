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
        // Implémentation simplifiée - dans un vrai projet, utilisez Jackson ou Gson
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String content = reader.lines().reduce("", String::concat);
            // Parse JSON basique (à améliorer avec une vraie bibliothèque JSON)
            LOGGER.info("Import JSON basique implémenté - utiliser Jackson/Gson pour production");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'import JSON", e);
            return null;
        }
        return students;
    }
}