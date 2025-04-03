package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.Attendant;
import com.airlinemanagementsystem.classes.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AttendantController implements KeyController {

    @FXML
    private TableView<Attendant> attendantsTable;
    @FXML
    private TableColumn<Attendant, String> attendantIDColumn;
    @FXML
    private TableColumn<Attendant, String> firstNameColumn;
    @FXML
    private TableColumn<Attendant, String> lastNameColumn;
    @FXML
    private TableColumn<Attendant, Integer> ageColumn;
    @FXML
    private TableColumn<Attendant, String> phoneNumberColumn;
    @FXML
    private TableColumn<Attendant, String> addressColumn;
    @FXML
    private TableColumn<Attendant, String> ssnColumn;

    @FXML
    private TextField attendantIDtxt;
    @FXML
    private TextField firstNametxt;
    @FXML
    private TextField lastNametxt;
    @FXML
    private TextField agetxt;
    @FXML
    private TextField phoneNumbertxt;
    @FXML
    private TextField addresstxt;
    @FXML
    private TextField ssntxt;

    private final Connection connection;

    public AttendantController() {
        connection = Database.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        attendantIDColumn.setCellValueFactory(cellData -> cellData.getValue().attendantIDProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty().asObject());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        ssnColumn.setCellValueFactory(cellData -> cellData.getValue().SSNProperty());

        ObservableList<Attendant> attendantData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM attendants";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String attendantID = rs.getString("AttendantID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int age = rs.getInt("Age");
                String phoneNumber = rs.getString("PhoneNumber");
                String address = rs.getString("Address");
                String SSN = rs.getString("SSN");
                Attendant attendant = new Attendant(attendantID, SSN, firstName, lastName, phoneNumber, address, age);
                attendantData.add(attendant);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        attendantsTable.setItems(attendantData);
    }

    @FXML
    private void handleAddAttendant() {
        if (attendantIDtxt.getText().isEmpty() || firstNametxt.getText().isEmpty() || lastNametxt.getText().isEmpty() ||
                phoneNumbertxt.getText().isEmpty() || agetxt.getText().isEmpty() || ssntxt.getText().isEmpty() ||
                addresstxt.getText().isEmpty()) {
            showAlert("Please fill in all attendant information", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "INSERT INTO attendants (AttendantID, FirstName, LastName, Age, PhoneNumber, SSN, Address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, attendantIDtxt.getText());
                stmt.setString(2, firstNametxt.getText());
                stmt.setString(3, lastNametxt.getText());
                stmt.setInt(4, Integer.parseInt(agetxt.getText()));
                stmt.setString(5, phoneNumbertxt.getText());
                stmt.setString(6, ssntxt.getText());
                stmt.setString(7, addresstxt.getText());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Attendant joined us successfully!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeleteAttendant() {
        if (attendantIDtxt.getText().isEmpty()) {
            showAlert("Please enter an attendant ID to fire", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "DELETE FROM attendants WHERE AttendantID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, attendantIDtxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Attendant fired!", "Success", Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Attendant does not exist!", "Failure", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        attendantIDtxt.clear();
        firstNametxt.clear();
        lastNametxt.clear();
        agetxt.clear();
        phoneNumbertxt.clear();
        addresstxt.clear();
        ssntxt.clear();
    }

    @FXML
    private void handleBack() {
        try {
            sceneLoader("/main.fxml", attendantsTable, "Management Panel");
        } catch (IOException e) {
            System.out.println("Failed to go back to Main scene!");
        }
    }

    @FXML
    private void handleViewInfo() {
        Attendant selectedAttendant = attendantsTable.getSelectionModel().getSelectedItem();
        if (selectedAttendant != null) {
            showAlert(selectedAttendant.viewInfo(), "Attendant Information", Alert.AlertType.INFORMATION);
        } else {
            showAlert("No attendant selected. Please select an attendant from the table.", "Error", Alert.AlertType.WARNING);
        }
    }

    public void sceneLoader(String fxmlpath, Node node, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlpath));
        Parent newRoot = loader.load();
        Stage currentStage = (Stage) node.getScene().getWindow();
        Scene newScene = new Scene(newRoot);
        currentStage.setScene(newScene);
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
