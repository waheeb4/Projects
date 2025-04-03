package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.Flight;
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

public class FlightsController implements KeyController {

    @FXML
    private TableView<Flight> flightsTable;
    @FXML
    private TableColumn<Flight, String> flightCodeColumn;
    @FXML
    private TableColumn<Flight, String> sourceColumn;
    @FXML
    private TableColumn<Flight, String> destinationColumn;
    @FXML
    private TableColumn<Flight, Date> dateOfDepColumn;
    @FXML
    private TableColumn<Flight, String> seatsColumn;
    @FXML
    private TextField flighttxt;
    @FXML
    private TextField seatstxt;
    @FXML
    private ComboBox<String> sourceBox;
    @FXML
    private ComboBox<String> destBox;

    ObservableList<String> capitals = FXCollections.observableArrayList(
            "Abu Dhabi", "Amsterdam", "Ankara", "Athens", "Baku", "Bangkok", "Beijing", "Berlin", "Bern",
            "Bogotá", "Brasília", "Bratislava", "Buenos Aires", "Cairo", "Canberra", "Caracas", "Chisinau",
            "Copenhagen", "Dhaka", "Helsinki", "Islamabad", "Istanbul", "Jakarta", "Kiev", "Kigali",
            "Lagos", "Lima", "Lisbon", "London", "Madrid", "Manila", "Mexico City", "Moscow", "Nairobi",
            "New Delhi", "Oslo", "Paris", "Prague", "Riyadh", "Rome", "Seoul", "Santiago", "Sao Paulo",
            "Stockholm", "Tehran", "Tokyo", "Warsaw", "Washington, D.C."
    );

    private void setBoxes(ComboBox<String> sourceBox, ComboBox<String> destBox) {
        this.sourceBox = sourceBox;
        this.destBox = destBox;
        sourceBox.setItems(capitals);
        destBox.setItems(capitals);
    }

    @FXML
    private DatePicker dateTime;

    private final Connection connection;

    public FlightsController() {
        connection = Database.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        setBoxes(sourceBox, destBox);
        flightCodeColumn.setCellValueFactory(cellData -> cellData.getValue().flightCodeProperty());
        sourceColumn.setCellValueFactory(cellData -> cellData.getValue().sourceProperty());
        destinationColumn.setCellValueFactory(cellData -> cellData.getValue().destinationProperty());
        dateOfDepColumn.setCellValueFactory(cellData -> cellData.getValue().dateOfDepProperty());
        seatsColumn.setCellValueFactory(cellData -> cellData.getValue().seatsProperty());
        ObservableList<Flight> flightData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM flights";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String flightCode = rs.getString("FlightCode");
                String source = rs.getString("Source");
                String destination = rs.getString("Destination");
                Date dateOfDep = rs.getDate("DateofDep");
                String seats = rs.getString("Seats");
                flightData.add(new Flight(flightCode, source, destination, dateOfDep, seats));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        flightsTable.setItems(flightData);
    }

    @FXML
    private void handleRecord() {
        if (flighttxt.getText().isEmpty() || seatstxt.getText().isEmpty() || sourceBox.getValue() == null || destBox.getValue() == null) {
            showAlert("Please fill in all Flight info", "Error!", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "INSERT INTO flights (FlightCode, Source, Destination, DateofDep, Seats) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, flighttxt.getText());
                stmt.setString(2, sourceBox.getValue());
                stmt.setString(3, destBox.getValue());
                stmt.setDate(4, Date.valueOf(dateTime.getValue()));
                stmt.setString(5, seatstxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Flight Added Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (flighttxt.getText().isEmpty()) {
            showAlert("Please enter a flight code to delete!", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "DELETE FROM flights WHERE FlightCode = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, flighttxt.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Flight Deleted Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Flight does not exist!", "Failure", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        flighttxt.clear();
        seatstxt.clear();
        sourceBox.setValue(null);
        destBox.setValue(null);
        dateTime.setValue(null);
    }

    @FXML
    private void handleBack() {
        try {
            sceneLoader("/main.fxml", destBox, "Management Panel");
        } catch (IOException e) {
            System.out.println("Failed to go back to Main scene!");
        }
    }

    public void sceneLoader(String fxmlpath, Node node, String title) throws IOException {
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
