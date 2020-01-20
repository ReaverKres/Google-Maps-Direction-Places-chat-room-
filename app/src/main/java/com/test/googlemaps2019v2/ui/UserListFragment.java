package com.test.googlemaps2019v2.ui;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
<<<<<<< HEAD
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
=======
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
>>>>>>> Migrate to Android X and AutoCompleteTV
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.GeolocationPermissions;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
=======
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
>>>>>>> Migrate to Android X and AutoCompleteTV
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


<<<<<<< HEAD

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.test.googlemaps2019v2.R;

=======
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.test.googlemaps2019v2.R;

import com.test.googlemaps2019v2.adapters.PlaceAutoSuggestAdapter;
>>>>>>> Migrate to Android X and AutoCompleteTV
import com.test.googlemaps2019v2.adapters.UserRecyclerAdapter;
import com.test.googlemaps2019v2.models.ClusterMarker;
import com.test.googlemaps2019v2.models.PolylineData;
import com.test.googlemaps2019v2.models.User;
import com.test.googlemaps2019v2.models.UserLocation;
import com.test.googlemaps2019v2.util.MyClusterManagerRenderer;
import com.test.googlemaps2019v2.util.ViewWeightAnimationWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

<<<<<<< HEAD
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
=======
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
>>>>>>> Migrate to Android X and AutoCompleteTV
import java.util.List;

import static com.test.googlemaps2019v2.Constants.DEFAULT_ZOOM;
import static com.test.googlemaps2019v2.Constants.MAPVIEW_BUNDLE_KEY;

public class UserListFragment extends Fragment implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener,
        UserRecyclerAdapter.UserListRecyclerClickListener
{

    private static final String TAG = "UserListFragment";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;


    //widgets
    private RecyclerView mUserListRecyclerView;
    private MapView mMapView;
    private RelativeLayout mMapContainer;
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;


    //vars
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private UserRecyclerAdapter mUserRecyclerAdapter;
    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition;
    private LatLngBounds mMapBoundary;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private int mMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = true;
  //  private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
<<<<<<< HEAD
=======
    private PlacesClient placesClient;
    private PlaceAutoSuggestAdapter placeAutoSuggestAdapter;
>>>>>>> Migrate to Android X and AutoCompleteTV



    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mUserLocations.size() == 0) { // make sure the list doesn't duplicate by navigating back
            if (getArguments() != null) {
                final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
                mUserList.addAll(users);

                final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
                mUserLocations.addAll(locations);
            }
        }
<<<<<<< HEAD
=======
       // initAutoCompleteFragment();
>>>>>>> Migrate to Android X and AutoCompleteTV
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view);
        mMapView = view.findViewById(R.id.user_list_map);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        mMapContainer = view.findViewById(R.id.map_container);
        mSearchText = view.findViewById(R.id.input_search);
        mGps = view.findViewById(R.id.ic_gps);

        initUserListRecyclerView();
        initGoogleMap(savedInstanceState);

        setUserPosition();
        init();
<<<<<<< HEAD
=======
        //initAutoCompleteFragment();
>>>>>>> Migrate to Android X and AutoCompleteTV
        return view;
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;

    private void startUserLocationsRunnable(){
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }

    private void retrieveUserLocations(){
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users in the chatroom.");

        try{
            for(final ClusterMarker clusterMarker: mClusterMarkers){

                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_user_locations))
                        .document(clusterMarker.getUser().getUser_id());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);

                            // update the location
                            for (int i = 0; i < mClusterMarkers.size(); i++) {
                                try {
                                    if (mClusterMarkers.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {

                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getGeo_point().getLatitude(),
                                                updatedUserLocation.getGeo_point().getLongitude()
                                        );

                                        mClusterMarkers.get(i).setPosition(updatedLatLng);
                                        mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                    }


                                } catch (NullPointerException e) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
            }
        }catch (IllegalStateException e){
            Log.e(TAG, "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
        }

    }

    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    private void addMapMarkers(){

        if(mGoogleMap != null){

            resetMap();

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            mGoogleMap.setOnInfoWindowClickListener(this);

            for(UserLocation userLocation: mUserLocations){

                Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
                try{
                    String snippet = "";
                    if(userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                        snippet = "This is you";
                    }
                    else{
                        snippet = "Determine route to " + userLocation.getUser().getUsername() + "?";
                    }

                    int avatar = R.drawable.cartman_cop; // set the default avatar
                    try{
                        avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
                    }
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();

            setCameraView();
        }
    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
    private void setCameraView() {
        Log.d(TAG, "setCameraView: called");
        // Set a boundary to start
        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - .1;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - .1;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + .1;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    private void setUserPosition() {
        Log.d(TAG, "setUserPosition: called");
        for (UserLocation userLocation : mUserLocations) {  //явдяется ли пользователь аутентифицированным
            if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                mUserPosition = userLocation;
                Log.d(TAG, "setUserPosition: mUserPosition" + mUserPosition.toString());
            }
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
<<<<<<< HEAD
=======

>>>>>>> Migrate to Android X and AutoCompleteTV
    }

    private void initUserListRecyclerView() {
        mUserRecyclerAdapter = new UserRecyclerAdapter(mUserList, this);
        mUserListRecyclerView.setAdapter(mUserRecyclerAdapter);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

<<<<<<< HEAD
    private void init(){        //edit_text
        Log.d(TAG, "init: initialization");

=======
//   private void initAutoCompleteFragment(){
//       if (!Places.isInitialized()){
//           Places.initialize(getActivity().getApplicationContext(),getString(R.string.google_maps_api_key));
//       }
//
//       placesClient = Places.createClient(getActivity().getApplicationContext());
//       final AutocompleteSupportFragment autocompleteSupportFragment =
//               (AutocompleteSupportFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
//        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                final LatLng latLng = place.getLatLng();
//                Log.d(TAG, "initAutoCompleteFragment: onPlaceSelected: \n" + latLng.latitude + " " + latLng.longitude);
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.e(TAG, "initAutoCompleteFragment: onError: \n" + status.getStatus().toString() );
//            }
//        });
//    }

    private void init(){        //AutoCompleteTV
        Log.d(TAG, "init: initialization");

        mSearchText.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

>>>>>>> Migrate to Android X and AutoCompleteTV
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
        //hideSoftKeyboard();
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked Gps icon");
                 // setUserPosition();
                    getDeviceLocation();
            }
        });
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException err:" + e.getMessage() );
        }
        if (list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());
            //Toast.makeText(getActivity(),address.toString(),toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom),600,null);
        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mGoogleMap.addMarker(options);
        }

       // hideSoftKeyboard();
    }

//    private void hideSoftKeyboard(){
//       this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();

        mGoogleMap = map;
        addMapMarkers();
        mGoogleMap.setOnPolylineClickListener(this);

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        stopLocationUpdates(); // stop updating user locations
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

            case R.id.btn_reset_map:{
                addMapMarkers();
                break;
            }
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getTitle().contains("Trip #")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            if(marker.getSnippet().equals("This is you")){
                marker.hideInfoWindow();
            }
            else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                resetSelectedMarker();
                                mSelectedMarker = marker;
                                calculateDirections(marker);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));

                mTripMarkers.add(marker);

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    @Override
    public void onUserClicked(int position) {
        Log.d(TAG, "onUserClicked: selected a user: " + mUserList.get(position).getUser_id());

        String selectedUserId = mUserList.get(position).getUser_id();

        for(ClusterMarker clusterMarker: mClusterMarkers){
            if(selectedUserId.equals(clusterMarker.getUser().getUser_id())){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(clusterMarker.getPosition().latitude, clusterMarker.getPosition().longitude)),
                        600,
                        null
                );
                break;
            }
        }
    }
}



















