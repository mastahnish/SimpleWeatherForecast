package com.myos.simpleweatherforecast;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Jacek on 2016-03-01.
 */
public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    int getCity(){
        return prefs.getInt("cityID", 1);
    }

    void setCity(int cityID){
        prefs.edit().putInt("cityID", cityID).commit();
    }

}
