package com.phonedeals.ascom.phonestrore.data.model;

public class ChatRoom {
    String from;

    public ChatRoom(String id, String chat_id, String from, String time, String content,String photo) {
        this.from = from;
        this.chat_id = chat_id;
        this.id = id;
        this.time = time;
        this.content = content;
        this.photo = photo;
    }

    public ChatRoom() {
    }

    String chat_id;
    String id;

    public String getPhoto() {
        return photo;
    }

    public String getChat_id() {
        return chat_id;
    }

    String photo;

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    String time;
    String content;
}
