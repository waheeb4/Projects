package com.airlinemanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class MainController implements KeyController{
    @FXML
    private RadioButton pilotRadioButton;
    @FXML
    private RadioButton attendantRadioButton;

    private final ToggleGroup crewGroup;

    public MainController() {
        crewGroup = new ToggleGroup();
    }
    @FXML
    public void initialize() {
        pilotRadioButton.setToggleGroup(crewGroup);
        attendantRadioButton.setToggleGroup(crewGroup);
    }
    @FXML
    private void handleTicketClick() {
        try {
            sceneLoader("/ticket.fxml",pilotRadioButton,"Ticket Management");
        } catch (IOException e) {
            System.out.println("Error loading Tickets scene!");
        }
    }
    @FXML
    private void handleFlightClick() {
        try {
            sceneLoader("/flight.fxml",attendantRadioButton,"Flight Management");
        } catch (IOException e) {
            System.out.println("Error loading Flights scene!");
        }
    }
    @FXML
    private void handlePassengerClick() {
        try {
            sceneLoader("/passengers.fxml",pilotRadioButton,"Passenger Management");
        } catch (IOException e) {
            System.out.println("Error loading Passengers scene!");
        }
    }

    @FXML
    private void handleCrewClick() {
        if (pilotRadioButton.isSelected()) {
            try {
                sceneLoader("/captain.fxml",pilotRadioButton,"Captain Management");
            } catch (IOException e) {
                System.out.println("Error loading Captains scene!");
            }
        } else if (attendantRadioButton.isSelected()) {
            try {
                sceneLoader("/attendant.fxml",attendantRadioButton,"Attendant Management");
            } catch (IOException e) {
                System.out.println("Error loading Attendant scene!");
            }
        } else {
            showAlert("Select an Option!","Crew Selection",Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void handleExitClick() {
        showAlert();
    }

    public void sceneLoader(String fxmlpath, Node node, String title)  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlpath));
        Parent newUserRoot = loader.load();
        Stage currentStage = (Stage) node.getScene().getWindow();
        Scene newUserScene = new Scene(newUserRoot);
        currentStage.setScene(newUserScene);
        currentStage.setTitle(title);
    }
    public  void showAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOk) {
            System.exit(0);
        }
    }
    public void showAlert(String content, String header, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
