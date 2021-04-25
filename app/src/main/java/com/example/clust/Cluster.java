package com.example.clust;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A class that represents a grouping of LatLng locations
 */
public class Cluster {
    private ArrayList<Place> locations;
    private LatLng center;

    /**
     * Initialises a Cluster
     * @param location An initial single location that defines the cluster
     */
    public Cluster(Place location){
        locations = new ArrayList<>();
        locations.add(location);
        center = location.getLatLng();
    }

    /**
     * This method calculates the center of the cluster
     * @return The center of the cluster
     */
    public LatLng getCenter(){
        if(this.locations.size()==1){
            return this.locations.get(0).getLatLng();
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i<locations.size(); i++){
            builder.include(locations.get(i).getLatLng());
        }

       return builder.build().getCenter();
    }

    /**
     * A getter for the clusters locations
     * @return The locations associated with the cluster
     */
    public ArrayList<Place> getLocations(){
        return this.locations;
    }

    /**
     * A utility method to add a group of new locations to the cluster
     * @param new_locations The new locations to merge into the cluster
     */
    public void addLocations(ArrayList<Place> new_locations){
        this.locations.addAll(new_locations);
    }
}
