package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.Pilot;
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

public class CaptainController implements KeyController {

    @FXML
    private TableView<Pilot> captainsTable;
    @FXML
    private TableColumn<Pilot, String> pilotIDColumn;
    @FXML
    private TableColumn<Pilot, String> firstNameColumn;
    @FXML
    private TableColumn<Pilot, String> lastNameColumn;
    @FXML
    private TableColumn<Pilot, Integer> ageColumn;
    @FXML
    private TableColumn<Pilot, String> phoneNumberColumn;
    @FXML
    private TableColumn<Pilot, String> addressColumn;
    @FXML
    private TableColumn<Pilot, String> ssnColumn;
    @FXML
    private TableColumn<Pilot, Integer> flightCreditColumn;

    @FXML
    private TextField pilotIDtxt;
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
    @FXML
    private TextField flightCredittxt;

    private final Connection connection;

    public CaptainController() {
        connection = Database.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        pilotIDColumn.setCellValueFactory(cellData -> cellData.getValue().pilotIDProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty().asObject());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        ssnColumn.setCellValueFactory(cellData -> cellData.getValue().SSNProperty());
        flightCreditColumn.setCellValueFactory(cellData -> cellData.getValue().flightCreditProperty().asObject());

        ObservableList<Pilot> captainData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM pilots";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String pilotID = rs.getString("PilotID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int age = rs.getInt("Age");
                String phoneNumber = rs.getString("PhoneNumber");
                String address = rs.getString("Address");
                String SSN = rs.getString("SSN");
                int flightCredit = rs.getInt("FlightCredit");
                Pilot pilot = new Pilot(pilotID, flightCredit, SSN, firstName, lastName, phoneNumber, address, age);
                captainData.add(pilot);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        captainsTable.setItems(captainData);
    }

    @FXML
    private void handleAddCaptain() {
        if (pilotIDtxt.getText().isEmpty() || firstNametxt.getText().isEmpty() || lastNametxt.getText().isEmpty() ||
                phoneNumbertxt.getText().isEmpty() || agetxt.getText().isEmpty() || ssntxt.getText().isEmpty() ||
                flightCredittxt.getText().isEmpty() || addresstxt.getText().isEmpty()) {
            showAlert("Please fill in all captain information", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "INSERT INTO pilots (PilotID, FirstName, LastName, Age, PhoneNumber, SSN, FlightCredit, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, pilotIDtxt.getText());
                stmt.setString(2, firstNametxt.getText());
                stmt.setString(3, lastNametxt.getText());
                stmt.setInt(4, Integer.parseInt(agetxt.getText()));
                stmt.setString(5, phoneNumbertxt.getText());
                stmt.setString(6, ssntxt.getText());
                stmt.setInt(7, Integer.parseInt(flightCredittxt.getText()));
                stmt.setString(8, addresstxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Captain joined us successfully!", "Success", Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Captain does not exist!", "Failure", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeleteCaptain() {
        if (pilotIDtxt.getText().isEmpty()) {
            showAlert("Please enter a pilot ID to fire", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "DELETE FROM pilots WHERE PilotID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, pilotIDtxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Pilot fired!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        pilotIDtxt.clear();
        firstNametxt.clear();
        lastNametxt.clear();
        agetxt.clear();
        phoneNumbertxt.clear();
        addresstxt.clear();
        ssntxt.clear();
        flightCredittxt.clear();
    }

    @FXML
    private void handleBack() {
        try {
            sceneLoader("/main.fxml", captainsTable, "Management Panel");
        } catch (IOException e) {
            System.out.println("Failed to go back to Main scene!");
        }
    }

    @FXML
    private void handleViewInfo() {
        Pilot selectedCaptain = captainsTable.getSelectionModel().getSelectedItem();
        if (selectedCaptain != null) {
            showAlert(selectedCaptain.viewInfo(), "Captain Information", Alert.AlertType.INFORMATION);
        } else {
            showAlert("No captain selected. Please select a captain from the table.", "Error", Alert.AlertType.WARNING);
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
