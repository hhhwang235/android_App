package com.example.select_placesapp;

import java.io.Serializable;

public class Location implements Serializable {
    //필드
    private int locationID;
    private int locUserID ;
    private String placeName;
    private double rate;
    private double lati;
    private double longi;
    private String addressName ;
    private String type;

    //생성자
    public Location(int locID, int userID, String pName, double rate, double latitude, double longitude,String addName,String type){
        this.locationID = locID;
        this.locUserID = userID;
        this.placeName = pName;
        this.rate = rate;
        this.lati = latitude;
        this.longi = longitude;
        this.addressName = addName;
        this.type = type;
    }

    //Getter, Setter 메서드 정의해둔것.
    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public int getLocUserID() {
        return locUserID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
