# Clust

With lockdown slowly lifting and the potential in the future to go travelling again, I thought It'd be fun to make a small app that helps with planning trips abroad + 
has Snowplow tracking embedded in it! 

Clust is a small android app that utilises the Google Maps/Places API to provide a map where you can add potential locations that you want 
to visit on your trip. You can then click the clustering button and enter how many days long your trip is (for now between 1 and 8!). The app then uses a little algorithm known 
as K Means Clustering to optimally group your locations so that you visit those closest each other on the same day! Finally there's a reset button so you can clear the map and start over :)

## Running Clust

If you just want to check out how the Clust app works, it is a simple as installing the `clust.apk` provided in the root of this repo to any Android device physical or emulated that is
on version 4.1 (or API level 16) and above. 

## Running with Snowplow Micro Tracking

If you want to run Clust in full with Snowplow tracking embedded you will need to:
  1. Clone the [snowplow micro](https://github.com/snowplow-incubator/snowplow-micro/) repo
  2. Replace the `example/iglu.json` and `example/micro.conf` in this repo with those [found in this repo](https://github.com/bfhorswell4/clust/tree/master/micro)
  3. In the root `snowplow-micro` repo you just cloned execute: 
  ```
  docker run --mount type=bind,source=$(pwd)/example,destination=/config -p 9090:9090 snowplow/snowplow-micro:1.1.1 --collector-config /config/micro.conf --iglu /config/iglu.json
  ```
   to start running a snowplow micro instance locally.
  
  4. Download [Android Studio](https://developer.android.com/studio?gclid=Cj0KCQjwppSEBhCGARIsANIs4p4gLhT4A3u6ptKJQma5tHiCbH4Ne6W-npxhNqw3C9SG8tp-uQ2ZaJsaAmA5EALw_wcB&gclsrc=aw.ds) if you don't have it installed already
  5. Follows the instructions for [setting up and running an Android Virtual Device](https://developer.android.com/studio/run/managing-avds)
  6. Once you have a virtual device up and running you can simply drag the `clust.apk` file into the emulator to install it. 
  7. The app will now send tracking data to snowplow micro! You can see some of the data that is being tracked by visiting `http://localhost:9090/micro/good` in your browser!


