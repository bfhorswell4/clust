package com.example.clust;
import com.snowplowanalytics.snowplow.tracker.*;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;

import android.content.Context;

/**
 * A class that builds a SnowPlow Tracker
 */
public class SnowplowTrackerBuilder {
    /**
     * A method to build the Tracker for our applicaiton
     */
    public static Tracker getTracker(Context context) {
        Emitter emitter = getEmitter(context);
        Subject subject = getSubject(context); // Optional

        return Tracker.init(new Tracker.TrackerBuilder(emitter, "Clust", "ClustID", context)
                .lifecycleEvents(true)
                .geoLocationContext(true)
                .installTracking(true)
                .subject(subject) // Optional
                .build()
        );
    }

    private static Emitter getEmitter(Context context) {
        return new Emitter.EmitterBuilder("10.0.2.2:9090", context)
                .method(HttpMethod.POST)
                .build();
    }

    private static Subject getSubject(Context context) {
        return new Subject.SubjectBuilder()
                .context(context)
                .build();
    }
}
