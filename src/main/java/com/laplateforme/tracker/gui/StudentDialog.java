package com.laplateforme.tracker.gui;

import com.laplateforme.tracker.model.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StudentDialog extends Dialog<Student> {
  private final TextField firstNameField;
  private final TextField lastNameField;
  private final TextField ageField;
  private final TextField gradeField;
  private final Label messageLabel;

  public StudentDialog(Stage owner, Student student) {
    setTitle(student == null ? "Add New Student" : "Edit Student");
    setHeaderText(student == null ? "Enter student information" : "Update student information");

    // Set up the dialog
    initOwner(owner);
    initModality(Modality.APPLICATION_MODAL);
    initStyle(StageStyle.UTILITY);
    setResizable(false);

    // Create the custom content
    VBox content = new VBox(15);
    content.setPadding(new Insets(20));
    content.setPrefWidth(400);

    // Form fields
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setAlignment(Pos.CENTER_LEFT);

    // First Name
    Label firstNameLabel = new Label("First Name:");
    firstNameLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
    firstNameField = new TextField();
    firstNameField.setPromptText("Enter first name");
    firstNameField.setPrefHeight(35);
    firstNameField.getStyleClass().add("text-field");

    // Last Name
    Label lastNameLabel = new Label("Last Name:");
    lastNameLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
    lastNameField = new TextField();
    lastNameField.setPromptText("Enter last name");
    lastNameField.setPrefHeight(35);
    lastNameField.getStyleClass().add("text-field");

    // Age
    Label ageLabel = new Label("Age:");
    ageLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
    ageField = new TextField();
    ageField.setPromptText("Enter age");
    ageField.setPrefHeight(35);
    ageField.getStyleClass().add("text-field");

    // Grade
    Label gradeLabel = new Label("Grade:");
    gradeLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
    gradeField = new TextField();
    gradeField.setPromptText("Enter grade (0-20)");
    gradeField.setPrefHeight(35);
    gradeField.getStyleClass().add("text-field");

    // Add fields to grid
    grid.add(firstNameLabel, 0, 0);
    grid.add(firstNameField, 1, 0);
    grid.add(lastNameLabel, 0, 1);
    grid.add(lastNameField, 1, 1);
    grid.add(ageLabel, 0, 2);
    grid.add(ageField, 1, 2);
    grid.add(gradeLabel, 0, 3);
    grid.add(gradeField, 1, 3);

    // Message label
    messageLabel = new Label();
    messageLabel.setWrapText(true);
    messageLabel.setVisible(false);

    // Add content to dialog
    content.getChildren().addAll(grid, messageLabel);
    getDialogPane().setContent(content);

    // Set up dialog buttons
    ButtonType saveButtonType = new ButtonType(student == null ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

    // Set up result converter
    setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        if (validateForm()) {
          return createStudent();
        }
      }
      return null;
    });

    // Pre-fill fields if editing
    if (student != null) {
      firstNameField.setText(student.getFirstName());
      lastNameField.setText(student.getLastName());
      ageField.setText(String.valueOf(student.getAge()));
      gradeField.setText(String.valueOf(student.getGrade()));
    }

    // Set default focus
    firstNameField.requestFocus();

    // Add validation listeners
    setupValidation();
  }

  private void setupValidation() {
    firstNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    lastNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    ageField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    gradeField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
  }

  private boolean validateForm() {
    String firstName = firstNameField.getText().trim();
    String lastName = lastNameField.getText().trim();
    String ageText = ageField.getText().trim();
    String gradeText = gradeField.getText().trim();

    // Check for empty fields
    if (firstName.isEmpty()) {
      showError("First name is required");
      return false;
    }

    if (lastName.isEmpty()) {
      showError("Last name is required");
      return false;
    }

    if (ageText.isEmpty()) {
      showError("Age is required");
      return false;
    }

    if (gradeText.isEmpty()) {
      showError("Grade is required");
      return false;
    }

    // Validate age
    try {
      int age = Integer.parseInt(ageText);
      if (age <= 0 || age > 150) {
        showError("Age must be between 1 and 150");
        return false;
      }
    } catch (NumberFormatException e) {
      showError("Age must be a valid number");
      return false;
    }

    // Validate grade
    try {
      double grade = Double.parseDouble(gradeText);
      if (grade < 0 || grade > 20) {
        showError("Grade must be between 0 and 20");
        return false;
      }
    } catch (NumberFormatException e) {
      showError("Grade must be a valid number");
      return false;
    }

    hideError();
    return true;
  }

  private Student createStudent() {
    try {
      String firstName = firstNameField.getText().trim();
      String lastName = lastNameField.getText().trim();
      int age = Integer.parseInt(ageField.getText().trim());
      double grade = Double.parseDouble(gradeField.getText().trim());

      return new Student(firstName, lastName, age, grade);
    } catch (NumberFormatException e) {
      showError("Invalid number format");
      return null;
    }
  }

  private void showError(String message) {
    messageLabel.setText(message);
    messageLabel.getStyleClass().removeAll("success-message", "info-message");
    messageLabel.getStyleClass().add("error-message");
    messageLabel.setVisible(true);
  }

  private void hideError() {
    messageLabel.setVisible(false);
  }
}