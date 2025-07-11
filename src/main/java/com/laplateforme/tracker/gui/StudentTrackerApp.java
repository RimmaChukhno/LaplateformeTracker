package com.laplateforme.tracker.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StudentTrackerApp extends Application {

  private Stage primaryStage;
  private LoginView loginView;
  private RegisterView registerView;
  private DashboardView dashboardView;

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    // Configure primary stage
    primaryStage.setTitle("Student Tracker - La Plateforme");
    primaryStage.setMinWidth(1200);
    primaryStage.setMinHeight(800);
    primaryStage.initStyle(StageStyle.DECORATED);

    // Initialize views
    loginView = new LoginView(this);
    registerView = new RegisterView(this);
    dashboardView = new DashboardView(this);

    // Start with login view
    showLoginView();

    primaryStage.show();
  }

  public void showLoginView() {
    Scene scene = new Scene(loginView, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  public void showRegisterView() {
    Scene scene = new Scene(registerView, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  public void showDashboardView() {
    Scene scene = new Scene(dashboardView, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
    primaryStage.setScene(scene);
    dashboardView.loadData(); // Load data when dashboard is shown
  }

  public static void main(String[] args) {
    launch(args);
  }
}