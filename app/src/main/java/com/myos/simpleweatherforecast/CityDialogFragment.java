package com.myos.simpleweatherforecast;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.jorgecastilloprz.FABProgressCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.myos.simpleweatherforecast.RecyclerViewAdapterCities.DisplayRVInferface;


public class CityDialogFragment extends DialogFragment implements View.OnClickListener, DisplayRVInferface {

    LocationRefreshInterface mCallback;

    DatabaseHandler db;

    //citiesList
    private ArrayList<City> citiesList = new ArrayList<>();
    private LinearLayout llDialogCity;


    public interface LocationRefreshInterface {
        public void onRefreshLocation();
    }

    static CityDialogFragment newInstance() {
        return new CityDialogFragment();
    }

    private ScrollView svLocation;
    private RecyclerView rvLocation;
    private RelativeLayout rlAddLocation;
    private EditText etAddLocation;
    private FloatingActionButton fabAddLocation;
    private FABProgressCircle fabAddLocationCircle;

    private RecyclerViewAdapterCities recyclerAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (LocationRefreshInterface) activity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_city, container, false);
        initialize(v);

        citiesList = db.getCitiesObjectList(false);
        recyclerAdapter = new RecyclerViewAdapterCities(getActivity(), this);

        if (citiesList.size() > 0) {
            Log.d("tag", "są miasta!");
            if (recyclerAdapter.cities.size() != citiesList.size()) {
                recyclerAdapter.cities.clear();
                for (int i = 0; i < citiesList.size(); i++) {
                    recyclerAdapter.addItem(new City(citiesList.get(i).getCityId(),
                            citiesList.get(i).getCityName(),
                            citiesList.get(i).getCityJSON(),
                            citiesList.get(i).getCityFlag()));
                }
            }
        } else {
            Log.d("tag", "nie ma miast!");
            rvLocation.setVisibility(View.GONE);
        }

        rvLocation.setAdapter(recyclerAdapter);
        rvLocation.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        rvLocation.setVisibility(citiesList.isEmpty() ? View.GONE : View.VISIBLE);


        // rvLocation.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        Toolbar toolbar = (Toolbar) v.findViewById(R.id.tb_location);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_location_dialog_save:
                        if (mCallback != null) {
                            mCallback.onRefreshLocation();
                        }
//                        rvLocation.setVisibility(db.getCitiesObjectList(false).size() > 0 ? View.VISIBLE : View.GONE);
                        dismiss();
                        break;
                    case R.id.action_location_dialog_reset:
                        db.resetCityTable();
                        if (mCallback != null) {
                            mCallback.onRefreshLocation();
                        }
                        citiesList.clear();
//                        rvLocation.setVisibility(db.getCitiesObjectList(false).size() > 0 ? View.VISIBLE : View.GONE);
                        dismiss();
//                        clearData(citiesList);
                        break;
                }
                return true;


            }
        });
        toolbar.inflateMenu(R.menu.menu_city_dialog);
        toolbar.setTitle(R.string.location);


        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }

    private void initialize(View v) {


//        svLocation = (ScrollView) v.findViewById(R.id.sv_location_filter);
        rvLocation = (RecyclerView) v.findViewById(R.id.rv_locations_list);
        etAddLocation = (EditText) v.findViewById(R.id.et_add_location);
        rlAddLocation = (RelativeLayout) v.findViewById(R.id.rl_add_location);
        fabAddLocation = (FloatingActionButton) v.findViewById(R.id.ib_add_location);
        fabAddLocation.setOnClickListener(this);

        fabAddLocationCircle = (FABProgressCircle) v.findViewById(R.id.fabProgressCircle);
        llDialogCity = (LinearLayout) v.findViewById(R.id.ll_dialog_city);

    }
/*
    public void clearData(ArrayList<City> list) {

        int size = list.size() - 1;

        Log.d("tag", getClass().getName() + " list.size: " + size);
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                list.remove(i);
                rvLocation.removeViewAt(i);
                recyclerAdapter.notifyItemRemoved(i);
                recyclerAdapter.notifyItemRangeChanged(i, list.size());
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.ib_add_location:


                String city = etAddLocation.getText().toString().replace(" ", "").trim();
                if (city.length()>0) {
                    fabAddLocationCircle.show();
                    processCityDataGETRequest(getActivity(), city);
                } else {
                    Snackbar.make(llDialogCity, R.string.typeCity, Snackbar.LENGTH_SHORT).show();
                }


                break;


        }


    }

    public static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private JSONObject json;

    public void processCityDataGETRequest(final Context context, String city) {

        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));

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
                                        Snackbar.make(llDialogCity, R.string.noSuchCity, Snackbar.LENGTH_SHORT).show();
                                        fabAddLocationCircle.hide();
                                        json = null;
                                    }
                                } else {
                                    Log.d("tag", getClass().getName() + json.toString());
                                    if (json != null) {
                                        Log.i("tag", getClass().getName() + json.toString());
                                        int cityID = json.getInt("id");
                                        if (!db.checkIfCityExistsAlready(cityID)) {
//                                            fabAddLocationCircle.beginFinalAnimation();
                                            fabAddLocationCircle.hide();
                                            Log.d("tag", getClass().getName() + " liczba miast w db: " + String.valueOf(db.getCitiesObjectList(false).size()));

                                            recyclerAdapter.addItem(new City(cityID, json.getString("name"), json.toString(), "1"));
                                            etAddLocation.setText("");

                                            String lat, lon;

                                            lat = json.getJSONObject("coord").getString("lat");
                                            lon = json.getJSONObject("coord").getString("lon");
                                            ;
                                            processPanoramioApi(lat, lon, cityID);
                                        } else {
                                            fabAddLocationCircle.hide();
                                            Snackbar.make(llDialogCity, R.string.cityAlreadyExists, Snackbar.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        fabAddLocationCircle.hide();
                                    }
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


   /* public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }


        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }*/


    @Override
    public void onDataSetChanged(int flag) {
        switch (flag) {
            case RecyclerViewAdapterCities.ADD_FLAG:
                rvLocation.setVisibility(View.VISIBLE);

                break;
            case RecyclerViewAdapterCities.REMOVE_FLAG:
                if (recyclerAdapter.cities.isEmpty()) {
                    rvLocation.setVisibility(View.GONE);
                }
                break;
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
                                Snackbar.make(llDialogCity, R.string.noPhotoInGoogle, Snackbar.LENGTH_SHORT).show();
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
                                            mCallback.onRefreshLocation();
                                        }

                                    }
                                } else {
                                    processPanoramioRandomPhotoApi(lat, lon, cityID, maxNumber);
                                }

                            } else {
                                Snackbar.make(llDialogCity, R.string.noPhotoInGoogle, Snackbar.LENGTH_SHORT).show();
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




   /*   May be used in future

   private void processGooglePlacesAPI(String lat, String lon, final int cityID) {
        Log.d("tag", getClass().getName() + "request URL  sent: " + new StringBuilder(Constants.GOOGLE_PLACES_REQUEST_URL).append(lat + "," + lon).toString());
        StringRequest processPlacePhoto = new StringRequest(Request.Method.GET, new StringBuilder(Constants.GOOGLE_PLACES_REQUEST_URL).append(lat + "," + lon).toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json;

                        try {
                            json = new JSONObject(response);
                            if (json.getString("status").matches("OK")) {

                                JSONArray array = json.getJSONArray("results");
                                JSONObject tempObj;
                                JSONArray photosArr;
                                String photoRef = null;
                                for (int i = 0; i < array.length(); i++) {
                                    tempObj=array.getJSONObject(i);
                                    photosArr = tempObj.optJSONArray("photos");
                                    if(photosArr != null){
                                        photoRef = photosArr.optJSONObject(0).getString("photo_reference");
                                        if(photoRef != null){
                                            Log.d("tag", getClass().getName() + "photo_reference: " + photoRef);
                                            db.updateCity(cityID, DatabaseHandler.getKeyPhotoReference(), photoRef);
                                            break;
                                        }
                                    }
                                }



                            }else{
                                Snackbar.make(llDialogCity, R.string.noPhotoInGoogle, Snackbar.LENGTH_SHORT).show();
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

    }*/
}