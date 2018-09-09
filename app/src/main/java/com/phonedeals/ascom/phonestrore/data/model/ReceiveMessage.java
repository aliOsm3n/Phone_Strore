package com.phonedeals.ascom.phonestrore.data.model;

public class ReceiveMessage {
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public ReceiveMessage(String json) {
        this.json = json;
    }
}
