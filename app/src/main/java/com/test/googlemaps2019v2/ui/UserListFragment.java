package com.test.googlemaps2019v2.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.test.googlemaps2019v2.R;


import com.test.googlemaps2019v2.adapters.CustomInfoWindowAdapter;
import com.test.googlemaps2019v2.adapters.PlaceAutoSuggestAdapter;
import com.test.googlemaps2019v2.adapters.UserRecyclerAdapter;
import com.test.googlemaps2019v2.models.Event;
import com.test.googlemaps2019v2.models.EventClusterMarker;
import com.test.googlemaps2019v2.models.EventLocation;
import com.test.googlemaps2019v2.models.UserClusterMarker;
import com.test.googlemaps2019v2.models.PolylineData;
import com.test.googlemaps2019v2.models.User;
import com.test.googlemaps2019v2.models.UserLocation;
import com.test.googlemaps2019v2.util.EventClusterManagerRenderer;
import com.test.googlemaps2019v2.util.UserClusterManagerRenderer;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.test.googlemaps2019v2.Constants.DEFAULT_ZOOM;
import static com.test.googlemaps2019v2.Constants.MAPVIEW_BUNDLE_KEY;

//@SuppressWarnings("ALL")
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
    private ImageView mPlus;


    //текстовые поля внутри шаблона
    private MaterialEditText eventDate;
    private MaterialAutoCompleteTextView eventAddressDialog;
    private MaterialEditText eventDescription;
    private MaterialEditText eventType;

    //vars
    private List<Address> addressList = new ArrayList<>();
    private Address eventAddress;
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<Event> mEventList = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<EventLocation> mEventLocations = new ArrayList<>();
    private ArrayList<EventClusterMarker> mEventClusterMarkers= new ArrayList<>();
    private ArrayList<UserClusterMarker> mUserClusterMarkers = new ArrayList<>();
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition;

    private ClusterManager<UserClusterMarker> userClusterManager;
    private ClusterManager<EventClusterMarker> eventClusterManager;
    private UserClusterManagerRenderer userClusterManagerRenderer;
    private EventClusterManagerRenderer eventClusterManagerRenderer;
    private int mMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private Marker mSelectedMarker = null;



    private Event event;
    private EventLocation mEventLocation;
    private FirebaseFirestore mDb;
    private DocumentReference eventRef;


    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();
        if (mUserLocations.size() == 0 && mEventLocations.size() == 0) { // make sure the addressList doesn't duplicate by navigating back
            if (getArguments() != null) {
                final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
                mUserList.addAll(users);
                final ArrayList<UserLocation> userLocations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
                mUserLocations.addAll(userLocations);

                final ArrayList<Event> events = getArguments().getParcelableArrayList(getString(R.string.intent_event_list));
                mEventList.addAll(events);
                final ArrayList<EventLocation> eventLocations = getArguments().getParcelableArrayList(getString(R.string.intent_event_locations));
                mEventLocations.addAll(eventLocations);
            }
        }
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
        mPlus = view.findViewById(R.id.ic_plus);

        initUserListRecyclerView();
        initGoogleMap(savedInstanceState);
        setUserPosition();
        init();
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
            for(final UserClusterMarker userClusterMarker : mUserClusterMarkers){

                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_user_locations))
                        .document(userClusterMarker.getUser().getUser_id());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);

                            // update the location
                            for (int i = 0; i < mUserClusterMarkers.size(); i++) {
                                try {
                                    if (mUserClusterMarkers.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {

                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getGeo_point().getLatitude(),
                                                updatedUserLocation.getGeo_point().getLongitude()
                                        );

                                        mUserClusterMarkers.get(i).setPosition(updatedLatLng);
                                        userClusterManagerRenderer.setUpdateMarker(mUserClusterMarkers.get(i));
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

    private void getEventDetails(){     //1 //Получаем данные авторизованного пользователя
            if(mEventLocation == null)//Изначально нет данных о местоположении мероприятия
            {
                mEventLocation = new EventLocation();
                eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Log.d(TAG, "getEventDetails: successfully set the event.");
                            //Event event = task.getResult().toObject(Event.class);
                            mEventLocation.setEvent(event);
                            getLastKnownLocation();
                        }
                    }
                });
            }
            else{
                getLastKnownLocation();
            }
        }

    private void getLastKnownLocation() { //Получаем координаты мероприятия//2
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GeoPoint geoPoint = new GeoPoint(eventAddress.getLatitude(), eventAddress.getLongitude());
        mEventLocation.setGeo_point(geoPoint);
        mEventLocation.setTimestamp(null);
        saveEventLocation();
    }

    private void saveEventLocation(){    //3 //Связывание с firebase, сохранение координат пользователя

        if(mEventLocation != null){
//            DocumentReference locationRef = mDb
//                    .collection(getString(R.string.collection_event_locations))
//                    .document();
            String chatId = getArguments().getString("chat_id");
            DocumentReference locationRef = mDb
                    .collection(getString(R.string.collection_chatrooms))
                    .document(chatId)
                    .collection(getString(R.string.collection_event_locations))
                    .document();

            mEventLocation.setEventLocation_id(locationRef.getId());
            mEventLocation.setEvent(event); //*********************
            locationRef.set(mEventLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveEventLocation: \ninserted event location into database." +
                                "\n latitude: " + mEventLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mEventLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }

    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(userClusterManager != null){
                userClusterManager.clearItems();
            }

            if (mUserClusterMarkers.size() > 0) {
                mUserClusterMarkers.clear();
                mUserClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    private void setUpClusterManager(GoogleMap googleMap){

        if(userClusterManager == null){
            userClusterManager = new ClusterManager<UserClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            userClusterManager.setRenderer(userClusterManagerRenderer);
        }

        if(eventClusterManager == null){
            eventClusterManager = new ClusterManager<EventClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            if(eventClusterManagerRenderer == null) {
                eventClusterManagerRenderer = new EventClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        eventClusterManager
                );
                eventClusterManager.setRenderer(eventClusterManagerRenderer);
            }
        }
        // 3
        googleMap.setOnCameraIdleListener(userClusterManager);
        addMapMarkersWithTouch();
    }

        private void addMapMarkersWithTouch(){
           mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick(LatLng latLng) {
                   addMapMarkers(latLng);
               }
           });
        }

    private void addMapMarkers(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocateMarker: IOException err: " + e.getMessage());
        }
        if (addressList.size() > 0) {
            //vars
            eventAddress = addressList.get(0);
            Log.i(TAG, "geoLocateMarker: found a location: " + eventAddress.getAddressLine(0));
            Toast.makeText(getActivity(), eventAddress.getAddressLine(0), Toast.LENGTH_LONG).show();
        }
        String title = "Event by Anonymous";
        for (UserLocation userLocation : mUserLocations) {
            if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                title = "Event by " + userLocation.getUser().getUsername();
            }
        }
        EventClusterMarker newEventClusterMarker;
        if (event != null) {
            newEventClusterMarker = new EventClusterMarker(latLng, title,
                    event.toString());
            getEventDetails();
        }else {
            newEventClusterMarker = new EventClusterMarker(latLng, title,
                addressList.get(0).getAddressLine(0));
        }
            mEventClusterMarkers.add(newEventClusterMarker);
            eventClusterManager.addItem(newEventClusterMarker);
            eventClusterManager.cluster();

            mGoogleMap.setInfoWindowAdapter(eventClusterManager.getMarkerManager());  // 3
            eventClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity())); // 4

    }

    private void addUserMapMarkers(){

        if(mGoogleMap != null){

            resetMap();

            if(userClusterManager == null){
                userClusterManager = new ClusterManager<UserClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(userClusterManagerRenderer == null){
                userClusterManagerRenderer = new UserClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        userClusterManager
                );
                userClusterManagerRenderer.setMinClusterSize(2);
                userClusterManager.setRenderer(userClusterManagerRenderer);
            }
            mGoogleMap.setOnInfoWindowClickListener(this);

            for(UserLocation userLocation: mUserLocations){

                Log.d(TAG, "addUserMapMarkers: location: " + userLocation.getGeo_point().toString());
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
                        Log.d(TAG, "addUserMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
                    }
                    UserClusterMarker newUserClusterMarker = new UserClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );
                    userClusterManager.addItem(newUserClusterMarker);
                    mUserClusterMarkers.add(newUserClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addUserMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            userClusterManager.cluster();

            getDeviceLocation();    //setCameraView() - deleted
        }
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

    }

    private void initUserListRecyclerView() {
        UserRecyclerAdapter mUserRecyclerAdapter = new UserRecyclerAdapter(mUserList, this);
        mUserListRecyclerView.setAdapter(mUserRecyclerAdapter);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void init(){        //AutoCompleteTV
        Log.d(TAG, "init: initialization");

        mSearchText.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || keyEvent.getAction() == KeyEvent.ACTION_UP){

                    //execute our method for searching
                    geoLocate(mSearchText);
                }
                return false;
            }
        });
        //hideSoftKeyboard();
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked Gps icon");
                getDeviceLocation();
            }
        });

        mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked Plus icon");
                addEvent();
            }
        });
    }

    private void addEvent(){
//            eventRef = mDb.collection(getString(R.string.collection_event))
//                .document();

        String chatId = getArguments().getString("chat_id");
        Log.d(TAG, "addEvent: chatroomID: " + chatId);

        eventRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(chatId)
                .collection(getString(R.string.collection_event))
                .document();
        //eventRef.set(event); // Don't care about listening for completion.

//        EventClient eventClient = new EventClient();
//        Event event = eventClient.getEvent();


            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            dialog.setTitle("Add event").setMessage("Type event description");

            LayoutInflater inflater = LayoutInflater.from(getActivity()); //Получаем нужный шаблон
            View addEvent = inflater.inflate(R.layout.dialog_add_event, null);
            dialog.setView(addEvent);

            //текстовые поля внутри шаблона
            eventDate = addEvent.findViewById(R.id.date);
            eventAddressDialog= addEvent.findViewById(R.id.addressAutoComplete);
            eventDescription = addEvent.findViewById(R.id.eventDescription);
            eventType = addEvent.findViewById(R.id.type);
            eventAddressDialog.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

            dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.setPositiveButton("добавить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "onClick");
                    if (checkField()) return;
                    event = new Event(eventAddressDialog.getText().toString(),
                            eventDescription.getText().toString(),
                            eventDate.getText().toString(),
                            eventType.getText().toString());

                    event.setEvent_id(eventRef.getId());

                    eventRef.set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(),"Add event on DataBase",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getContext(),"Add event on DataBase failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    geoLocate(eventAddressDialog);
                }
            });
            dialog.show();
    }

    private boolean checkField() {
        if (TextUtils.isEmpty(eventDate.getText().toString())) {
            Toast.makeText(getContext(), "Empty field name", LENGTH_SHORT).show();
            Log.d(TAG, "Empty field name");
            return true;
        }
        if (TextUtils.isEmpty(eventAddressDialog.getText().toString())) {
            Toast.makeText(getContext(), "Empty field eventAddress", LENGTH_SHORT).show();
            Log.d(TAG, "Empty field eventAddress");
            return true;
        }
        if (TextUtils.isEmpty(eventDescription.getText().toString())) {
            Toast.makeText(getContext(), "empty field event description", LENGTH_SHORT).show();
            Log.d(TAG, "Empty field event description");
            return true;
        }
        if (eventType.getText().toString().length() < 5) {
            Toast.makeText(getContext(), "empty field type", LENGTH_SHORT).show();
            Log.d(TAG, "Empty field type");
            return true;
        }
        return false;
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try{
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
                            Toast.makeText(getContext(), "unable to get current location", LENGTH_SHORT).show();
                        }
                    }
                });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void geoLocate(View V){   //Поиск локации в TextAutoComplete
        Log.d(TAG, "geoLocateTV: geoLocating");
        String searchString;
        switch (V.getId()) {
            case R.id.addressAutoComplete:
                searchString = eventAddressDialog.getText().toString();
                break;
            case R.id.input_search:
                searchString = mSearchText.getText().toString();
                break;
            default:
                searchString = "Search string initialization error";
                Log.e(TAG, "geoLocate: Search string initialization error");
                break;
        }

        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG, "geoLocateTV: IOException err:" + e.getMessage());
        }
        if (list.size()>0){
            Address address = list.get(0);
            Log.i(TAG, "geoLocateTV: found a location: " + address.getAddressLine(0));
            Toast.makeText(getActivity(),address.getAddressLine(0), Toast.LENGTH_LONG).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom),600,null);
        if(!title.equals("My Location")){
            addMapMarkers(latLng);
        }
       // hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
       this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

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
        mGoogleMap = map;
        addUserMapMarkers();
        mGoogleMap.setOnPolylineClickListener(this);
        setUpClusterManager(mGoogleMap);
        //addMapMarkersWithTouch(mGoogleMap);
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
                addUserMapMarkers();
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
                                Toast.makeText(getActivity(), "Couldn't open map", LENGTH_SHORT).show();
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
                        })
                        .setNeutralButton("Edit description", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                AlertDialog.Builder neutralDialog = new AlertDialog.Builder(getActivity());
                                neutralDialog.setTitle("Edit description");

                                LayoutInflater inflater = LayoutInflater.from(getActivity()); //Получаем нужный шаблон
                                View eventDescription = inflater.inflate(R.layout.dialog_edit_description_event, null);
                                neutralDialog.setView(eventDescription);

                                //текстовые поля внутри шаблона
                                final MaterialEditText descriptionText = eventDescription.findViewById(R.id.eventDescription);

                                neutralDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                neutralDialog.setPositiveButton("Add description", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (descriptionText == null){
                                            Toast.makeText(getActivity().getApplicationContext(),"You have not added a description",Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            marker.setSnippet(addressList.get(0).getAddressLine(0)+ "\n" + descriptionText.getText().toString());
                                        }
                                    }
                                });
                                neutralDialog.show();
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

    private void zoomRoute(List<LatLng> lstLatLngRoute) {

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

        for(UserClusterMarker userClusterMarker : mUserClusterMarkers){
            if(selectedUserId.equals(userClusterMarker.getUser().getUser_id())){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(userClusterMarker.getPosition().latitude, userClusterMarker.getPosition().longitude)),
                        600,
                        null
                );
                break;
            }
        }
    }
}
