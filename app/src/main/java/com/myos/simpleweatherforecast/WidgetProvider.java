package com.myos.simpleweatherforecast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jacek on 2016-03-02.
 */
public class WidgetProvider extends AppWidgetProvider {


    private String TRIGGER = "trigger";

    private ArrayList<City> cities = new ArrayList<>();
    private DatabaseHandler db;
    private Bitmap iconBitmap;
    private int cityPosition;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int count = appWidgetIds.length;

        db = new DatabaseHandler(context);
        if (db.checkIfCityTableExists()) {
            cities = db.getCitiesObjectList(true);
        }

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            try {
                Random r = new Random();
                Double temperature = 0.00;
                String name = "";
                int id = 0;

                if (cities.size() > 0) {

                    cityPosition = r.nextInt(cities.size());
                    Log.d("tag", "WidgetProvider cityPosition1: " + cityPosition);
                    JSONObject json = new JSONObject(cities.get(cityPosition).getCityJSON());
                    JSONObject main = json.getJSONObject("main");

                    temperature = main.getDouble("temp");
                    name = json.getString("name");
                    id = json.getInt("id");

                }

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget_layout);
                remoteViews.setTextViewText(R.id.tv_wiget_city_temperature, (cities.size() > 0) ? String.format("%.2f", temperature) + " ℃" : "");
                remoteViews.setTextViewText(R.id.tv_widget_city_name, (cities.size() > 0) ? name : "Choose locations");

                remoteViews.setOnClickPendingIntent(R.id.b_widget_refresh, getRefreshIntent(context, widgetId, appWidgetIds, R.id.b_widget_refresh));
                remoteViews.setOnClickPendingIntent(R.id.fl_widget_main, getGoToActivityIntent(context, (cities.size() > 0) ? id : 0/*cities.get(cityPosition).getCityId()*/));
                Log.d("tag", "WidgetProvider cityPosition2: " + cityPosition);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);

            } catch (JSONException e) {
                Log.d("tag", getClass().getName() + " JSON error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public static String WIDGET_INTENT_OBJECT_KEY = "widget_intent_object_key";

    protected PendingIntent getGoToActivityIntent(Context context, int cityID) {
        Log.d("tag", "WidgetPRovider Wysyłam cityID: " + cityID);
        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.putExtra(WIDGET_INTENT_OBJECT_KEY, cityID);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

        return configPendingIntent;

    }

    protected PendingIntent getRefreshIntent(Context context, int widgetID, int[] appWidgetIds, final int id) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

   /* @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        Integer id = (Integer) (extras == null ? null : extras.get(TRIGGER));
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action) && id != null) {
            int[] widgetIDs = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            for (int i = 0; i < widgetIDs.length; i++) {
                int widgetID = widgetIDs[i];
                onNavigate(context, widgetID, id);
            }
        } *//*else {
            super.onReceive(context, intent);
        }*//*
    }


    protected void onNavigate(Context context, Integer widgetID, Integer id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (id == R.id.left) {
            remoteViews.showPrevious(R.id.scroll);
        } else {
            remoteViews.showNext(R.id.scroll);
        }
        appWidgetManager.updateAppWidget(widgetID, remoteViews);
    }*/

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context, R.string.widgetRemoved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Toast.makeText(context, R.string.widgetEnabled, Toast.LENGTH_SHORT).show();
    }
}
