package com.test.googlemaps2019v2.models;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class EventClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private Event event;


    public EventClusterMarker(LatLng position) {
        this.position = position;
    }

    public EventClusterMarker(LatLng position, String title, String snippet, int iconPicture, Event event) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.event = event;
    }

    public EventClusterMarker(LatLng position, String title, String snippet) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }
}
