package com.test.googlemaps2019v2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String title;
    private String eventAddressDialog;
    private String description;
    private String date;
    private String type;
    private String event_id;



    public Event(String eventAddressDialog, String description,
                 String date, String type) {
        this.eventAddressDialog = eventAddressDialog;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public Event() {

    }

    private Event(Parcel in) {
        eventAddressDialog = in.readString();
        description = in.readString();
        date = in.readString();
        type = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEventAddressDialog() {
        return eventAddressDialog;
    }

    public void setEventAddressDialog(String eventAddressDialog) {
        this.eventAddressDialog = eventAddressDialog;
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
        return  "Address: " + eventAddressDialog  +
                "\nDescription: " + description  +
                "\nDate: " + date +
                "\nType: " + type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(eventAddressDialog);
            dest.writeString(description);
            dest.writeString(date);
            dest.writeString(type);

    }
}
