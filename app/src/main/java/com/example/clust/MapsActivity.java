package com.example.clust;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.snowplowanalytics.snowplow.tracker.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    // Useful static definitions
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
    private static final float[] MARKER_COLOURS = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW};

    // UI elements
    private GoogleMap mMap;
    FloatingActionButton addLocationButton;
    FloatingActionButton clusterLocationsButton;
    FloatingActionButton resetButton;

    private Tracker tracker;
    private ArrayList<Place> currLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialise buttons and their listeners
        addLocationButton = findViewById(R.id.addLocationButton);
        clusterLocationsButton = findViewById(R.id.clusterLocationsButton);
        resetButton = findViewById(R.id.resetButton);
        addLocationButton.setOnClickListener(this);
        clusterLocationsButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        // Initialise Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        currLocations = new ArrayList<>();

        // Initialise Snowplow tracking
        tracker = SnowplowTrackerBuilder.getTracker(this.getApplicationContext());
    }

    /**
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Handle our location autocomplete intent response
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Result succeeded, adding location to map
                Place location = Autocomplete.getPlaceFromIntent(data);
                TrackerEvents.trackAddLocationEvent(tracker, location);
                currLocations.add(location);

                mMap.addMarker(new MarkerOptions()
                        .position(location.getLatLng())
                        .title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getLatLng(), 5));

                Log.i(TAG, "Location Added: " + location.getLatLng().toString() + "," + location.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Result threw error
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.getStatusMessage());
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A generic handler to route onClick methods for various buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addLocationButton:
                handleAddLocationClick();
                break;
            case R.id.clusterLocationsButton:
                handleClusterLocationsClick();
                break;
            case R.id.resetButton:
                handleResetClick();
            default:
                break;
        }
    }

    /**
     * Handles a user clicking the add location button
     */
    private void handleAddLocationClick(){
        Log.i(TAG, "Add Location Button Clicked");

        // Set fields for what type of place data should be returned on user selection
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.NAME);

        // Starts up a location autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                fieldList).build(MapsActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Handles a user clicking the cluster locations button
     */
    private void handleClusterLocationsClick(){
        Log.i(TAG, "Cluster Locations button clicked");

        // Create a dialog with a numerical input for setting cluster number
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
        dialog.setTitle("How many days long is your trip?");
        final EditText numberInput = new EditText(MapsActivity.this);
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(numberInput);

        // Set the dialog to have an OK button which will being clustering
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Grab the number of clusters desired
                int clusterCount = Integer.valueOf(numberInput.getText().toString());
                Log.i(TAG, "Cluster Count in Numerical Input: " + clusterCount);

                if(clusterCount > 8 || clusterCount < 1){
                    Toast.makeText(getApplicationContext(),
                            "Enter a number of days between 1 and 8", Toast.LENGTH_LONG).show();
                }else{
                    // Calculate clustering
                    ArrayList<Cluster> clusters = KMeansClusterer.clusterLocations(currLocations, clusterCount);
                    TrackerEvents.trackClusterLocationsEvent(tracker, clusters);
                    mMap.clear();

                    // For each of our clusters, associate a colour with it and add the locations to the map
                    for(int c = 0; c < clusters.size(); c++){
                        ArrayList<Place> locs = clusters.get(c).getLocations();
                        float colour = MARKER_COLOURS[c];

                        for(int l = 0; l < locs.size(); l++){
                            mMap.addMarker(new MarkerOptions()
                                    .position(locs.get(l).getLatLng())
                                    .icon(BitmapDescriptorFactory.defaultMarker(colour)));
                        }
                    }
                }
            }
        });

        // Set the dialog to have a cancel button
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();;
            }
        });

        // Start the dialog
        dialog.show();
    }

    /**
     * Handles a user resetting the map back to an empty state
     */
    private void handleResetClick(){
        TrackerEvents.trackResetMapEvent(tracker, currLocations.size());
        currLocations.clear();
        mMap.clear();
    }
}