package com.example.clust;

import com.google.android.libraries.places.api.model.Place;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;
import com.snowplowanalytics.snowplow.tracker.events.SelfDescribing;
import com.snowplowanalytics.snowplow.tracker.events.Structured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility Class used to send all
 * combinations of Tracker Events.
 */
public class TrackerEvents {

    public static void trackStructuredEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker) {
        tracker.track(Structured.builder().category("category").action("action").label("label").property("property").value(0.00).build());
    }

    private static void trackScreenView(com.snowplowanalytics.snowplow.tracker.Tracker tracker) {
        tracker.track(ScreenView.builder().name("screenName1").id(UUID.randomUUID().toString()).build());
    }

    public static void trackAddLocationEvent(com.snowplowanalytics.snowplow.tracker.Tracker tracker, Place place) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("latlngCoords", place.getLatLng().toString());
        SelfDescribingJson test = new SelfDescribingJson("iglu:com.snowplowanalytics.snowplow/link_click/jsonschema/1-0-1", attributes);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }
}