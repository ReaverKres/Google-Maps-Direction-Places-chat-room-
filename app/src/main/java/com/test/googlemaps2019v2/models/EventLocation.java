package com.test.googlemaps2019v2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class EventLocation implements Parcelable{

    private Event event;
    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;
    private String eventLocation_id;

    public EventLocation(Event event, GeoPoint geo_point, Date timestamp) {
        this.event = event;
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public EventLocation() {

    }

    protected EventLocation(Parcel in) {
        event = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<EventLocation> CREATOR = new Creator<EventLocation>() {
        @Override
        public EventLocation createFromParcel(Parcel in) {
            return new EventLocation(in);
        }

        @Override
        public EventLocation[] newArray(int size) {
            return new EventLocation[size];
        }
    };

    public String getEventLocation_id() {
        return eventLocation_id;
    }

    public void setEventLocation_id(String eventLocation_id) {
        this.eventLocation_id = eventLocation_id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return  "EventLocation{" +
                "event=" + event +
                ", geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(event, flags);
    }
}
