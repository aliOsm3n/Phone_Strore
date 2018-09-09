package com.phonedeals.ascom.phonestrore.data.model;

public class MostView {

    public MostView() {
    }

    public MostView(String id, String photo, String views) {
        this.id = id;
        this.photo = photo;
        this.views = views;
    }

    String id;
    String photo;
    String views;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }
}

