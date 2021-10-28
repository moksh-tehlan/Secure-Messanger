package com.moksh.securemessenger.Models;

public class ChatHistory {
    private String message,uid,username;

    public ChatHistory(String message, String uid, String username) {
        this.message = message;
        this.uid = uid;
        this.username = username;
    }

    public ChatHistory() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
