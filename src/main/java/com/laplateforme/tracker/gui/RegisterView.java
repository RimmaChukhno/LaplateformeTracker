package com.laplateforme.tracker.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RegisterView extends VBox {
  private final StudentTrackerApp app;
  private final ApiService apiService;

  private TextField usernameField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;
  private Label messageLabel;
  private Button registerButton;
  private Button backButton;

  public RegisterView(StudentTrackerApp app) {
    this.app = app;
    this.apiService = new ApiService();

    setupUI();
    setupEventHandlers();
  }

  private void setupUI() {
    setAlignment(Pos.CENTER);
    setSpacing(20);
    setPadding(new Insets(40));
    getStyleClass().add("auth-container");

    // Title
    Label titleLabel = new Label("Create Account");
    titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
    titleLabel.getStyleClass().add("auth-title");
    titleLabel.setTextAlignment(TextAlignment.CENTER);

    // Subtitle
    Label subtitleLabel = new Label("Join Student Tracker and start managing your students.");
    subtitleLabel.getStyleClass().add("auth-subtitle");
    subtitleLabel.setTextAlignment(TextAlignment.CENTER);

    // Form container
    VBox formContainer = new VBox(15);
    formContainer.setAlignment(Pos.CENTER);
    formContainer.setMaxWidth(400);
    formContainer.setPadding(new Insets(30));
    formContainer.getStyleClass().add("dashboard-content");

    // Username field
    Label usernameLabel = new Label("Username:");
    usernameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

    usernameField = new TextField();
    usernameField.setPromptText("Choose a username");
    usernameField.setPrefHeight(45);
    usernameField.getStyleClass().add("text-field");

    // Password field
    Label passwordLabel = new Label("Password:");
    passwordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

    passwordField = new PasswordField();
    passwordField.setPromptText("Create a password");
    passwordField.setPrefHeight(45);
    passwordField.getStyleClass().add("text-field");

    // Confirm password field
    Label confirmPasswordLabel = new Label("Confirm Password:");
    confirmPasswordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

    confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm your password");
    confirmPasswordField.setPrefHeight(45);
    confirmPasswordField.getStyleClass().add("text-field");

    // Message label
    messageLabel = new Label();
    messageLabel.setWrapText(true);
    messageLabel.setTextAlignment(TextAlignment.CENTER);
    messageLabel.setVisible(false);

    // Buttons container
    HBox buttonsContainer = new HBox(15);
    buttonsContainer.setAlignment(Pos.CENTER);

    registerButton = new Button("Create Account");
    registerButton.getStyleClass().addAll("btn", "btn-success");
    registerButton.setPrefWidth(140);
    registerButton.setPrefHeight(45);

    backButton = new Button("Back to Login");
    backButton.getStyleClass().addAll("btn", "btn");
    backButton.setPrefWidth(140);
    backButton.setPrefHeight(45);

    buttonsContainer.getChildren().addAll(registerButton, backButton);

    // Add components to form
    formContainer.getChildren().addAll(
        usernameLabel, usernameField,
        passwordLabel, passwordField,
        confirmPasswordLabel, confirmPasswordField,
        messageLabel,
        buttonsContainer);

    // Add everything to main container
    getChildren().addAll(titleLabel, subtitleLabel, formContainer);

    // Set default focus
    usernameField.requestFocus();
  }

  private void setupEventHandlers() {
    // Register button
    registerButton.setOnAction(e -> handleRegister());

    // Back button
    backButton.setOnAction(e -> app.showLoginView());

    // Enter key handlers
    usernameField.setOnAction(e -> passwordField.requestFocus());
    passwordField.setOnAction(e -> confirmPasswordField.requestFocus());
    confirmPasswordField.setOnAction(e -> handleRegister());

    // Real-time validation
    usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });

    passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });

    confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });
  }

  private void handleRegister() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    if (!validateForm()) {
      return;
    }

    if (!password.equals(confirmPassword)) {
      showMessage("Passwords do not match", "error");
      confirmPasswordField.clear();
      confirmPasswordField.requestFocus();
      return;
    }

    if (password.length() < 6) {
      showMessage("Password must be at least 6 characters long", "error");
      passwordField.clear();
      confirmPasswordField.clear();
      passwordField.requestFocus();
      return;
    }

    // Show loading state
    registerButton.setDisable(true);
    registerButton.setText("Creating Account...");
    showMessage("Creating your account...", "info");

    // Perform registration in background thread
    new Thread(() -> {
      boolean success = apiService.register(username, password);

      // Update UI on JavaFX thread
      javafx.application.Platform.runLater(() -> {
        registerButton.setDisable(false);
        registerButton.setText("Create Account");

        if (success) {
          showMessage("Account created successfully! Redirecting to login...", "success");
          // Small delay to show success message
          new Thread(() -> {
            try {
              Thread.sleep(2000);
              javafx.application.Platform.runLater(() -> {
                app.showLoginView();
              });
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
            }
          }).start();
        } else {
          // Check if server is running first
          if (!apiService.isServerRunning()) {
            showMessage("Cannot connect to server. Please make sure the backend is running.", "error");
          } else {
            showMessage("Registration failed. Please check your input and try again.", "error");
          }
          usernameField.requestFocus();
        }
      });
    }).start();
  }

  private boolean validateForm() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    boolean isValid = !username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    registerButton.setDisable(!isValid);

    if (username.isEmpty() && !usernameField.getText().isEmpty()) {
      showMessage("Username is required", "error");
      return false;
    }

    if (password.isEmpty() && !passwordField.getText().isEmpty()) {
      showMessage("Password is required", "error");
      return false;
    }

    if (confirmPassword.isEmpty() && !confirmPasswordField.getText().isEmpty()) {
      showMessage("Please confirm your password", "error");
      return false;
    }

    if (isValid) {
      hideMessage();
    }

    return isValid;
  }

  private void showMessage(String message, String type) {
    messageLabel.setText(message);
    messageLabel.setVisible(true);

    // Remove existing style classes
    messageLabel.getStyleClass().removeAll("error-message", "success-message", "info-message");

    // Add appropriate style class
    switch (type) {
      case "error":
        messageLabel.getStyleClass().add("error-message");
        break;
      case "success":
        messageLabel.getStyleClass().add("success-message");
        break;
      case "info":
        messageLabel.getStyleClass().add("info-message");
        break;
    }
  }

  private void hideMessage() {
    messageLabel.setVisible(false);
  }
}