package com.airlinemanagementsystem.classes;

public class Crew extends Person {
    public Crew(String SSN, String firstName, String lastName, String phoneNumber, String address, int age) {
        super(SSN, firstName, lastName, phoneNumber, address, age);
    }
    public String viewInfo(){
        return super.viewInfo();
    }
}

