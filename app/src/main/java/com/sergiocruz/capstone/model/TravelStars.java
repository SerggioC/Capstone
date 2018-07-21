package com.sergiocruz.capstone.model;

public class TravelStars {
    private Float rating;
    private Long count;
    private String commentID;
    private String travelID;

    public TravelStars() {
    }

    public TravelStars(Float rating, Long count, String commentID, String travelID) {
        this.rating = rating;
        this.count = count;
        this.commentID = commentID;
        this.travelID = travelID;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
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
