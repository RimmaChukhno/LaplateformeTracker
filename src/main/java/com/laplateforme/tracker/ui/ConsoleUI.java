package com.laplateforme.tracker.ui;

import com.laplateforme.tracker.model.Student;
import com.laplateforme.tracker.service.AuthenticationService;
import com.laplateforme.tracker.service.StudentService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final StudentService studentService;
    private final AuthenticationService authService;
    private final Scanner scanner;

    public ConsoleUI() {
        this.studentService = new StudentService();
        this.authService = new AuthenticationService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== LA PLATEFORME TRACKER ===");

        // Authentification
        if (!authenticate()) {
            System.out.println("Authentification échouée. Au revoir!");
            return;
        }

        // Menu principal
        while (true) {
            showMainMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consommer la ligne

                if (!handleUserChoice(choice)) {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur: Veuillez entrer un nombre valide.");
                scanner.nextLine(); // Nettoyer le buffer
            }
        }

        System.out.println("Au revoir!");
        scanner.close();
    }

    private boolean authenticate() {
        System.out.println("\n=== AUTHENTIFICATION ===");
        for (int attempts = 0; attempts < 3; attempts++) {
            System.out.print("Nom d'utilisateur: ");
            String username = scanner.nextLine();
            System.out.print("Mot de passe: ");
            String password = scanner.nextLine();

            if (authService.login(username, password)) {
                System.out.println("Connexion réussie! Bienvenue " + username);
                return true;
            } else {
                System.out.println("Identifiants incorrects. Tentatives restantes: " + (2 - attempts));
            }
        }
        return false;
    }

    private void showMainMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Ajouter un étudiant");
        System.out.println("2. Modifier un étudiant");
        System.out.println("3. Supprimer un étudiant");
        System.out.println("4. Afficher tous les étudiants");
        System.out.println("5. Rechercher un étudiant");
        System.out.println("6. Recherche avancée");
        System.out.println("7. Trier les étudiants");
        System.out.println("8. Afficher les statistiques");
        System.out.println("9. Import/Export");
        System.out.println("10. Affichage paginé");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1 -> addStudent();
            case 2 -> updateStudent();
            case 3 -> deleteStudent();
            case 4 -> displayAllStudents();
            case 5 -> searchStudent();
            case 6 -> advancedSearch();
            case 7 -> sortStudents();
            case 8 -> showStatistics();
            case 9 -> handleImportExport();
            case 10 -> displayPaginated();
            case 0 -> {
                return false;
            }
            default -> System.out.println("Choix invalide!");
        }
        return true;
    }

    private void addStudent() {
        System.out.println("\n=== AJOUTER UN ÉTUDIANT ===");
        Student student = inputStudent();
        if (student != null && studentService.addStudent(student)) {
            System.out.println("Étudiant ajouté avec succès!");
        } else {
            System.out.println("Erreur lors de l'ajout de l'étudiant.");
        }
    }

    private void updateStudent() {
        System.out.println("\n=== MODIFIER UN ÉTUDIANT ===");
        System.out.print("ID de l'étudiant à modifier: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();

            Student existing = studentService.getStudentById(id);
            if (existing == null) {
                System.out.println("Étudiant non trouvé.");
                return;
            }

            System.out.println("Étudiant actuel: " + existing);
            System.out.println("Entrez les nouvelles informations:");

            Student updated = inputStudent();
            if (updated != null) {
                updated.setId(id);
                if (studentService.updateStudent(updated)) {
                    System.out.println("Étudiant modifié avec succès!");
                } else {
                    System.out.println("Erreur lors de la modification.");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("ID invalide.");
            scanner.nextLine();
        }
    }

    private void deleteStudent() {
        System.out.println("\n=== SUPPRIMER UN ÉTUDIANT ===");
        System.out.print("ID de l'étudiant à supprimer: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();

            Student student = studentService.getStudentById(id);
            if (student == null) {
                System.out.println("Étudiant non trouvé.");
                return;
            }

            System.out.println("Étudiant à supprimer: " + student);
            System.out.print("Confirmer la suppression (o/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("o")) {
                if (studentService.deleteStudent(id)) {
                    System.out.println("Étudiant supprimé avec succès!");
                } else {
                    System.out.println("Erreur lors de la suppression.");
                }
            } else {
                System.out.println("Suppression annulée.");
            }
        } catch (InputMismatchException e) {
            System.out.println("ID invalide.");
            scanner.nextLine();
        }
    }

    private void displayAllStudents() {
        System.out.println("\n=== TOUS LES ÉTUDIANTS ===");
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("Aucun étudiant trouvé.");
        } else {
            for (Student student : students) {
                System.out.println(student);
            }
        }
    }

    private void searchStudent() {
        System.out.println("\n=== RECHERCHER UN ÉTUDIANT ===");
        System.out.println("1. Rechercher par prénom");
        System.out.println("2. Rechercher par nom");
        System.out.println("3. Rechercher par âge");
        System.out.println("4. Rechercher par note");
        System.out.print("Votre choix: ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne
            
            String criteria = "";
            Object value = null;
            
            switch (choice) {
                case 1 -> {
                    System.out.print("Prénom à rechercher: ");
                    criteria = "first_name";
                    value = scanner.nextLine();
                }
                case 2 -> {
                    System.out.print("Nom à rechercher: ");
                    criteria = "last_name";
                    value = scanner.nextLine();
                }
                case 3 -> {
                    System.out.print("Âge à rechercher: ");
                    criteria = "age";
                    value = Integer.parseInt(scanner.nextLine());
                }
                case 4 -> {
                    System.out.print("Note à rechercher: ");
                    criteria = "grade";
                    value = Double.parseDouble(scanner.nextLine());
                }
                default -> {
                    System.out.println("Choix invalide!");
                    return;
                }
            }
            
            List<Student> results = studentService.searchStudents(criteria, value);
            if (results.isEmpty()) {
                System.out.println("Aucun étudiant trouvé avec ce critère.");
            } else {
                System.out.println("Résultats trouvés (" + results.size() + "):");
                for (Student student : results) {
                    System.out.println(student);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Erreur: Veuillez entrer un nombre valide.");
            scanner.nextLine();
        } catch (NumberFormatException e) {
            System.out.println("Erreur: Veuillez entrer un nombre valide pour l'âge/note.");
        }
    }

    private void advancedSearch() {
        System.out.println("\n=== RECHERCHE AVANCÉE ===");
        System.out.println("Critères disponibles: first_name, last_name, age, grade");
        System.out.print("Critère de recherche: ");
        String criterion = scanner.nextLine();
        
        // Validate criterion
        if (!criterion.matches("first_name|last_name|age|grade")) {
            System.out.println("Critère invalide. Utilisez: first_name, last_name, age, ou grade");
            return;
        }
        
        System.out.print("Valeur: ");
        String value = scanner.nextLine();
        
        Object searchValue = value;
        // Convert value based on criterion
        if (criterion.equals("age")) {
            try {
                searchValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Erreur: L'âge doit être un nombre entier.");
                return;
            }
        } else if (criterion.equals("grade")) {
            try {
                searchValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Erreur: La note doit être un nombre décimal.");
                return;
            }
        }

        List<Student> results = studentService.searchStudents(criterion, searchValue);
        if (results.isEmpty()) {
            System.out.println("Aucun étudiant trouvé avec ce critère.");
        } else {
            System.out.println("Résultats trouvés (" + results.size() + "):");
            for (Student student : results) {
                System.out.println(student);
            }
        }
    }

    private void sortStudents() {
        System.out.println("\n=== TRIER LES ÉTUDIANTS ===");
        System.out.println("Critères de tri disponibles: id, first_name, last_name, age, grade");
        System.out.print("Critère de tri: ");
        String criterion = scanner.nextLine();
        
        // Validate criterion to prevent SQL injection
        if (!criterion.matches("id|first_name|last_name|age|grade")) {
            System.out.println("Critère invalide. Utilisez: id, first_name, last_name, age, ou grade");
            return;
        }
        
        System.out.print("Ordre (asc/desc): ");
        String order = scanner.nextLine();
        
        // Validate order
        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            System.out.println("Ordre invalide. Utilisez: asc ou desc");
            return;
        }

        List<Student> sortedStudents = studentService.sortStudents(criterion, order.toUpperCase());
        if (sortedStudents.isEmpty()) {
            System.out.println("Aucun étudiant trouvé.");
        } else {
            System.out.println("Étudiants triés par " + criterion + " (" + order + "):");
            for (Student student : sortedStudents) {
                System.out.println(student);
            }
        }
    }

    private void showStatistics() {
        System.out.println("\n=== STATISTIQUES ===");
        double avg = studentService.calculateClassAverage();
        Map<String, Integer> stats = studentService.getAgeStatistics();
        System.out.println("Moyenne de la classe: " + avg);
        if (stats.isEmpty()) {
            System.out.println("Aucune statistique d'âge disponible.");
        } else {
            stats.forEach((key, value) -> System.out.println(key + ": " + value));
        }
    }

    private void handleImportExport() {
        System.out.println("\n=== IMPORT/EXPORT ===");
        System.out.println("1. Importer des étudiants");
        System.out.println("2. Exporter des étudiants");
        System.out.print("Votre choix: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne

            switch (choice) {
                case 1 -> {
                    System.out.print("Format (csv/json): ");
                    String format = scanner.nextLine();
                    System.out.print("Nom du fichier: ");
                    String filename = scanner.nextLine();
                    if (format.equalsIgnoreCase("csv")) {
                        studentService.importFromCSV(filename);
                    } else if (format.equalsIgnoreCase("json")) {
                        studentService.importFromJSON(filename);
                    } else {
                        System.out.println("Format non supporté.");
                    }
                }
                case 2 -> {
                    System.out.print("Format (csv/json): ");
                    String format = scanner.nextLine();
                    System.out.print("Nom du fichier: ");
                    String filename = scanner.nextLine();
                    if (format.equalsIgnoreCase("csv")) {
                        studentService.exportToCSV(filename);
                    } else if (format.equalsIgnoreCase("json")) {
                        studentService.exportToJSON(filename);
                    } else {
                        System.out.println("Format non supporté.");
                    }
                }
                default -> System.out.println("Choix invalide!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Erreur: Veuillez entrer un nombre valide.");
            scanner.nextLine(); // Nettoyer le buffer
        }
    }

    private void displayPaginated() {
        System.out.println("\n=== AFFICHAGE PAGINÉ ===");
        System.out.print("Nombre d'étudiants par page: ");

        try {
            int pageSize = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne
            int page = 1;
            List<Student> paginatedStudents;
            do {
                paginatedStudents = studentService.getStudentsPaginated(page, pageSize);
                if (paginatedStudents.isEmpty()) {
                    System.out.println("Aucun étudiant trouvé.");
                    break;
                }
                System.out.println("Page " + page + ":");
                for (Student student : paginatedStudents) {
                    System.out.println(student);
                }
                System.out.print("Afficher la page suivante ? (o/n): ");
                String next = scanner.nextLine();
                if (!next.equalsIgnoreCase("o")) break;
                page++;
            } while (true);
        } catch (InputMismatchException e) {
            System.out.println("Erreur: Veuillez entrer un nombre valide.");
            scanner.nextLine(); // Nettoyer le buffer
        }
    }

    private Student inputStudent() {
        System.out.print("Nom: ");
        String name = scanner.nextLine();
        System.out.print("Prénom: ");
        String firstName = scanner.nextLine();
        System.out.print("Âge: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Note: ");
        double grade = Double.parseDouble(scanner.nextLine());

        // Validation basique
        if (name.isEmpty() || firstName.isEmpty()) {
            System.out.println("Erreur: Tous les champs sont requis.");
            return null;
        }

        return new Student(firstName, name, age, grade);
    }

    public static void main(String[] args) {
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}
// Note: This code assumes that the StudentService and AuthenticationService
// classes are implemented correctly.