package com.airlinemanagementsystem.classes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ComboBoxFactory {

    private final Connection connection;

    public ComboBoxFactory() {
        this.connection = Database.getInstance().getConnection();
    }

    public ObservableList<String> getFlightCodes() {
        ObservableList<String> flightCodes = FXCollections.observableArrayList();
        try {
            String query = "SELECT FlightCode FROM flights";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                flightCodes.add(rs.getString("FlightCode"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching flight codes: " + e.getMessage());
        }
        return flightCodes;
    }

    public ObservableList<String> getAttendantIDs() {
        ObservableList<String> attendantIDs = FXCollections.observableArrayList();
        try {
            String query = "SELECT AttendantID FROM attendants";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                attendantIDs.add(rs.getString("AttendantID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching attendant IDs: " + e.getMessage());
        }
        return attendantIDs;
    }

    public ObservableList<String> getPilotIDs() {
        ObservableList<String> pilotIDs = FXCollections.observableArrayList();
        try  {
            String query = "SELECT PilotID FROM pilots";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                pilotIDs.add(rs.getString("PilotID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pilot IDs: " + e.getMessage());
        }
        return pilotIDs;
    }

    public ObservableList<String> getPassengerIDs() {
        ObservableList<String> passengerIDs = FXCollections.observableArrayList();
        try {
            String query = "SELECT PassengerID FROM passengers";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                passengerIDs.add(rs.getString("PassengerID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching passenger IDs: " + e.getMessage());
        }
        return passengerIDs;
    }
}
