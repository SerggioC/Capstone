package com.sergiocruz.capstone.model;

public class User {
    String userID;
    String userName;
    String userPhotoUri;
    String userEmail;
    String userPhoneNumber;
    String authProvider;
    boolean isAnonymous;

    public User(String userID, String userName, String userPhotoUri, String userEmail, String userPhoneNumber, String authProvider, boolean isAnonymous) {
        this.userID = userID;
        this.userName = userName;
        this.userPhotoUri = userPhotoUri;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.authProvider = authProvider;
        this.isAnonymous = isAnonymous;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    public void setUserPhotoUri(String userPhotoUri) {
        this.userPhotoUri = userPhotoUri;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public boolean getIsAnonymous() {
        return this.isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.isAnonymous = anonymous;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhotoUri=" + userPhotoUri +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", authProvider='" + authProvider + '\'' +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
