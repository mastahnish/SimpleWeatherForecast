package com.myos.simpleweatherforecast;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class WeatherFragment extends Fragment {

    private Typeface weatherFont;

    private TextView tvCityField;
    private TextView tvUpdatedField;
    private TextView tvDetailsField;
    private TextView tvCurrentTemperatureField;
    //    private TextView weatherIcon;
    private RelativeLayout rlCityData;
    private RelativeLayout rlNoCityData;
    private FrameLayout flFragmentMain;
    private NetworkImageView nivWeatherIcon;
    private NetworkImageView nivLocationPhoto;
    private TextView tvCurrentDesc;
    private SwipeRefreshLayout srlPhotoRefresh;

    private DatabaseHandler db;

    private int cityID;
    public static final java.lang.String ARG_PAGE = "arg_page";

    public static String ICON_URL = "http://openweathermap.org/img/w/";

    CityDialogFragment.LocationRefreshInterface mCallback;

    public static WeatherFragment newInstance(int cityID) {
        WeatherFragment myFragment = new WeatherFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_PAGE, cityID);
        myFragment.setArguments(arguments);

        return myFragment;
    }


    public WeatherFragment() {
    }

    public int getCityID() {
        return cityID;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivity activity = null;
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
        try {
            mCallback = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onRefreshLocation");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        initialize(rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            cityID = arguments.getInt(ARG_PAGE);
            updateWeatherData(cityID);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final int cityIDfinal = cityID;
        srlPhotoRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d("cobytu", getClass().getName() + " Odświeżyłem");

                processCityDataGETRequest(getActivity(), cityIDfinal);
            }
        });

    }

    private void initialize(View v) {
        flFragmentMain = (FrameLayout) v.findViewById(R.id.fl_fragment_main);
        tvCityField = (TextView) v.findViewById(R.id.tv_city_field);
        tvUpdatedField = (TextView) v.findViewById(R.id.tv_updated_field);
        tvDetailsField = (TextView) v.findViewById(R.id.tv_details_field);
        tvCurrentTemperatureField = (TextView) v.findViewById(R.id.tv_current_temperature_field);
//        weatherIcon = (TextView) v.findViewById(R.id.tv_weather_icon);
        rlCityData = (RelativeLayout) v.findViewById(R.id.rl_city_data);
        rlNoCityData = (RelativeLayout) v.findViewById(R.id.rl_no_city_data);
        nivWeatherIcon = (NetworkImageView) v.findViewById(R.id.niv_weather_icon);
        nivLocationPhoto = (NetworkImageView) v.findViewById(R.id.niv_location_photo);
        tvCurrentDesc = (TextView) v.findViewById(R.id.tv_current_description);

        srlPhotoRefresh = (SwipeRefreshLayout) v.findViewById(R.id.srl_fragment_weather);
//        weatherIcon.setTypeface(weatherFont);
    }

    private void updateWeatherData(final int cityID) {

        JSONObject json = null;
        try {
            Log.d("tag", getClass().getName() + " cityID: " + cityID);
            Log.d("tag", getClass().getName() + "exists?: " + String.valueOf(db.checkIfCityTableExists()));
            if (cityID > 0) {
                if (db.checkIfCityTableExists()) {

                    String jsonStr = db.getCityDetails(cityID).get(DatabaseHandler.getKeyJsonData());
                    Log.d("tag", "jsonStr: " + jsonStr);
                    if (jsonStr != null && !jsonStr.isEmpty()) {

                        json = new JSONObject(jsonStr);

                        if (json == null) {
                            Snackbar.make(flFragmentMain, getActivity().getString(R.string.place_not_found), Snackbar.LENGTH_SHORT).show();

                            showView(false);


                        } else {
                            final JSONObject finalJson = json;
                            renderWeather(finalJson);


                        }
                    } else {
                        Snackbar.make(flFragmentMain, getActivity().getString(R.string.noLocation), Snackbar.LENGTH_SHORT).show();

                        showView(false);

                    }

                } else {
                    showView(false);
                }
            } else {
                showView(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showView(boolean decision) {
        if (decision) {
            rlCityData.setVisibility(View.VISIBLE);
            rlNoCityData.setVisibility(View.INVISIBLE);
        } else {
            rlCityData.setVisibility(View.INVISIBLE);
            rlNoCityData.setVisibility(View.VISIBLE);
        }
    }

    private void renderWeather(JSONObject json) {
        Log.d("tag", json.toString());
        showView(true);
        try {
            tvCityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            tvDetailsField.setText("Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            tvCurrentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");
            tvCurrentDesc.setText(details.getString("description").toUpperCase(Locale.US));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            tvUpdatedField.setText("Last update: " + updatedOn);


            setWeatherIcon(details.getString("icon"), json.getInt("id"));
        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(/*int actualId, long sunrise, long sunset*/String icon, int cityID) {
        String url = new StringBuilder(ICON_URL).append(icon + ".png").toString();
        nivWeatherIcon.setImageUrl(url, Singleton.getInstance().getImageLoader());

        Log.d("tag", getClass().getName() + "from db photo ref: " + db.getCityDetails(cityID).get(DatabaseHandler.getKeyPhotoReference()));

        String urlPhoto = db.getCityDetails(cityID).get(DatabaseHandler.getKeyPhotoReference());
        Log.d("tag", getClass().getName() + " urlPhoto: " + urlPhoto);
        nivLocationPhoto.setImageUrl(urlPhoto, Singleton.getInstance().getImageLoader());


    }

    public static final String OPEN_WEATHER_MAP_API_BY_ID = "http://api.openweathermap.org/data/2.5/weather?id=%s&units=metric";
    private JSONObject json;

    public void processCityDataGETRequest(final Context context, final int cityID) {

        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_BY_ID, cityID));

            Log.d("tag", getClass().getName() + url.toString());
            StringRequest processData = new StringRequest(Request.Method.GET, url.toString(),//new StringBuilder(OPEN_WEATHER_MAP_API).append(city).toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("tag", response);

                                json = new JSONObject(response);
                                if (json.getInt("cod") != 200) {
                                    if (json.getInt("cod") == 404) {
                                        Snackbar.make(rlCityData, R.string.noSuchCity, Snackbar.LENGTH_SHORT).show();

                                        json = null;
                                    }
                                } else {

                                    db.updateCity(cityID, DatabaseHandler.getKeyJsonData(), json.toString());

                                    String lat, lon;

                                    lat = json.getJSONObject("coord").getString("lat");
                                    lon = json.getJSONObject("coord").getString("lon");

                                    processPanoramioApi(lat, lon, cityID);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", context.getString(R.string.open_weather_maps_app_id));
                    return params;
                }
            };
            Singleton.getInstance().getRequestQueue().add(processData);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void processPanoramioApi(final String lat, final String lon, final int cityID) {

        String url = new StringBuilder(Constants.PANORRAMIO_PLACES_REQUEST_URL)
                .append("minx=" + Constants.doubleToFloor(lon) + "&miny=" + Constants.doubleToFloor(lat) + "&maxx=" + Double.valueOf(lon) + "&maxy=" + Double.valueOf(lat)).toString();

        Log.d("tag", getClass().getName() + "request URL  sent: " + url);

        StringRequest processPlacePhoto = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json;

                        try {
                            json = new JSONObject(response);
                            int count = json.getInt("count");
                            if (count > 0) {

                                processPanoramioRandomPhotoApi(lat, lon, cityID, count);

                            } else {
                                Snackbar.make(rlCityData, R.string.noPhotoInGoogle, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        Singleton.getInstance().getRequestQueue().add(processPlacePhoto);

    }

    private void processPanoramioRandomPhotoApi(final String lat, final String lon, final int cityID, final int maxNumber) {
        Random r = new Random();
        int from = r.nextInt(maxNumber);

        String url = new StringBuilder(Constants.PANORRAMIO_PLACES_RANDOM_PHOTOS_URL)
                .append("minx=" + Constants.doubleToFloor(lon) + "&miny=" + Constants.doubleToFloor(lat) + "&maxx=" + Double.valueOf(lon) + "&maxy=" + Double.valueOf(lat) + "&from=" + from + "&to=" + (from + 1)).toString();

        Log.d("tag", getClass().getName() + "request URL  sent: " + url);

        StringRequest processPlacePhoto = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json;

                        try {
                            json = new JSONObject(response);

                            if (json.getInt("count") > 0) {
                                JSONArray array = json.optJSONArray("photos");

                                if (array.length() != 0) {
                                    String photoURL = array.getJSONObject(0).getString("photo_file_url");
                                    if (photoURL != null) {
                                        Log.d("tag", getClass().getName() + "photo_reference url: " + photoURL);
                                        db.updateCity(cityID, DatabaseHandler.getKeyPhotoReference(), photoURL);
                                        if (mCallback != null) {
                                            srlPhotoRefresh.setRefreshing(false);
                                            mCallback.onRefreshLocation();
                                        }

                                    }
                                } else {
                                    processPanoramioRandomPhotoApi(lat, lon, cityID, maxNumber);
                                }

                            } else {
                                Snackbar.make(rlCityData, R.string.noPhotoInGoogle, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        Singleton.getInstance().getRequestQueue().add(processPlacePhoto);

    }


}
