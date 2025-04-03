package com.airlinemanagementsystem.classes;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Attendant extends Crew {
    private final StringProperty attendantID;

    public Attendant(String attendantID, String SSN, String firstName, String lastName, String phoneNumber, String address, int age) {
        super(SSN, firstName, lastName, phoneNumber, address, age);
        this.attendantID = new SimpleStringProperty(attendantID);
    }

    public StringProperty attendantIDProperty() {
        return attendantID;
    }

    @Override
    public String viewInfo() {
        return String.format("\nAttendant Info:%s\nAttendant ID: %s\n", super.viewInfo(), attendantIDProperty().get());
    }
}
