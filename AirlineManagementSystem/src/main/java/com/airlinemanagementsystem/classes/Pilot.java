package com.airlinemanagementsystem.classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pilot extends Crew {
    private StringProperty pilotID;
    private IntegerProperty flightCredit;

    public Pilot(String pilotID, int flightCredit, String SSN, String firstName, String lastName, String phoneNumber, String address, int age) {
        super(SSN, firstName, lastName, phoneNumber, address, age);
        this.pilotID = new SimpleStringProperty(pilotID);
        this.flightCredit = new SimpleIntegerProperty(flightCredit);
    }

    // Property methods
    public StringProperty pilotIDProperty() {
        return pilotID;
    }

    public IntegerProperty flightCreditProperty() {
        return flightCredit;
    }

    @Override
    public String viewInfo() {
        return String.format("\nPilot Info:\n%sPilot ID: %s\nFlight Credit: %d\n",
                super.viewInfo(), pilotIDProperty().get(), flightCreditProperty().get());
    }
}
