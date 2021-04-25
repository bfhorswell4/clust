# Clust

With lockdown slowly lifting and the potential in the future to go travelling again, I thought It'd be fun to make a small app that helps with planning trips abroad + 
has Snowplow tracking embedded in it! 

## Introduction
**Clust** is a small android app that utilises the **Google Maps/Places API** to provide a map upon which you can add potential sightseeing locations that you want 
to visit on a trip. You can then click the clustering button and enter how many days long your trip is (for now between 1 and 8!). The app then uses a little algorithm known 
as K Means Clustering to optimally group your locations so that you can visit those closest each other on the same day. This way you don't waste lots of time travelling between locations that are far from each other! Finally there's a reset button so you can clear the map and start over :) Below is an example of some popular sights in London that have
been clustered for a 3 day trip. 

![alt text](https://github.com/bfhorswell4/clust/blob/master/example_images/before_clustering.png)           ![alt text](https://github.com/bfhorswell4/clust/blob/master/example_images/after_clustering.png)



## Data Tracking
When implementing tracking I tried to think of what data might be useful to track if this was to be developed into a fully fledged app. I added some default auto-tracking features that come with the Android tracker, such as install tracking and application crashes. I also tried to think of some custom events that I myself could define. In the end
I decided on two I thought were important:
  - `add_location_event`: An event that tracks locational information everytime a user adds a potential place they want to see on their trip to the map. I thought this could be useful in beginning to discern useful data that could inform tourism, such as what locations are popular hotspots with users.
  - `cluster_locations_event`: An event that is fired whenever a user clusters their locations. I thought this would be useful to provide some more complex and nuanced information about users travels, such as how popular locations are grouped in relation to each other e.g it could be useful to know that Big Ben and the London Eye are popular locations that are very near to each other to provide some sort of tie in tourist deals!
  

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


