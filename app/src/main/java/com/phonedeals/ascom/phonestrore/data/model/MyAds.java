package com.phonedeals.ascom.phonestrore.data.model;

public class MyAds {

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

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    private String visible;



    private String numberView;

    public String getNumberView() {
        return numberView;
    }

    public void setNumberView(String numberView) {
        this.numberView = numberView;
    }

    public String getPhoneImage() {
        return phoneImage;
    }

    public void setPhoneImage(String phoneImage) {
        this.phoneImage = phoneImage;
    }

    private String phoneImage;

    public MyAds(String id,String phoneName, String phoneCity, String phoneStatus, String numberOrder,String phoneImage,String visible) {
        this.id = id;
        this.phoneName = phoneName;
        this.phoneCity = phoneCity;
        this.phoneStatus = phoneStatus;
        this.numberView = numberOrder;
        this.phoneImage = phoneImage;
        this.visible=visible;
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

    public MyAds() {
    }
}
