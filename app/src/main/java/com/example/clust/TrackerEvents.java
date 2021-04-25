package com.example.clust;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;
import com.snowplowanalytics.snowplow.tracker.events.SelfDescribing;
import com.snowplowanalytics.snowplow.tracker.events.Structured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A utility Class used to hold logic
 *  for various Tracker Events.
 */
public class TrackerEvents {

    public static void trackResetMapEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, int amountOfLocations) {
        tracker.track(Structured.builder().category("Map").action("Resetting Locations").value((double)amountOfLocations).build());
    }

    public static void trackAddLocationEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, Place place) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("name", place.getName());
        attributes.put("address", place.getAddress());
        attributes.put("lat", place.getLatLng().latitude);
        attributes.put("lng", place.getLatLng().longitude);

        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/add_location_event/json_schema/1-0-0", attributes);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }

    public static void trackClusterLocationsEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, ArrayList<Cluster> clusters) {
        Map<String, ArrayList<Object>> attributes = new HashMap<>();
        ArrayList<Object> cluster_maps = new ArrayList<>();

        for(int c = 0; c < clusters.size(); c++){
            Cluster cluster = clusters.get(c);
            HashMap<String, Object> cluster_map = new HashMap<>();
            ArrayList<Place> locs = clusters.get(c).getLocations();
            ArrayList<Object> loc_maps = new ArrayList<>();

            HashMap<String, Double> center_map = new HashMap<>();
            center_map.put("lat", cluster.getCenter().latitude);
            center_map.put("lng", cluster.getCenter().longitude);
            cluster_map.put("cluster_center", center_map);

            for(int l = 0; l < locs.size(); l++){
                Place loc = locs.get(l);
                Map<String, Object> loc_map = new HashMap<>();
                loc_map.put("lat", loc.getLatLng().latitude);
                loc_map.put("lng", loc.getLatLng().longitude);
                loc_map.put("name", loc.getName());
                loc_map.put("address", loc.getAddress());
                loc_maps.add(loc_map);
            }
            cluster_map.put("locations", loc_maps);
            cluster_maps.add(cluster_map);
        }

        attributes.put("clusters", cluster_maps);
        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/cluster_locations_event/json_schema/1-0-0", attributes);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }
}