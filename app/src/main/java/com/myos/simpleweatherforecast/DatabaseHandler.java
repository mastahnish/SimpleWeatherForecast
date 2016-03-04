package com.myos.simpleweatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jacek on 2016-03-01.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "cities_db";

    private static final String TABLE_CITIES = "cities";

    private static String KEY_ID = "id";
    private static String KEY_CITY_ID = "city_id";
    private static String KEY_NAME = "city_name";
    private static String KEY_JSON_DATA = "json_data";
    private static String KEY_WANNA_SEE_FLAG = "wanna_see_flag";
    private static String KEY_PHOTO_REFERENCE = "photo_reference";

    private Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_CITIES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CITY_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_JSON_DATA + " TEXT,"
                + KEY_WANNA_SEE_FLAG + " TEXT,"
                + KEY_PHOTO_REFERENCE+ " TEXT )";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        onCreate(db);
    }

    public void insertCity(int cityID, String name, String json, String flag) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CITY_ID, cityID);
        values.put(KEY_NAME, name);
        values.put(KEY_JSON_DATA, json);
        values.put(KEY_WANNA_SEE_FLAG, flag);

        db.insert(TABLE_CITIES, null, values);
        db.close();
    }

     public void updateCity (int cityID, String columnName, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(columnName, newValue);

        db.update(TABLE_CITIES, value, KEY_CITY_ID + "=" + cityID, null);
        db.close();
    }

    public boolean deleteCity(int cityID){
        Log.d("tag", getClass().getName() + "cityID do usuniecia: " + cityID);
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted;
        deleted = db.delete(TABLE_CITIES,KEY_CITY_ID + " =?",new String[] {String.valueOf(cityID)});
        boolean result = (deleted==1);
        if(result)  Log.d("tag", getClass().getName() + "usunąłem JEDEN rekord" + deleted);
        if(!result)  Log.d("tag", getClass().getName() + "usunąłem wiecej niż JEDEN  lub 0 rekordow" + deleted);
        return result;
    }

    public int getFirstCityID(){//dummy function
        String selectQuery = "SELECT  * FROM " + TABLE_CITIES + " ORDER BY ROWID ASC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int citiID = 0;

        cursor.moveToFirst();
        if(cursor.getCount()>0){
            citiID = cursor.getInt(1);
        }
        cursor.close();
        db.close();
        return citiID;
    }

    public HashMap<String, String> getCityDetails(int cityID){
        HashMap<String, String> city = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CITIES + " WHERE " + KEY_CITY_ID + "= "+cityID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            city.put(KEY_CITY_ID, String.valueOf(cursor.getInt(1)));
            city.put(KEY_NAME, cursor.getString(2));
            city.put(KEY_JSON_DATA, cursor.getString(3));
            city.put(KEY_WANNA_SEE_FLAG, cursor.getString(4));
            city.put(KEY_PHOTO_REFERENCE, cursor.getString(5));
        }
        cursor.close();
        db.close();

        return city;
    }

    public void resetCityTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITIES, null, null);
        db.close();
    }

    public boolean checkIfCityTableExists(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CITIES, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }

        }
        return false;
    }


    public int getRowCount(String table) {
        String countQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    public ArrayList<City> getCitiesObjectList(boolean filtered){
        ArrayList<City> cities = new ArrayList<>();
        String selectQuery = (filtered) ? "SELECT  * FROM " + TABLE_CITIES + " WHERE " + KEY_WANNA_SEE_FLAG + "= 1" : "SELECT  * FROM " + TABLE_CITIES;



        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Integer[] cityID = new Integer[cursor.getCount()];
        String[] cityName = new String[cursor.getCount()];
        String[] cityJSON = new String[cursor.getCount()];
        String[] cityFLAG = new String[cursor.getCount()];

        City tempCity = null;
        int i = 0;
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                cityID[i] = cursor.getInt(1);
                cityName[i] = cursor.getString(2);
                cityJSON[i] = cursor.getString(3);
                cityFLAG[i] = cursor.getString(4);
                tempCity = new City(i, cityID[i], cityName[i], cityJSON[i], cityFLAG[i]);
                cities.add(i, tempCity);
                i++;
            }
        }
        cursor.close();
        db.close();
        return cities;
    }



    public boolean checkIfCityExistsAlready(int cityID){
        String selectQuery = "SELECT  * FROM " + TABLE_CITIES + " WHERE " +  KEY_CITY_ID  + "=" + cityID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            db.close();
            cursor.close();
            return false;
        } else {
            db.close();
            cursor.close();
            return true;
        }

    }

    public static String getKeyCityId() {
        return KEY_CITY_ID;
    }

    public static String getKeyId() {
        return KEY_ID;
    }

    public static String getKeyJsonData() {
        return KEY_JSON_DATA;
    }

    public static String getKeyName() {
        return KEY_NAME;
    }

    public static String getKeyWannaSeeFlag() {
        return KEY_WANNA_SEE_FLAG;
    }

    public static String getKeyPhotoReference() {
        return KEY_PHOTO_REFERENCE;
    }

    public static String getTableCities() {
        return TABLE_CITIES;
    }
}
