package com.test.googlemaps2019v2.models;

public class Event {

    private String description;
    private String date;
    private String type;

    public Event(String description, String date, String type) {
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public Event() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return  "\nDescription: " + description  +
                "\nDate: " + date +
                "\nType: " + type;
    }
}
