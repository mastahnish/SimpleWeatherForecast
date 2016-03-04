package com.myos.simpleweatherforecast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jacek on 2016-03-02.
 */
class PagerAdapter extends FragmentStatePagerAdapter {



    public static ArrayList<City> cities;


    public PagerAdapter(FragmentManager fm, ArrayList<City> cities)
    {
        super(fm);
        this.cities = cities;

    }

    @Override
    public Fragment getItem(int position) {
        Log.e("tag", getClass().getName() + ": getItem current tab position: " + position);
        android.support.v4.app.Fragment myFragment = null;
        if(cities.isEmpty()){
            myFragment = WeatherFragment
                    .newInstance(0);
        }else{
          myFragment = WeatherFragment
                    .newInstance(cities.get(position).getCityId());
        }


        return myFragment;
    }

    @Override
    public int getCount() {
        Log.d("tag", getClass().getName() + "getCOunt: "+ cities.size());
        if(cities.isEmpty()){
            return 1;
        }else{
            return cities.size();
        }

    }

    @Override
    public int getItemPosition(Object object) {
        WeatherFragment myFragment = (WeatherFragment) object;
        int cityId = myFragment.getCityID();
        int position = cities.indexOf(cityId);

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }


    }
}