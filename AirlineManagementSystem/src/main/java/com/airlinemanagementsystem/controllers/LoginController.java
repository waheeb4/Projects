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
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController implements KeyController {
    @FXML
    private TextField usertxt;
    @FXML
    private PasswordField passtxt;

    private  final  Connection connection;

    public LoginController() {
        connection = Database.getInstance().getConnection();
    }
    @FXML
    private void handleLogin() {
        if (usertxt.getText().isEmpty() || passtxt.getText().isEmpty()) {
            showAlert("Please enter a username and a password!", "Error!", Alert.AlertType.WARNING);
        } else {
            if (validateLogin()) {
                showAlert("Login Successful!", "Valid", Alert.AlertType.INFORMATION);
                try {
                    sceneLoader("/main.fxml",usertxt,"Management Panel");
                } catch (IOException e) {
                    System.out.println("Failed to load the Main scene!");
                }
            } else {
                showAlert("Invalid Credentials!", "Invalid", Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void handleClear() {
        usertxt.clear();
        passtxt.clear();
    }
    @FXML
    private void handleExit() {
        Stage stage = (Stage) usertxt.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleNewUser() {
        try {
            sceneLoader("/newuser.fxml",passtxt,"Registration");
        } catch (IOException e) {
           System.out.println("Failed to load the New User scene!");
        }
    }
    private boolean validateLogin() {
        try {
            String query = "SELECT * FROM login WHERE Username = ? AND Password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usertxt.getText());
            preparedStatement.setString(2, passtxt.getText());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            return false;
        }
    }


    public void sceneLoader(String fxmlpath, Node node,String title)  throws IOException {
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
