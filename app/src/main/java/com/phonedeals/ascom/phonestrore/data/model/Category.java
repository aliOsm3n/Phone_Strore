package com.phonedeals.ascom.phonestrore.data.model;

public class Category {

    private String phoneName;
    private String phoneCity;
    private String phoneStatus;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String addTime;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getPhonePrice() {
        return phonePrice;
    }

    public void setPhonePrice(String phonePrice) {
        this.phonePrice = phonePrice;
    }

    private String phonePrice;



    public String getPhoneImage() {
        return phoneImage;
    }

    public void setPhoneImage(String phoneImage) {
        this.phoneImage = phoneImage;
    }

    private String phoneImage;

    public Category(String id,String phoneName, String phoneCity, String phoneStatus, String addTime, String phoneImage,String price) {
        this.id = id;
        this.phoneName = phoneName;
        this.phoneCity = phoneCity;
        this.phoneStatus = phoneStatus;
        this.addTime = addTime;
        this.phoneImage = phoneImage;
        this.phonePrice=price;
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

    public Category() {
    }
}
