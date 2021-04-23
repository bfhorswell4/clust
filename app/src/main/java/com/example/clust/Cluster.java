package com.example.clust;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A class that represents a group of LatLng locations
 */
public class Cluster {
    private ArrayList<LatLng> locations;
    private LatLng center;

    /**
     * Initialises a Cluster
     * @param location The initial single location that defines the cluster
     */
    public Cluster(LatLng location){
        locations = new ArrayList<>();
        locations.add(location);
        center = location;
    }

    /**
     * This method calculates the current center of the cluster
     * @return The current center coordinate of the cluster
     */
    public LatLng getCenter(){
        if(this.locations.size()==1){
            return this.locations.get(0);
        }

       return this.locations.get(0);
    }

    public ArrayList<LatLng> getLocations(){
        return this.locations;
    }

    public void addLocations(ArrayList<LatLng> new_locations){
        this.locations.addAll(new_locations);
    }
}
