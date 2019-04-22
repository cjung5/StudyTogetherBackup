package com.studytogether.studytogether.Models;

import com.google.firebase.database.ServerValue;


public class User {

    private String userId,userImage,userName, userEmail;
    private Object timestamp;


    public User() {
    }

    public User(String userEmail, String userId, String userImage, String userName) {
        this.userEmail = userEmail;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserImage() {
        return userImage;
    }

    public void setuserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
