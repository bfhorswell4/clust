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
 * Utility Class used to hold logic
 *  for  various Tracker Events.
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
        attributes.put("name", place.getName());
        SelfDescribingJson test = new SelfDescribingJson("iglu:test.example.iglu/add_location_event/jsonschema/1-0-0", attributes);
        tracker.track(SelfDescribing.builder().eventData(test).build());
    }
}