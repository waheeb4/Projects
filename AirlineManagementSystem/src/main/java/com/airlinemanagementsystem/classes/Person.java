package com.airlinemanagementsystem.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Person implements Info {

    private StringProperty SSN;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty phoneNumber;
    private StringProperty address;
    private IntegerProperty age;

    public Person(String SSN, String firstName, String lastName, String phoneNumber, String address, Integer age) {
        this.SSN = new SimpleStringProperty(SSN);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.age = new SimpleIntegerProperty(age);
    }

    public StringProperty SSNProperty() {
        return SSN;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    @Override
    public String viewInfo() {
        return String.format("Social Security Number: %s\nFull name: %s %s\nPhone Number: %s\nAddress: %s\nAge: %s\n",
                SSNProperty().get(),  firstNameProperty().get(), lastNameProperty().get(), phoneNumberProperty().get(), addressProperty().get(),ageProperty().get());
    }
}
