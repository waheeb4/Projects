package com.airlinemanagementsystem.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Passport {

    private StringProperty passportNumber;

    public Passport(String passportNumber) {
        this.passportNumber = new SimpleStringProperty(passportNumber);
    }

    public StringProperty passportNumberProperty() {
        return passportNumber;
    }

}
