package com.example.clust;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class KMeansClusterer {

    public static ArrayList<Cluster> clusterLocations(ArrayList<LatLng> locations, int k) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            clusters.add(new Cluster(locations.get(i)));
        }

        return clusters;
    }
}
