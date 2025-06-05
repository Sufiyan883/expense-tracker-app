/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package javafxapplication2;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

// Expense class to represent an expense (Encapsulation)
class Expense {
    private String description;
    private double amount;
    private String category;
    private LocalDate date;

    public Expense(String description, double amount, String category, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f - %s - %s", description, amount, category, date);
    }
}

// ExpenseManager class to manage expenses (Abstraction and Encapsulation)
class ExpenseManager {
    private List<Expense> expenses;

    public ExpenseManager() {
        expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}

// Main application class (Inheritance from Application)
public class JavaFXApplication2 extends Application {
    private static final Logger LOGGER = Logger.getLogger(JavaFXApplication2.class.getName());
    private ExpenseManager expenseManager = new ExpenseManager();
    private ObservableList<Expense> expenseObservableList = FXCollections.observableArrayList();
    private Label totalLabel; // Instance variable to hold totalLabel reference

    @Override
    public void start(Stage primaryStage) {
        try {
            // Main layout
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #f0f4f8;");

            // Header
            Label headerLabel = new Label("Expense Tracker");
            headerLabel.setFont(new Font("Arial", 28));
            headerLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
            HBox headerBox = new HBox(headerLabel);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.setPadding(new Insets(20));
            root.setTop(headerBox);

            // Input form
            VBox inputForm = new VBox(10);
            inputForm.setPadding(new Insets(20));
            inputForm.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

            TextField descriptionField = new TextField();
            descriptionField.setPromptText("Enter description");
            descriptionField.setStyle("-fx-background-radius: 5;");

            TextField amountField = new TextField();
            amountField.setPromptText("Enter amount");
            amountField.setStyle("-fx-background-radius: 5;");

            ComboBox<String> categoryCombo = new ComboBox<>();
            categoryCombo.getItems().addAll("Food", "Transport", "Utilities", "Entertainment", "Other");
            categoryCombo.setPromptText("Select category");
            categoryCombo.setStyle("-fx-background-radius: 5;");

            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now());
            datePicker.setStyle("-fx-background-radius: 5;");

            Button addButton = new Button("Add Expense");
            addButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
            addButton.setOnAction(e -> {
                try {
                    String description = descriptionField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    String category = categoryCombo.getValue();
                    LocalDate date = datePicker.getValue();

                    if (description.isEmpty() || category == null) {
                        showAlert("Error", "Please fill in all fields.");
                        return;
                    }

                    Expense expense = new Expense(description, amount, category, date);
                    expenseManager.addExpense(expense);
                    expenseObservableList.add(expense);
                    updateTotalLabel();
                    clearInputs(descriptionField, amountField, categoryCombo, datePicker);
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Please enter a valid amount.");
                    LOGGER.log(Level.WARNING, "Invalid amount entered", ex);
                }
            });

            // Create and style labels
            Label descriptionLabel = new Label("Description:");
            descriptionLabel.setStyle("-fx-font-weight: bold;");

            Label amountLabel = new Label("Amount:");
            amountLabel.setStyle("-fx-font-weight: bold;");

            Label categoryLabel = new Label("Category:");
            categoryLabel.setStyle("-fx-font-weight: bold;");

            Label dateLabel = new Label("Date:");
            dateLabel.setStyle("-fx-font-weight: bold;");

            // Add all nodes to inputForm
            inputForm.getChildren().addAll(
                descriptionLabel,
                descriptionField,
                amountLabel,
                amountField,
                categoryLabel,
                categoryCombo,
                dateLabel,
                datePicker,
                addButton
            );

            // Expense list
            ListView<Expense> expenseListView = new ListView<>(expenseObservableList);
            expenseListView.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

            Button deleteButton = new Button("Delete Selected");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
            deleteButton.setOnAction(e -> {
                Expense selected = expenseListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    expenseManager.removeExpense(selected);
                    expenseObservableList.remove(selected);
                    updateTotalLabel();
                } else {
                    showAlert("Error", "Please select an expense to delete.");
                }
            });

            VBox listBox = new VBox(10, expenseListView, deleteButton);
            listBox.setPadding(new Insets(20));

            // Total expenses label
            totalLabel = new Label("Total Expenses: $0.00"); // Initialize instance variable
            totalLabel.setFont(new Font("Arial", 18));
            totalLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
            HBox totalBox = new HBox(totalLabel);
            totalBox.setAlignment(Pos.CENTER);
            totalBox.setPadding(new Insets(20));

            // Main content layout
            VBox mainContent = new VBox(20, inputForm, listBox, totalBox);
            mainContent.setPadding(new Insets(20));
            root.setCenter(mainContent);

            // Scene setup
            Scene scene = new Scene(root, 600, 700);
            primaryStage.setTitle("Expense Tracker");
            primaryStage.setScene(scene);
            primaryStage.show();

            LOGGER.info("Application started successfully");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error starting application", ex);
            showAlert("Fatal Error", "Failed to start the application: " + ex.getMessage());
        }
    }

    private void updateTotalLabel() {
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total Expenses: $%.2f", expenseManager.getTotalExpenses()));
            LOGGER.info("Total label updated: " + totalLabel.getText());
        } else {
            LOGGER.warning("totalLabel is null, cannot update");
            showAlert("Error", "Cannot update total due to initialization error.");
        }
    }

    private void clearInputs(TextField description, TextField amount, ComboBox<String> category, DatePicker date) {
        description.clear();
        amount.clear();
        category.setValue(null);
        date.setValue(LocalDate.now());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        LOGGER.info("Alert shown: " + title + " - " + message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
