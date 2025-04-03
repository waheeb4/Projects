package com.airlinemanagementsystem.classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Tickets {

    private StringProperty ticketID;
    private StringProperty passengerID;
    private StringProperty flightCode;
    private  StringProperty pilotID;
    private StringProperty attendantID;
    private IntegerProperty Amount;

    public Tickets(String ticketID, String passengerID, String flightCode, String pilotID, String attendantID , Integer Amount) {
        this.ticketID = new SimpleStringProperty(ticketID);
        this.passengerID = new SimpleStringProperty(passengerID);
        this.flightCode = new SimpleStringProperty(flightCode);
        this.pilotID = new SimpleStringProperty(pilotID);
        this.attendantID = new SimpleStringProperty(attendantID);
        this.Amount = new SimpleIntegerProperty(Amount);
    }
    public StringProperty ticketIDProperty() {
        return ticketID;
    }
    public StringProperty passengerIDProperty() {
        return passengerID;
    }
    public StringProperty flightCodeProperty() {
        return flightCode;
    }

    public StringProperty pilotIDProperty() {
        return pilotID;
    }
    public StringProperty attendantIDProperty() {
        return attendantID;
    }
    public IntegerProperty AmountProperty(){
        return Amount;
    }
}
