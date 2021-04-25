package com.example.clust;

import com.google.android.libraries.places.api.model.Place;
import com.snowplowanalytics.snowplow.tracker.events.SelfDescribing;
import com.snowplowanalytics.snowplow.tracker.events.Structured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class used to hold logic
 * for various Tracker Events.
 */
public class TrackerEvents {

    /**
     * A method to track reset map events
     * @param tracker The application tracker
     * @param amountOfLocations The number of locations the user has added before resetting
     */
    public static void trackResetMapEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, int amountOfLocations) {
        tracker.track(Structured.builder().category("Map").action("Resetting Locations").value((double)amountOfLocations).build());
    }

    /**
     * A method to track add location events
     * @param tracker The application tracker
     * @param location The location the user added
     */
    public static void trackAddLocationEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, Place location) {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("name", location.getName());
        dataMap.put("address", location.getAddress());
        dataMap.put("lat", location.getLatLng().latitude);
        dataMap.put("lng", location.getLatLng().longitude);

        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/add_location_event/json_schema/1-0-0", dataMap);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }

    /**
     * A method to track location clustering events
     * @param tracker The application tracker
     * @param clusters The clusters generated from location clustering
     */
    public static void trackClusterLocationsEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, ArrayList<Cluster> clusters) {
        Map<String, ArrayList<Object>> dataMap = new HashMap<>();
        ArrayList<Object> clusterMaps = new ArrayList<>();

        for(int c = 0; c < clusters.size(); c++){
            // Represent cluster object via a map
            Cluster cluster = clusters.get(c);
            HashMap<String, Object> clusterMap = new HashMap<>();

            // Represent cluster objects associated locations via an array of location maps
            ArrayList<Place> locs = clusters.get(c).getLocations();
            ArrayList<Object> locMaps = new ArrayList<>();

            // Add cluster center data to the cluster object
            HashMap<String, Double> centerMap = new HashMap<>();
            centerMap.put("lat", cluster.getCenter().latitude);
            centerMap.put("lng", cluster.getCenter().longitude);
            clusterMap.put("cluster_center", centerMap);

            // Add location data via the locations maps to the cluster object
            for(int l = 0; l < locs.size(); l++){
                Place loc = locs.get(l);
                Map<String, Object> locMap = new HashMap<>();
                locMap.put("lat", loc.getLatLng().latitude);
                locMap.put("lng", loc.getLatLng().longitude);
                locMap.put("name", loc.getName());
                locMap.put("address", loc.getAddress());
                locMaps.add(locMap);
            }
            clusterMap.put("locations", locMaps);
            clusterMaps.add(clusterMap);
        }

        dataMap.put("clusters", clusterMaps);
        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/cluster_locations_event/json_schema/1-0-0", dataMap);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }
}