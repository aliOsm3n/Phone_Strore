package com.phonedeals.ascom.phonestrore.data.model;

public class OrderPrice {

    private String id;
    private String phoneName;
    private String phoneCity;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String phoneStatus;
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderPrice(String id, String phoneName, String phoneCity, String phoneStatus,String userId) {
        this.id = id;
        this.phoneName = phoneName;
        this.phoneCity = phoneCity;
        this.phoneStatus = phoneStatus;
        this.userId = userId;
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

    public OrderPrice() {
    }
}
