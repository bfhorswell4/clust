package com.example.clust;
import com.snowplowanalytics.snowplow.tracker.*;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;

import android.content.Context;

/**
 * A class that builds a SnowPlow Tracker for our Android application
 */
public class SnowplowTrackerBuilder {

    /**
     * A method to retrieve the tracker
     * @param context Our application context
     * @return A SnowPlow android tracker
     */
    public static Tracker getTracker(Context context) {
        Emitter emitter = getEmitter(context);
        Subject subject = getSubject(context);

        return Tracker.init(new Tracker.TrackerBuilder(emitter, "Clust", "ClustID", context)
                .lifecycleEvents(true)
                .applicationCrash(true)
                .geoLocationContext(true)
                .installTracking(true)
                .subject(subject)
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
