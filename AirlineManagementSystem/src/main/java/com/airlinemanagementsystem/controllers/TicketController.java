package com.airlinemanagementsystem.controllers;

import com.airlinemanagementsystem.classes.ComboBoxFactory;
import com.airlinemanagementsystem.classes.Database;
import com.airlinemanagementsystem.classes.Tickets;
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

public class TicketController {

    @FXML
    private ComboBox<String> flightcodeCombobox;
    @FXML
    private ComboBox<String> attendantIDCombobox;
    @FXML
    private ComboBox<String> pilotIDCombobox;
    @FXML
    private ComboBox<String> passengerCombobox;

    final private ComboBoxFactory comboBoxFactory;
    @FXML
    private TableView<Tickets> ticketsTable;
    @FXML
    private TableColumn<Tickets, String> ticketCodeColumn;
    @FXML
    private TableColumn<Tickets, String> passengerIDColumn;
    @FXML
    private TableColumn<Tickets, String> flightCodeColumn;
    @FXML
    private TableColumn<Tickets, String> pilotIDColumn;
    @FXML
    private TableColumn<Tickets, String> attendantIDColumn;
    @FXML
    private TableColumn<Tickets, Integer> AmountColumn;

    @FXML
    private TextField ticketCodetext;
    @FXML
    private TextField Amounttxt;

    private final Connection connection;

    public TicketController() {
        connection = Database.getInstance().getConnection();
        this.comboBoxFactory = new ComboBoxFactory();
    }

    @FXML
    public void initialize() {
        flightcodeCombobox.setItems(comboBoxFactory.getFlightCodes());
        attendantIDCombobox.setItems(comboBoxFactory.getAttendantIDs());
        pilotIDCombobox.setItems(comboBoxFactory.getPilotIDs());
        passengerCombobox.setItems(comboBoxFactory.getPassengerIDs());

        ticketCodeColumn.setCellValueFactory(cellData -> cellData.getValue().ticketIDProperty());
        passengerIDColumn.setCellValueFactory(cellData -> cellData.getValue().passengerIDProperty());
        flightCodeColumn.setCellValueFactory(cellData -> cellData.getValue().flightCodeProperty());
        pilotIDColumn.setCellValueFactory(cellData -> cellData.getValue().pilotIDProperty());
        attendantIDColumn.setCellValueFactory(cellData -> cellData.getValue().attendantIDProperty());
        AmountColumn.setCellValueFactory(cellData -> cellData.getValue().AmountProperty().asObject());

        ObservableList<Tickets> ticketData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM tickets";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String ticketID = rs.getString("TicketCode");
                String passengerID = rs.getString("PassengerID");
                String flightCode = rs.getString("FlightCode");
                String pilotID = rs.getString("PilotID");
                String attendantID = rs.getString("AttendantID");
                int amount = rs.getInt("Amount");
                ticketData.add(new Tickets(ticketID, passengerID, flightCode, pilotID, attendantID, amount));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        ticketsTable.setItems(ticketData);
    }

    @FXML
    private void handleRecord() {
        if (ticketCodetext.getText().isEmpty() || passengerCombobox.getValue() == null ||
                attendantIDCombobox.getValue() == null || flightcodeCombobox.getValue() == null ||
                pilotIDCombobox.getValue() == null || Amounttxt.getText().isEmpty()) {
            showAlert("Please fill in all ticket info", "Error!", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "INSERT INTO tickets (TicketCode, PassengerID, FlightCode, PilotID, AttendantID, Amount) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, ticketCodetext.getText());
                stmt.setString(2, passengerCombobox.getValue());
                stmt.setString(3, flightcodeCombobox.getValue());
                stmt.setString(4, pilotIDCombobox.getValue());
                stmt.setString(5, attendantIDCombobox.getValue());
                stmt.setInt(6, Integer.parseInt(Amounttxt.getText()));

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Ticket Booked Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (ticketCodetext.getText().isEmpty()) {
            showAlert("Please enter a ticket code to cancel!", "Error", Alert.AlertType.WARNING);
        } else {
            try {
                String query = "DELETE FROM tickets WHERE TicketCode = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, ticketCodetext.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showAlert("Ticket canceled Successfully!", "Success", Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Ticket does not exist!", "Failure", Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClear() {
        ticketCodetext.clear();
        passengerCombobox.setValue(null);
        flightcodeCombobox.setValue(null);
        pilotIDCombobox.setValue(null);
        attendantIDCombobox.setValue(null);
        Amounttxt.clear();
    }

    @FXML
    private void handleBack() {
        try {
            sceneLoader("/main.fxml", ticketCodetext, "Management Panel");
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
