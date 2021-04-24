package com.example.clust;
import com.snowplowanalytics.snowplow.tracker.*;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;

import android.content.Context;

public class SnowplowTrackerBuilder {
    public static Tracker getTracker(Context context) {
        Emitter emitter = getEmitter(context);
        Subject subject = getSubject(context); // Optional

        return Tracker.init(new Tracker.TrackerBuilder(emitter, "Clust", "ClustID", context)
                .lifecycleEvents(true)
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
