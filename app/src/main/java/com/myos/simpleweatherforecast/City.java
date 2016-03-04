package com.myos.simpleweatherforecast;

import java.io.Serializable;

/**
 * Created by Jacek on 2016-03-01.
 */
public class City implements Serializable{

    private int id;
    private int cityId;
    private String cityName;
    private String cityJSON;
    private String cityFlag;


    public City(int id, int cityId,String cityName,  String cityJSON, String cityFlag) {
        this.id = id;
        this.cityId = cityId;
        this.cityJSON = cityJSON;
        this.cityName = cityName;
        this.cityFlag = cityFlag;
    }

    public City(int cityId,String cityName,  String cityJSON, String cityFlag) {
        this.cityId = cityId;
        this.cityJSON = cityJSON;
        this.cityName = cityName;
        this.cityFlag = cityFlag;
    }

    public String getCityFlag() {
        return cityFlag;
    }

    public void setCityFlag(String cityFlag) {
        this.cityFlag = cityFlag;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityJSON() {
        return cityJSON;
    }

    public void setCityJSON(String cityJSON) {
        this.cityJSON = cityJSON;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
