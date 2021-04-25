# Clust

With lockdown slowly lifting and the potential in the future to go travelling again, I thought It'd be a fun challenge to make a small app that helps with planning trips abroad, and then try to embed a Snowplow tracker into it!

## Introduction
**Clust** is a small android app that heavily utilises the **Google Maps/Places API**. It provides a map upon which you can add potential sightseeing locations that you want 
to visit on a trip via the add location button.

You can then click the clustering button upon which you will be asked how long your trip is (for now between 1 and 8 days!). The app then uses a little algorithm known as K Means Clustering to optimally group your locations so that you can visit those closest each other on the same day. This way you don't waste lots of time on your trip going back and forth between locations that could all be visited on a single day! 

Finally there is a reset button which is convenient for clearing the map and starting over :) 

Below is an example of some popular sights in London that have
been clustered for a 3 day trip in the app:

![alt text](https://github.com/bfhorswell4/clust/blob/master/example_images/before_clustering.png)           ![alt text](https://github.com/bfhorswell4/clust/blob/master/example_images/after_clustering.png)


## Snowplow Tracking
When implementing tracking I tried to think of what data might be useful to analyse if this was to be developed into a fully productised travel planning app. Initially I added some default auto-tracking features that come with the Android tracker, such as install tracking  as well as Google Analytics-style structured events.

However I also tried to think of some custom events that I could define myself via asscoiated schemas etc. In the end I decided on two I thought could be potentially useful and important:
  - `add_location_event`: An event that triggeres everytime a user adds a potential place they want to vsit on their trip to the map. By tracking locational data, I thought this could be useful in beginning to discern useful data that could inform tourism, such as what locations are popular hotspots with users.
  - `cluster_locations_event`: An event that is fired whenever a user clusters their locations. I thought this would be useful to provide some more complex and nuanced data about users travel plans, such as how popular locations are grouped in relation to each other e.g it could be useful to know that Big Ben and the London Eye are popular locations that are very near to each other to provide some sort of tie in tourist experience!
  

## Running Clust

If you just want to try out the Clust app without tracking functionality, you can simply install the exported `clust.apk` provided in the root of this repo to any Android device physical or virtual (version 4.1 / API level 16 and above). Make sure that Internet access is enabled so that the app can pull from the various Google APIs it uses.

## Running with embedded tracking via Snowplow Micro

If you want to run Clust in full with embedded tracking that sends behavioural data to Snowplow Micro you will need to:
  1. Clone the [snowplow micro](https://github.com/snowplow-incubator/snowplow-micro/) repo
  2. Replace the `example/iglu.json` and `example/micro.conf` files with those [found in this repo](https://github.com/bfhorswell4/clust/tree/master/micro)
  3. In the root `snowplow-micro` folder structure you just cloned execute: 
  ```
  docker run --mount type=bind,source=$(pwd)/example,destination=/config -p 9090:9090 snowplow/snowplow-micro:1.1.1 --collector-config /config/micro.conf --iglu /config/iglu.json
  ```
   to run and expose a Snowplow Micro instance locally.
  
  4. Download [Android Studio](https://developer.android.com/studio?gclid=Cj0KCQjwppSEBhCGARIsANIs4p4gLhT4A3u6ptKJQma5tHiCbH4Ne6W-npxhNqw3C9SG8tp-uQ2ZaJsaAmA5EALw_wcB&gclsrc=aw.ds) if you don't have it installed already
  5. Follows the instructions for [setting up and running an Android Virtual Device](https://developer.android.com/studio/run/managing-avds)
  6. Once you have a virtual device up and running you can simply drag the `clust.apk` file into the emulator to install it. 
  7. The app will now send tracking data to your local Snowplow Micro instance! You can see the data Snowplow Micro has recieved by visiting `http://localhost:9090/micro/good` in your browser!


