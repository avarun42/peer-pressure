package me.arora.varun.peerpressure;

public class Contact {

    public String name;
    public String phone_number;
    public String tier;

    public String toString() {
        return "Name: " + name
                + ", Phone #: " + phone_number
                + ", Tier: " + tier;
    }
}