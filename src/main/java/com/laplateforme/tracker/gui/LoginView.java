package com.laplateforme.tracker.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class LoginView extends VBox {
  private final StudentTrackerApp app;
  private final ApiService apiService;

  private TextField usernameField;
  private PasswordField passwordField;
  private Label messageLabel;
  private Button loginButton;
  private Button registerButton;

  public LoginView(StudentTrackerApp app) {
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
    Label titleLabel = new Label("Student Tracker");
    titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
    titleLabel.getStyleClass().add("auth-title");
    titleLabel.setTextAlignment(TextAlignment.CENTER);

    // Subtitle
    Label subtitleLabel = new Label("Welcome back! Please sign in to your account.");
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
    usernameField.setPromptText("Enter your username");
    usernameField.setPrefHeight(45);
    usernameField.getStyleClass().add("text-field");

    // Password field
    Label passwordLabel = new Label("Password:");
    passwordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

    passwordField = new PasswordField();
    passwordField.setPromptText("Enter your password");
    passwordField.setPrefHeight(45);
    passwordField.getStyleClass().add("text-field");

    // Message label
    messageLabel = new Label();
    messageLabel.setWrapText(true);
    messageLabel.setTextAlignment(TextAlignment.CENTER);
    messageLabel.setVisible(false);

    // Buttons container
    HBox buttonsContainer = new HBox(15);
    buttonsContainer.setAlignment(Pos.CENTER);

    loginButton = new Button("Sign In");
    loginButton.getStyleClass().addAll("btn", "btn-primary");
    loginButton.setPrefWidth(120);
    loginButton.setPrefHeight(45);

    registerButton = new Button("Register");
    registerButton.getStyleClass().addAll("btn", "btn");
    registerButton.setPrefWidth(120);
    registerButton.setPrefHeight(45);

    buttonsContainer.getChildren().addAll(loginButton, registerButton);

    // Add components to form
    formContainer.getChildren().addAll(
        usernameLabel, usernameField,
        passwordLabel, passwordField,
        messageLabel,
        buttonsContainer);

    // Add everything to main container
    getChildren().addAll(titleLabel, subtitleLabel, formContainer);

    // Set default focus
    usernameField.requestFocus();
  }

  private void setupEventHandlers() {
    // Login button
    loginButton.setOnAction(e -> handleLogin());

    // Register button
    registerButton.setOnAction(e -> app.showRegisterView());

    // Enter key handlers
    usernameField.setOnAction(e -> passwordField.requestFocus());
    passwordField.setOnAction(e -> handleLogin());

    // Real-time validation
    usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });

    passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
      validateForm();
    });
  }

  private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();

    if (!validateForm()) {
      return;
    }

    // Show loading state
    loginButton.setDisable(true);
    loginButton.setText("Signing In...");
    showMessage("Connecting to server...", "info");

    // Perform login in background thread
    new Thread(() -> {
      boolean success = apiService.login(username, password);

      // Update UI on JavaFX thread
      javafx.application.Platform.runLater(() -> {
        loginButton.setDisable(false);
        loginButton.setText("Sign In");

        if (success) {
          showMessage("Login successful! Redirecting...", "success");
          // Small delay to show success message
          new Thread(() -> {
            try {
              Thread.sleep(1000);
              javafx.application.Platform.runLater(() -> {
                app.showDashboardView();
              });
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
            }
          }).start();
        } else {
          showMessage("Invalid username or password. Please try again.", "error");
          passwordField.clear();
          passwordField.requestFocus();
        }
      });
    }).start();
  }

  private boolean validateForm() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();

    boolean isValid = !username.isEmpty() && !password.isEmpty();
    loginButton.setDisable(!isValid);

    if (username.isEmpty() && !usernameField.getText().isEmpty()) {
      showMessage("Username is required", "error");
      return false;
    }

    if (password.isEmpty() && !passwordField.getText().isEmpty()) {
      showMessage("Password is required", "error");
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