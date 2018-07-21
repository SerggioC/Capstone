package com.sergiocruz.capstone.model;

public class TravelStar {
    private String userID;
    private Float value;
    private String commentID;
    private String travelID;

    public TravelStar() {
    }

    public TravelStar(Float value, String travelID, String commentID, String userID) {
        this.value = value;
        this.travelID = travelID;
        this.commentID = commentID;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getTravelID() {
        return travelID;
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }
}
