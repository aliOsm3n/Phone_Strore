package com.phonedeals.ascom.phonestrore.data.model;

public class ShowPrice {

    private String phoneName;
    private String phoneCity;
    private String phoneStatus;
    private String id;


    public String getPhonePrice() {
        return phonePrice;
    }

    public void setPhonePrice(String phonePrice) {
        this.phonePrice = phonePrice;
    }

    private String phonePrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String lat;
    private String lng;
    private String number;

    public ShowPrice(String id,String phoneName, String phoneCity, String phoneStatus, String price, String lat, String lng, String number) {
        this.id = id;
        this.phoneName = phoneName;
        this.phoneCity = phoneCity;
        this.phoneStatus = phoneStatus;
        this.phonePrice=price;
        this.lat = lat;
        this.lng = lng;
        this.number=number;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneCity() {
        return phoneCity;
    }

    public void setPhoneCity(String phoneCity) {
        this.phoneCity = phoneCity;
    }

    public String getPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public ShowPrice() {
    }
}
