package com.phonedeals.ascom.phonestrore.data.model;

public class MyOrderPrice {

    private String phoneName;
    private String phoneCity;
    private String phoneStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    public String getNumberOrder() {
        return numberOrder;
    }

    public void setNumberOrder(String numberOrder) {
        this.numberOrder = numberOrder;
    }

    private String numberOrder;

    public MyOrderPrice(String id, String phoneName, String phoneCity, String phoneStatus,String numberOrder) {
        this.id = id;
        this.phoneName = phoneName;
        this.phoneCity = phoneCity;
        this.phoneStatus = phoneStatus;
        this.numberOrder = numberOrder;
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

    public MyOrderPrice() {
    }
}
