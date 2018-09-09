package com.phonedeals.ascom.phonestrore.data.model;

public class Chat {
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }



    public Chat() {

    }



    String name;

    public Chat(String id, String name, String from, String itemId) {
        this.name = name;
        this.itemId = itemId;
        this.id = id;
        this.from = from;
    }

    public String getItemId() {
        return itemId;
    }

    String itemId;

    String id;
    String from;

    public String getFrom() {
        return from;
    }
}
