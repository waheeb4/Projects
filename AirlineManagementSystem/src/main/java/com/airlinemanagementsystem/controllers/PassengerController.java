package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.Passenger;
import com.airlinemanagementsystem.classes.Passport;
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

public class PassengerController implements KeyController {

    @FXML
    private TableView<Passenger> passengersTable;
    @FXML
    private TableColumn<Passenger, String> passengerIDColumn;
    @FXML
    private TableColumn<Passenger, String> firstNameColumn;
    @FXML
    private TableColumn<Passenger, String> lastNameColumn;
    @FXML
    private TableColumn<Passenger, Integer> ageColumn;
    @FXML
    private TableColumn<Passenger, String> phoneNumberColumn;
    @FXML
    private TableColumn<Passenger, String> addressColumn;
    @FXML
    private TableColumn<Passenger, String> ssnColumn;
    @FXML
    private TableColumn<Passenger, String> passportColumn;

    @FXML
    private TextField passengerIDtxt;
    @FXML
    private TextField firstNametxt;
    @FXML
    private TextField lastNametxt;
    @FXML
    private TextField phoneNumbertxt;
    @FXML
    private TextField agetxt;
    @FXML
    private TextField ssntxt;
    @FXML
    private TextField passportNumbertxt;
    @FXML
    private TextField addresstxt;


    private final Connection connection;

    public PassengerController() {
        connection = Database.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        passengerIDColumn.setCellValueFactory(cellData -> cellData.getValue().passengerIDProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty().asObject());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        ssnColumn.setCellValueFactory(cellData -> cellData.getValue().SSNProperty());
        passportColumn.setCellValueFactory(cellData -> cellData.getValue().passportNumberProperty());

        ObservableList<Passenger> passengerData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM passengers";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String passengerID = rs.getString("PassengerID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int age = rs.getInt("Age");
                String phoneNumber = rs.getString("PhoneNumber");
                String address = rs.getString("Address");
                String SSN = rs.getString("SSN");
                String passportNumber = rs.getString("PassportNumber");
                Passenger passenger = new Passenger(SSN, firstName, lastName, phoneNumber, address, age, passengerID, new Passport(passportNumber));
                passengerData.add(passenger);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        passengersTable.setItems(passengerData);
    }

    @FXML
    private void handleAddPassenger() {
        if (passengerIDtxt.getText().isEmpty() || firstNametxt.getText().isEmpty() || lastNametxt.getText().isEmpty() || phoneNumbertxt.getText().isEmpty() || agetxt.getText().isEmpty() || ssntxt.getText().isEmpty() || passportNumbertxt.getText().isEmpty() || addresstxt.getText().isEmpty()) {
            showAlert("Please fill in all passenger info", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "INSERT INTO passengers (PassengerID, FirstName, LastName, Age, PhoneNumber, SSN, PassportNumber, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, passengerIDtxt.getText());
                stmt.setString(2, firstNametxt.getText());
                stmt.setString(3, lastNametxt.getText());
                stmt.setString(4, agetxt.getText());
                stmt.setString(5, phoneNumbertxt.getText());
                stmt.setString(6, ssntxt.getText());
                stmt.setString(7, passportNumbertxt.getText());
                stmt.setString(8, addresstxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Passenger Added Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeletePassenger() {
        if (passengerIDtxt.getText().isEmpty()) {
            showAlert("Please enter a passenger ID to delete", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "DELETE FROM passengers WHERE PassengerID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, passengerIDtxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Passenger Deleted Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Passenger does not exist!", "Failure", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        passengerIDtxt.clear();
        firstNametxt.clear();
        lastNametxt.clear();
        phoneNumbertxt.clear();
        agetxt.clear();
        ssntxt.clear();
        passportNumbertxt.clear();
        addresstxt.clear();  // Clear address
    }

    @FXML
    private void handleBack() {
        try {
            sceneLoader("/main.fxml", passengersTable, "Management Panel");
        } catch (IOException e) {
            System.out.println("Failed to go back to Main scene!");
        }
    }
    @FXML
    private void handleViewInfo(){
        Passenger selectedPassenger = passengersTable.getSelectionModel().getSelectedItem();
        if (selectedPassenger != null) {
            showAlert(selectedPassenger.viewInfo(),"Passenger Information", Alert.AlertType.INFORMATION);
        } else {
            showAlert("No passenger selected. Please select a passenger from the table.", "Error", Alert.AlertType.WARNING);
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
