package com.example.clust;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    FloatingActionButton addLocationButton;
    FloatingActionButton clusterLocationsButton;
    private ArrayList<LatLng> currLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialise buttons and their listeners
        addLocationButton = findViewById(R.id.addLocationButton);
        clusterLocationsButton = findViewById(R.id.clusterLocationsButton);
        addLocationButton.setOnClickListener(this);
        clusterLocationsButton.setOnClickListener(this);

        // Initialise Places API
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        currLocations = new ArrayList<>();

        Tracker tracker = SnowplowTrackerBuilder.getTracker(this.getApplicationContext());
        tracker.track(ScreenView.builder().name("screenName").build());

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
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Result succeeded, adding location to map
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(getApplicationContext(), "Place: " + place.getName() + ", " + place.getLatLng().toString(), Toast.LENGTH_LONG).show();
                currLocations.add(place.getLatLng());
                mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 5));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A generic handler to handle different onClick events for specific Buttons
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
            default:
                break;
        }
    }

    /**
     * Handles a user clicking the add location button
     */
    private void handleAddLocationClick(){
        // Set fields for what type of place data should be returned on user selection
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.NAME);

        // Starts up an autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                fieldList).build(MapsActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Handles a user clicking the cluster locations button
     */
    private void handleClusterLocationsClick(){
        // Create a dialog with a numerical input for setting cluster number
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
        dialog.setTitle("How many clusters?");
        final EditText numberInput = new EditText(MapsActivity.this);
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(numberInput);

        // Set the dialog to have an OK button which will start location clustering
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int clusterCount = Integer.valueOf(numberInput.getText().toString());
                mMap.clear();
                float[] colours = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_RED};
                ArrayList<Cluster> clusters = KMeansClusterer.clusterLocations(currLocations, clusterCount);

                // For each of our clusters, associate a colour with it and add the locations to the map
                for(int c = 0; c < clusters.size(); c++){
                    ArrayList<LatLng> locs = clusters.get(c).getLocations();
                    float colour = colours[c];
                    for(int l = 0; l < locs.size(); l++){
                        mMap.addMarker(new MarkerOptions()
                                .position(locs.get(l))
                                .icon(BitmapDescriptorFactory.defaultMarker(colour)));

                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clusters.get(0).getCenter(), 1));
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
}