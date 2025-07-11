package com.laplateforme.tracker.gui;

import com.laplateforme.tracker.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsView extends VBox {
  private final ApiService apiService;
  private final Stage stage;

  private Label totalStudentsLabel;
  private Label averageGradeLabel;
  private BarChart<String, Number> gradeChart;

  public AnalyticsView(Stage parentStage) {
    this.apiService = new ApiService();
    this.stage = new Stage();

    setupUI();
    setupStage();
    loadData();
  }

  private void setupUI() {
    setAlignment(Pos.TOP_CENTER);
    setSpacing(20);
    setPadding(new Insets(30));
    getStyleClass().add("analytics-container");
    setPrefWidth(800);
    setPrefHeight(600);

    // Title
    Label titleLabel = new Label("Student Analytics Dashboard");
    titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
    titleLabel.getStyleClass().add("analytics-title");

    // Statistics cards
    HBox statsContainer = new HBox(20);
    statsContainer.setAlignment(Pos.CENTER);

    // Total students card
    VBox totalCard = createStatCard("Total Students", "0");
    totalStudentsLabel = (Label) totalCard.getChildren().get(1);

    // Average grade card
    VBox avgCard = createStatCard("Average Grade", "0.0");
    averageGradeLabel = (Label) avgCard.getChildren().get(1);

    statsContainer.getChildren().addAll(totalCard, avgCard);

    // Chart
    Label chartTitle = new Label("Grade Distribution");
    chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

    setupChart();

    // Add all components
    getChildren().addAll(titleLabel, statsContainer, chartTitle, gradeChart);
  }

  private VBox createStatCard(String label, String value) {
    VBox card = new VBox(10);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(20));
    card.setPrefWidth(200);
    card.setPrefHeight(120);
    card.getStyleClass().add("stat-card");

    Label labelNode = new Label(label);
    labelNode.getStyleClass().add("stat-label");
    labelNode.setFont(Font.font("Segoe UI", 14));

    Label valueNode = new Label(value);
    valueNode.getStyleClass().add("stat-value");
    valueNode.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

    card.getChildren().addAll(labelNode, valueNode);
    return card;
  }

  private void setupChart() {
    CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Grade Ranges");

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Number of Students");

    gradeChart = new BarChart<>(xAxis, yAxis);
    gradeChart.setTitle("Student Grade Distribution");
    gradeChart.setPrefHeight(300);
    gradeChart.setLegendVisible(false);
    gradeChart.getStyleClass().add("chart");

    // Set chart data
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.getData().add(new XYChart.Data<>("0-5", 0));
    series.getData().add(new XYChart.Data<>("6-10", 0));
    series.getData().add(new XYChart.Data<>("11-15", 0));
    series.getData().add(new XYChart.Data<>("16-20", 0));

    gradeChart.getData().add(series);
  }

  private void setupStage() {
    stage.setTitle("Student Analytics");
    stage.setResizable(false);
    stage.initOwner(null); // Make it independent
  }

  public void show() {
    if (!stage.isShowing()) {
      stage.show();
    }
    stage.requestFocus();
  }

  private void loadData() {
    // Load data in background thread
    new Thread(() -> {
      try {
        // Get statistics
        Map<String, Object> stats = apiService.getStatistics();

        // Get all students for grade distribution
        ObservableList<Student> students = apiService.getAllStudents();

        // Calculate grade distribution
        Map<String, Integer> gradeDistribution = calculateGradeDistribution(students);

        // Update UI on JavaFX thread
        javafx.application.Platform.runLater(() -> {
          updateStatistics(stats);
          updateChart(gradeDistribution);
        });

      } catch (Exception e) {
        javafx.application.Platform.runLater(() -> {
          showError("Failed to load analytics data");
        });
      }
    }).start();
  }

  private Map<String, Integer> calculateGradeDistribution(ObservableList<Student> students) {
    Map<String, Integer> distribution = new HashMap<>();
    distribution.put("0-5", 0);
    distribution.put("6-10", 0);
    distribution.put("11-15", 0);
    distribution.put("16-20", 0);

    for (Student student : students) {
      double grade = student.getGrade();
      if (grade >= 0 && grade <= 5) {
        distribution.put("0-5", distribution.get("0-5") + 1);
      } else if (grade >= 6 && grade <= 10) {
        distribution.put("6-10", distribution.get("6-10") + 1);
      } else if (grade >= 11 && grade <= 15) {
        distribution.put("11-15", distribution.get("11-15") + 1);
      } else if (grade >= 16 && grade <= 20) {
        distribution.put("16-20", distribution.get("16-20") + 1);
      }
    }

    return distribution;
  }

  private void updateStatistics(Map<String, Object> stats) {
    int totalStudents = (Integer) stats.get("totalStudents");
    double averageGrade = (Double) stats.get("averageGrade");

    totalStudentsLabel.setText(String.valueOf(totalStudents));
    averageGradeLabel.setText(String.format("%.2f", averageGrade));
  }

  private void updateChart(Map<String, Integer> distribution) {
    XYChart.Series<String, Number> series = gradeChart.getData().get(0);
    series.getData().clear();

    series.getData().add(new XYChart.Data<>("0-5", distribution.get("0-5")));
    series.getData().add(new XYChart.Data<>("6-10", distribution.get("6-10")));
    series.getData().add(new XYChart.Data<>("11-15", distribution.get("11-15")));
    series.getData().add(new XYChart.Data<>("16-20", distribution.get("16-20")));
  }

  private void showError(String message) {
    Label errorLabel = new Label(message);
    errorLabel.getStyleClass().add("error-message");
    errorLabel.setAlignment(Pos.CENTER);

    // Replace content with error message
    getChildren().clear();
    getChildren().add(errorLabel);
  }
}