package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewUserController implements KeyController{
    @FXML
    private TextField newUtxt;
    @FXML
    private PasswordField newPtxt;
    @FXML
    private PasswordField newCPtxt;


    private final Connection connection;

    public NewUserController() {
        connection = Database.getInstance().getConnection();
    }
    @FXML
    private void handleCreateUser() {
        if ( newUtxt.getText().isEmpty() || newPtxt.getText().isEmpty() || newCPtxt.getText().isEmpty()) {
            showAlert("Please enter all fields!", "Error", Alert.AlertType.WARNING);
        } else if (!newPtxt.getText().equals(newCPtxt.getText())) {
            showAlert("Passwords do not match!", "Error", Alert.AlertType.ERROR);
        } else {
            try (connection) {
                String query = "INSERT INTO login (Username, Password) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newUtxt.getText());
                preparedStatement.setString(2, newPtxt.getText());
                int rows = preparedStatement.executeUpdate();
                if (rows > 0) {
                    showAlert("User Created Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void handleClear() {
        newUtxt.clear();
        newPtxt.clear();
        newCPtxt.clear();
    }
    @FXML
    private void handleBack() {
        try {
            sceneLoader("/login.fxml",newPtxt,"Airline Management System");
        } catch (IOException e) {
            System.out.println("Error going back!");
        }
    }



    public void sceneLoader(String fxmlpath, Node node, String title)  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlpath));
        Parent newUserRoot = loader.load();
        Stage currentStage = (Stage) node.getScene().getWindow();
        Scene newUserScene = new Scene(newUserRoot);
        currentStage.setScene(newUserScene);
        currentStage.setTitle(title);
    }
    public void showAlert(String content, String header, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
