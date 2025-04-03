package com.airlinemanagementsystem.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.sql.Date;

public class Flight {

    private final StringProperty flightCode;
    private final StringProperty source;
    private final StringProperty destination;
    private final ObjectProperty<Date> dateOfDep;
    private final StringProperty seats;

    public Flight(String flightCode, String source, String destination, Date dateOfDep, String seats) {
        this.flightCode = new SimpleStringProperty(flightCode);
        this.source = new SimpleStringProperty(source);
        this.destination = new SimpleStringProperty(destination);
        this.dateOfDep = new SimpleObjectProperty<>(dateOfDep);
        this.seats = new SimpleStringProperty(seats);
    }
    public StringProperty flightCodeProperty() {
        return flightCode;
    }
    public StringProperty sourceProperty() {
        return source;
    }

    public StringProperty destinationProperty() {
        return destination;
    }

    public ObjectProperty<Date> dateOfDepProperty() {
        return dateOfDep;
    }
    public StringProperty seatsProperty() {
        return seats;
    }
}
