package com.example.updatebiometricapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UpdateBiometricApp extends Application {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lamisplus?preparedStatementCacheQueries=0";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "R1se@jhp321@*";

    private TextField uuidTextField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Update Biometric App Password ");

        Label uuidLabel = new Label("Enter UUID:");
        uuidTextField = new TextField();
        Button updateButton = new Button("Update Biometric");

        updateButton.setOnAction(e -> handleUpdateButtonAction());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(uuidLabel, uuidTextField, updateButton);

        Scene scene = new Scene(vbox, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleUpdateButtonAction() {
        String uuidString = uuidTextField.getText().trim();
        if (!uuidString.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                updateBiometric(uuid);
                showMessage(uuid);
            } catch (IllegalArgumentException | SQLException ex) {
                showErrorMessage("Error updating biometric: " + ex.getMessage());
            }
        } else {
            showErrorMessage("Please enter a valid UUID");
        }
    }

    private void updateBiometric(UUID personUuid) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE biometric SET ARCHIVED = 0, Recapture = CASE WHEN NOT EXISTS (SELECT 1 FROM biometric WHERE person_uuid = ? AND Recapture = 0)THEN 0 ELSE Recapture END WHERE PERSON_UUID = ? ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, personUuid.toString());
                preparedStatement.setString(2,personUuid.toString());
                preparedStatement.executeUpdate();
            }
        }
    }

    private void showMessage(UUID uuid) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Biometric Status");
        alert.setHeaderText(null);
        alert.setContentText("Biometric updated successfully for UUID: " + uuid);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        System.out.println(message);
    }
}
