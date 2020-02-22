package com.test.googlemaps2019v2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.test.googlemaps2019v2.R;
import com.test.googlemaps2019v2.models.UserClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


public class UserClusterManagerRenderer extends DefaultClusterRenderer<UserClusterMarker>
{

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private static final int MARKER_DIMENSION = 100;
    //private final int markerWidth;
    //private final int markerHeight;
    private final View clusterItemView;
    private LayoutInflater layoutInflater;

    public UserClusterManagerRenderer(Context context, GoogleMap googleMap,
                                      ClusterManager<UserClusterMarker> clusterManager) {

        super(context, googleMap, clusterManager);

        // initialize cluster item icon generator
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
//        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
//        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
        layoutInflater = LayoutInflater.from(context);
        clusterItemView = layoutInflater.inflate(R.layout.single_cluster_marker_view, null);
    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(UserClusterMarker item, MarkerOptions markerOptions) {

        imageView.setImageResource(item.getIconPicture());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

//    @Override
//    protected void onBeforeClusterRendered(Cluster<UserClusterMarker> cluster, MarkerOptions markerOptions) {
//        TextView singleClusterMarkerSizeTextView = clusterItemView.findViewById(R.id.singleClusterMarkerSizeTextView);
//        singleClusterMarkerSizeTextView.setText(String.valueOf(cluster.getSize()));
//        Bitmap icon = iconGenerator.makeIcon();
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//    }

    @Override
    public void setMinClusterSize(int minClusterSize) {
        super.setMinClusterSize(minClusterSize);
    }



    //    @Override
//    protected boolean shouldRenderAsCluster(Cluster cluster) {
//        return true;
//    }

    /**
     * Update the GPS coordinate of a ClusterItem
     * @param userClusterMarker
     */
    public void setUpdateMarker(UserClusterMarker userClusterMarker) {
        Marker marker = getMarker(userClusterMarker);
        if (marker != null) {
            marker.setPosition(userClusterMarker.getPosition());
        }
    }
}

















