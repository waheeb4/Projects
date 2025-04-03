package com.airlinemanagementsystem.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Passenger extends Person {

    private StringProperty passengerID;
    private Passport passport;

    public Passenger(String SSN, String firstName, String lastName, String phoneNumber, String address, Integer age, String passengerID, Passport passport) {
        super(SSN, firstName, lastName, phoneNumber, address, age);
        this.passengerID = new SimpleStringProperty(passengerID);
        this.passport = passport;
    }

    public StringProperty passengerIDProperty() {
        return passengerID;
    }

    public StringProperty passportNumberProperty() {
        return passport.passportNumberProperty();
    }

    @Override
    public String viewInfo() {
        return String.format("\nPassenger Info:\n%sPassenger ID: %s\nPassport Number: %s", super.viewInfo(),passengerIDProperty().get(), passportNumberProperty().get());
    }
}
