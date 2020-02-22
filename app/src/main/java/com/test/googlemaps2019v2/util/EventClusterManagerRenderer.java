package com.test.googlemaps2019v2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.test.googlemaps2019v2.R;
import com.test.googlemaps2019v2.models.EventClusterMarker;


public class EventClusterManagerRenderer extends DefaultClusterRenderer<EventClusterMarker> {
    private int markerDimension = 100;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    public EventClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<EventClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(markerDimension, markerDimension));
        iconGenerator.setContentView(markerImageView);  // 4
    }
    @Override
    protected void onBeforeClusterItemRendered(EventClusterMarker item, MarkerOptions markerOptions) { // 5
        markerImageView.setImageResource(R.drawable.events_icon);  // 6
        Bitmap icon = iconGenerator.makeIcon();  // 7
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));  // 8
        markerOptions.title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }

    public void setUpdateMarker(EventClusterMarker eventClusterMarker) {
        Marker marker = getMarker(eventClusterMarker);
        if (marker != null) {
            marker.setPosition(eventClusterMarker.getPosition());
        }
    }

    public int getMarkerDimension() {
        return markerDimension;
    }

    public void setMarkerDimension(int markerDimension) {
        this.markerDimension = markerDimension;
    }
}

