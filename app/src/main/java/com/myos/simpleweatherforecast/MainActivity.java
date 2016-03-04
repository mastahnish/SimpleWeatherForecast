package com.myos.simpleweatherforecast;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.LocationRefreshInterface {


    private DatabaseHandler db;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private ArrayList<City> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D000000")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D000000")));*/

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);

     /*   if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }
*/
        initialize();

//        Bundle bundle = getIntent().getExtras();


        if (db.checkIfCityTableExists()) {
            cities = db.getCitiesObjectList(true); //Only those with "1" flag
            mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), cities);
            mViewPager.setAdapter(mPagerAdapter);
           /* if(bundle!=null){
                Log.d("tag", "bundle nie jest nulll");
                int cityIDFromWidget = bundle.getInt(WidgetProvider.WIDGET_INTENT_OBJECT_KEY);
                Log.d("tag", String.valueOf(cityIDFromWidget));
                Log.d("tag", String.valueOf(cities.indexOf(findCityObjByCityID(cityIDFromWidget))));
                mViewPager.setCurrentItem(cities.indexOf(findCityObjByCityID(cityIDFromWidget)));
            }*/
        } else {
            mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), new ArrayList<City>());
            mViewPager.setAdapter(mPagerAdapter);
        }

        Log.d("tag", "uruchomiłem onCreate");


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = getIntent().getExtras();
        if (db.checkIfCityTableExists()) {
            cities = db.getCitiesObjectList(true); //Only those with "1" flag
            mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), cities);
            mViewPager.setAdapter(mPagerAdapter);
            if (bundle != null) {
                Log.d("tag", "bundle nie jest nulll");
                int cityIDFromWidget = bundle.getInt(WidgetProvider.WIDGET_INTENT_OBJECT_KEY);
                Log.d("tag", String.valueOf(cityIDFromWidget));
                Log.d("tag", String.valueOf(cities.indexOf(findCityObjByCityID(cityIDFromWidget))));
                mViewPager.setCurrentItem(cities.indexOf(findCityObjByCityID(cityIDFromWidget)));
            }
        }

    }

    private City findCityObjByCityID(int cityID) {
        City c = null;
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getCityId() == cityID) {
                c = cities.get(i);
            }
        }
        return c;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tag", "uruchomiłem onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tag", "uruchomiłem onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.change_city:
                showInputActivity();
                break;
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this,
                        PreferenceActivity.class);
                startActivityForResult(i, 1);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        mViewPager = (ViewPager) findViewById(R.id.vp_main);

    }

    private void showInputActivity() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = null;
        DialogFragment newFragment = null;

        prev = getSupportFragmentManager().findFragmentByTag(
                "dialog_location");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment = CityDialogFragment.newInstance();
        newFragment.show(ft, "dialog_location");
    }

  /*  public void changeCity(int cityID){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(cityID);
//       new CityPreference(this).setCity(cityID);
    }*/

    @Override
    public void onRefreshLocation() {
//        changeCity(db.getFirstCityID());

        cities = db.getCitiesObjectList(true); //Only those with "1" flag
        PagerAdapter.cities = cities;
        mPagerAdapter.notifyDataSetChanged();
/*        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), cities);
        mViewPager.setAdapter(mPagerAdapter);*/
    }


}
