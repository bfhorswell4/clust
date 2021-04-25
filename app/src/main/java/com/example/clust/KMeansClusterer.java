package com.example.clust;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;

/**
 * A class to hold our K Means Clustering algorithm logic
 */
public class KMeansClusterer {

    /**
     * Merges a list of locations into k clusters via K Means Clustering
     * @param locations The locations to cluster together
     * @param k The amount of clusters to generate
     * @return An ArrayList of Cluster objects
     */
    public static ArrayList<Cluster> clusterLocations(ArrayList<Place> locations, int k) {
        // Initially each cluster is made from a single location
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            clusters.add(new Cluster(locations.get(i)));
        }

        // It's not possible to have more clusters than locations,
        // so in this edge case we just return
        if(k > locations.size()){
            return clusters;
        }

        // Loop the algorithm until we have our desired amount of clusters
        while (clusters.size() != k) {
            double minDistance = Double.MAX_VALUE;
            int minI = 0;
            int minJ = 0;

            // Compute the  distances between all cluster centers and
            // find the Ith and Jth clusters that are closest
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.size(); j++) {
                    LatLng centerI = clusters.get(i).getCenter();
                    LatLng centerJ = clusters.get(j).getCenter();

                    float [] dist_results = new float[5];
                    Location.distanceBetween(centerI.latitude, centerI.longitude, centerJ.latitude, centerJ.longitude, dist_results);
                    float dist = dist_results[0];
                    if (dist < minDistance && dist != 0) {
                        minI = i;
                        minJ = j;
                        minDistance = dist;
                    }
                }
            }

            // Merge these two clusters that are closest into a single cluster
            Cluster cI = clusters.get(minI);
            Cluster cJ = clusters.get(minJ);
            cJ.addLocations(cI.getLocations());
            clusters.remove(cI);
        }

        return clusters;
    }
}
