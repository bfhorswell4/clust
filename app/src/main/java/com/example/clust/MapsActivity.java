package com.example.clust;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

        addLocationButton = findViewById(R.id.addLocationButton);
        clusterLocationsButton = findViewById(R.id.clusterLocationsButton);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        currLocations = new ArrayList<>();

        addLocationButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Set fields for what type of place data should be returned on user selection
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);

                // Starts up an autocomplete intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(MapsActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        // TESTING FOR K MEANS

        clusterLocationsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Hardcode static cluster count for now for testing until we add a number input field
                int k = 2;
                ArrayList<Cluster> clusters = KMeansClusterer.clusterLocations(currLocations, k);
                for(int c = 0; c < clusters.size(); c++){
                    Log.i("KMEANS", "Cluster=" + c);
                    ArrayList<LatLng> locations = clusters.get(c).getLocations();
                    for(int l = 0; l < locations.size(); l++){
                        Log.i("KMEANS", "Location=" + locations.get(l).toString());
                    }
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
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
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}