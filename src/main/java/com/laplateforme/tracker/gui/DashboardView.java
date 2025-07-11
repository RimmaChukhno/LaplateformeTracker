package com.laplateforme.tracker.gui;

import com.laplateforme.tracker.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardView extends VBox {
  private final StudentTrackerApp app;
  private final ApiService apiService;
  private final AnalyticsView analyticsView;

  // UI Components
  private Label welcomeLabel;
  private TextField searchField;
  private ComboBox<String> searchCriteria;
  private TableView<Student> studentTable;
  private Pagination pagination;
  private Label statusLabel;

  // Data
  private ObservableList<Student> allStudents;
  private ObservableList<Student> filteredStudents;
  private int currentPage = 1;
  private int pageSize = 10;

  public DashboardView(StudentTrackerApp app) {
    this.app = app;
    this.apiService = new ApiService();
    this.analyticsView = new AnalyticsView(null);

    this.allStudents = FXCollections.observableArrayList();
    this.filteredStudents = FXCollections.observableArrayList();

    setupUI();
    setupEventHandlers();
  }

  private void setupUI() {
    setSpacing(20);
    setPadding(new Insets(20));

    // Header
    setupHeader();

    // Search and Actions Bar
    setupSearchAndActions();

    // Table
    setupTable();

    // Pagination
    setupPagination();

    // Status Bar
    setupStatusBar();
  }

  private void setupHeader() {
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(20));
    header.getStyleClass().add("dashboard-header");

    VBox titleContainer = new VBox(5);

    welcomeLabel = new Label("Welcome to Student Tracker");
    welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
    welcomeLabel.getStyleClass().add("dashboard-title");

    Label subtitle = new Label("Manage your students efficiently");
    subtitle.getStyleClass().add("dashboard-subtitle");

    titleContainer.getChildren().addAll(welcomeLabel, subtitle);

    // Logout button
    Button logoutButton = new Button("Logout");
    logoutButton.getStyleClass().addAll("btn", "btn-danger");
    logoutButton.setOnAction(e -> app.showLoginView());

    HBox.setHgrow(titleContainer, Priority.ALWAYS);
    header.getChildren().addAll(titleContainer, logoutButton);

    getChildren().add(header);
  }

  private void setupSearchAndActions() {
    VBox container = new VBox(15);
    container.getStyleClass().add("search-container");

    // Search row
    HBox searchRow = new HBox(10);
    searchRow.setAlignment(Pos.CENTER_LEFT);

    Label searchLabel = new Label("Search:");
    searchLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

    searchCriteria = new ComboBox<>();
    searchCriteria.getItems().addAll("All", "First Name", "Last Name", "Age", "Grade");
    searchCriteria.setValue("All");
    searchCriteria.setPrefWidth(120);

    searchField = new TextField();
    searchField.setPromptText("Enter search term...");
    searchField.setPrefWidth(200);
    searchField.getStyleClass().add("search-field");

    Button searchButton = new Button("Search");
    searchButton.getStyleClass().addAll("btn", "btn-primary");
    searchButton.setOnAction(e -> performSearch());

    Button clearButton = new Button("Clear");
    clearButton.getStyleClass().addAll("btn", "btn");
    clearButton.setOnAction(e -> {
      searchField.clear();
      filteredStudents.clear();
      filteredStudents.addAll(allStudents);
      updatePagination();
      updateStatus();
    });

    searchRow.getChildren().addAll(searchLabel, searchCriteria, searchField, searchButton, clearButton);

    // Actions row
    HBox actionsRow = new HBox(10);
    actionsRow.setAlignment(Pos.CENTER_LEFT);

    Button addButton = new Button("Add Student");
    addButton.getStyleClass().addAll("btn", "btn-success");
    addButton.setOnAction(e -> addStudent());

    Button updateButton = new Button("Update");
    updateButton.getStyleClass().addAll("btn", "btn-warning");
    updateButton.setOnAction(e -> updateStudent());

    Button deleteButton = new Button("Delete");
    deleteButton.getStyleClass().addAll("btn", "btn-danger");
    deleteButton.setOnAction(e -> deleteStudent());

    Button analyticsButton = new Button("Analytics");
    analyticsButton.getStyleClass().addAll("btn", "btn-primary");
    analyticsButton.setOnAction(e -> showAnalytics());

    Button importButton = new Button("Import");
    importButton.getStyleClass().addAll("btn", "btn");
    importButton.setOnAction(e -> importData());

    Button exportButton = new Button("Export");
    exportButton.getStyleClass().addAll("btn", "btn");
    exportButton.setOnAction(e -> exportData());

    actionsRow.getChildren().addAll(addButton, updateButton, deleteButton, analyticsButton, importButton, exportButton);

    container.getChildren().addAll(searchRow, actionsRow);
    getChildren().add(container);
  }

  private void setupTable() {
    studentTable = new TableView<>();
    studentTable.setPrefHeight(400);
    studentTable.getStyleClass().add("table-view");

    // Create columns
    TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    idCol.setPrefWidth(50);

    TableColumn<Student, String> firstNameCol = new TableColumn<>("First Name");
    firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    firstNameCol.setPrefWidth(150);

    TableColumn<Student, String> lastNameCol = new TableColumn<>("Last Name");
    lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lastNameCol.setPrefWidth(150);

    TableColumn<Student, Integer> ageCol = new TableColumn<>("Age");
    ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
    ageCol.setPrefWidth(80);

    TableColumn<Student, Double> gradeCol = new TableColumn<>("Grade");
    gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
    gradeCol.setPrefWidth(100);

    studentTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, ageCol, gradeCol);
    studentTable.setItems(filteredStudents);

    getChildren().add(studentTable);
  }

  private void setupPagination() {
    pagination = new Pagination();
    pagination.setPageCount(1);
    pagination.setCurrentPageIndex(0);
    pagination.getStyleClass().add("pagination");

    getChildren().add(pagination);
  }

  private void setupStatusBar() {
    HBox statusBar = new HBox();
    statusBar.setAlignment(Pos.CENTER_LEFT);
    statusBar.setPadding(new Insets(10));

    statusLabel = new Label("Ready");
    statusLabel.getStyleClass().add("info-message");

    statusBar.getChildren().add(statusLabel);
    getChildren().add(statusBar);
  }

  private void setupEventHandlers() {
    // Search functionality
    searchField.setOnAction(e -> performSearch());

    // Pagination
    pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
      currentPage = newVal.intValue() + 1;
      loadData();
    });

    // Table selection
    studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      updateButtonStates();
    });
  }

  public void loadData() {
    showStatus("Loading data...");

    new Thread(() -> {
      try {
        ObservableList<Student> students = apiService.getAllStudents();

        javafx.application.Platform.runLater(() -> {
          allStudents.clear();
          allStudents.addAll(students);

          applyFilters();
          updatePagination();
          updateStatus();
        });

      } catch (Exception e) {
        javafx.application.Platform.runLater(() -> {
          showStatus("Error loading data: " + e.getMessage());
        });
      }
    }).start();
  }

  private void performSearch() {
    String searchTerm = searchField.getText().trim();
    String criteria = searchCriteria.getValue();

    if (searchTerm.isEmpty()) {
      filteredStudents.clear();
      filteredStudents.addAll(allStudents);
      return;
    }

    showStatus("Searching...");

    new Thread(() -> {
      try {
        String searchField = getSearchField(criteria);
        ObservableList<Student> results = apiService.searchStudents(searchField, searchTerm);

        javafx.application.Platform.runLater(() -> {
          filteredStudents.clear();
          filteredStudents.addAll(results);
          updatePagination();
          showStatus("Search completed. Found " + results.size() + " students.");
        });

      } catch (Exception e) {
        javafx.application.Platform.runLater(() -> {
          showStatus("Search failed: " + e.getMessage());
        });
      }
    }).start();
  }

  private String getSearchField(String criteria) {
    switch (criteria) {
      case "First Name":
        return "first_name";
      case "Last Name":
        return "last_name";
      case "Age":
        return "age";
      case "Grade":
        return "grade";
      default:
        return "first_name";
    }
  }

  private void applyFilters() {
    filteredStudents.clear();
    filteredStudents.addAll(allStudents);
  }

  private void updatePagination() {
    int totalPages = (int) Math.ceil((double) filteredStudents.size() / pageSize);
    pagination.setPageCount(Math.max(1, totalPages));
  }

  private void updateStatus() {
    int total = allStudents.size();
    int filtered = filteredStudents.size();

    if (total == filtered) {
      showStatus("Showing " + total + " students");
    } else {
      showStatus("Showing " + filtered + " of " + total + " students");
    }
  }

  private void updateButtonStates() {
    Student selected = studentTable.getSelectionModel().getSelectedItem();
    boolean hasSelection = selected != null;

    // Update and delete buttons should be enabled only when a student is selected
    // This would be implemented in the actual button handlers
  }

  private void showStatus(String message) {
    statusLabel.setText(message);
  }

  // Public methods for button actions
  public void showAnalytics() {
    analyticsView.show();
  }

  private void addStudent() {
    StudentDialog dialog = new StudentDialog(null, null);
    dialog.showAndWait().ifPresent(student -> {
      showStatus("Adding student...");

      new Thread(() -> {
        try {
          boolean success = apiService.addStudent(student);

          javafx.application.Platform.runLater(() -> {
            if (success) {
              showStatus("Student added successfully");
              loadData(); // Reload data
            } else {
              showStatus("Failed to add student. Please check your input and try again.");
            }
          });

        } catch (Exception e) {
          javafx.application.Platform.runLater(() -> {
            showStatus("Error adding student: " + e.getMessage());
          });
        }
      }).start();
    });
  }

  private void updateStudent() {
    Student selected = studentTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showStatus("Please select a student to update");
      return;
    }

    StudentDialog dialog = new StudentDialog(null, selected);
    dialog.showAndWait().ifPresent(updatedStudent -> {
      updatedStudent.setId(selected.getId()); // Preserve the ID
      showStatus("Updating student...");

      new Thread(() -> {
        try {
          boolean success = apiService.updateStudent(updatedStudent);

          javafx.application.Platform.runLater(() -> {
            if (success) {
              showStatus("Student updated successfully");
              loadData(); // Reload data
            } else {
              showStatus("Failed to update student. Please check your input and try again.");
            }
          });

        } catch (Exception e) {
          javafx.application.Platform.runLater(() -> {
            showStatus("Error updating student: " + e.getMessage());
          });
        }
      }).start();
    });
  }

  private void deleteStudent() {
    Student selected = studentTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showStatus("Please select a student to delete");
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Student");
    alert.setContentText(
        "Are you sure you want to delete " + selected.getFirstName() + " " + selected.getLastName() + "?");

    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        showStatus("Deleting student...");

        new Thread(() -> {
          try {
            boolean success = apiService.deleteStudent(selected.getId());

            javafx.application.Platform.runLater(() -> {
              if (success) {
                showStatus("Student deleted successfully");
                loadData(); // Reload data
              } else {
                showStatus("Failed to delete student");
              }
            });

          } catch (Exception e) {
            javafx.application.Platform.runLater(() -> {
              showStatus("Error deleting student: " + e.getMessage());
            });
          }
        }).start();
      }
    });
  }

  public void importData() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import Students");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      String format = file.getName().toLowerCase().endsWith(".csv") ? "csv" : "json";

      showStatus("Importing data...");

      new Thread(() -> {
        try {
          boolean success = apiService.importStudents(format, file.getAbsolutePath());

          javafx.application.Platform.runLater(() -> {
            if (success) {
              showStatus("Import completed successfully");
              loadData(); // Reload data
            } else {
              showStatus("Import failed");
            }
          });

        } catch (Exception e) {
          javafx.application.Platform.runLater(() -> {
            showStatus("Import error: " + e.getMessage());
          });
        }
      }).start();
    }
  }

  public void exportData() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export Students");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    fileChooser.setInitialFileName("students_" + timestamp);

    File file = fileChooser.showSaveDialog(null);
    if (file != null) {
      String format = file.getName().toLowerCase().endsWith(".csv") ? "csv" : "json";

      showStatus("Exporting data...");

      new Thread(() -> {
        try {
          boolean success = apiService.exportStudents(format, file.getAbsolutePath());

          javafx.application.Platform.runLater(() -> {
            if (success) {
              showStatus("Export completed successfully");
            } else {
              showStatus("Export failed");
            }
          });

        } catch (Exception e) {
          javafx.application.Platform.runLater(() -> {
            showStatus("Export error: " + e.getMessage());
          });
        }
      }).start();
    }
  }
}