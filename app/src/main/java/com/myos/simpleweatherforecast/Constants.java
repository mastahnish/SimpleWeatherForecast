package com.myos.simpleweatherforecast;

/**
 * Created by Jacek on 2016-03-02.
 */
public class Constants {

    //Wersion for GooglePlacesApi - not too many views in here, thus I changed it to Panoramio API
    public static String GOOGLE_PLACES_API_KEY = "AIzaSyBhlzN-TsAfr5bXPgtCI2Zp8345x_PX87o";

    public static int maxWidth = 400;
    public static int radius = 10000;
    public static String place_type = "stadium";

    public static String GOOGLE_PLACES_REQUEST_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type="+place_type+"&radius="+radius+"&key="+GOOGLE_PLACES_API_KEY+"&location=";//lat,lon 52.22,18.25"
    public static String GOOGLE_PLACES_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="+maxWidth+"&key="+GOOGLE_PLACES_API_KEY+"&photoreference=";

    //Panoramio API
    public static int from = 0;
    public static int to = 1;
    public static String PANORRAMIO_PLACES_REQUEST_URL = "http://www.panoramio.com/map/get_panoramas.php?set=public&from="+from+"&to="+to+"&size=medium&mapfilter=true&";//minx=21&miny=52&maxx=21.011&maxy=52.229
    public static String PANORRAMIO_PLACES_RANDOM_PHOTOS_URL = "http://www.panoramio.com/map/get_panoramas.php?set=public&size=medium&mapfilter=true&";

    public static double doubleToFloor(String x) {
        return Math.floor(Double.valueOf(x));

    }
}
