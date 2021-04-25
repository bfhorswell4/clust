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
 * Utility Class used to hold logic
 *  for  various Tracker Events.
 */
public class TrackerEvents {

    public static void trackStructuredEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker) {
        tracker.track(Structured.builder().category("category").action("action").label("label").property("property").value(0.00).build());
    }

    public static void trackScreenView(com.snowplowanalytics.snowplow.tracker.Tracker tracker) {
        tracker.track(ScreenView.builder().name("MapActivityScreen").id(UUID.randomUUID().toString()).build());
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
        ArrayList<Object> cluster_objs = new ArrayList<>();

        for(int c = 0; c < clusters.size(); c++){
            Cluster cluster = clusters.get(c);
            HashMap<String, Object> cluster_obj = new HashMap<>();
            ArrayList<Place> locs = clusters.get(c).getLocations();
            ArrayList<Object> loc_objs = new ArrayList<>();

            cluster_obj.put("cluster_center", cluster.getCenter().toString());


            for(int l = 0; l < locs.size(); l++){
                Place loc = locs.get(l);
                Map<String, Object> loc_obj = new HashMap<>();
                loc_obj.put("lat", loc.getLatLng().latitude);
                loc_obj.put("lng", loc.getLatLng().longitude);
                loc_obj.put("name", loc.getName());
                loc_obj.put("address", loc.getAddress());
                loc_objs.add(loc_obj);
            }
            cluster_obj.put("locations", loc_objs);
            cluster_objs.add(cluster_obj);
        }

        attributes.put("clusters", cluster_objs);
        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/cluster_locations_event/json_schema/1-0-0", attributes);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }
}