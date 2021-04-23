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

        while (clusters.size() != k) {
            double minDistance = Double.MAX_VALUE;
            int minI = 0;
            int minJ = 0;

            // Computethe  distances between all cluster centers and
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
