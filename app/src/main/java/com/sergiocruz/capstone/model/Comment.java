package com.sergiocruz.capstone.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Comment {

    @NonNull
    @PrimaryKey
    private String commentID;
    private String userID;
    private String travelID;
    private String content;
    private Long date;
    private Float stars;
    private String userName;
    private String userImage;

    @Ignore
    public Comment() {
    }

    @Ignore
    public Comment(String commentID, String userID, String content, Long date, Float stars, String userName, String userImage) {
        this.commentID = commentID;
        this.userID = userID;
        this.content = content;
        this.date = date;
        this.stars = stars;
        this.userName = userName;
        this.userImage = userImage;
    }

    public Comment(String commentID, String userID, String travelID, String content, Long date, Float stars, String userName, String userImage) {
        this.commentID = commentID;
        this.userID = userID;
        this.travelID = travelID;
        this.content = content;
        this.date = date;
        this.stars = stars;
        this.userName = userName;
        this.userImage = userImage;
    }

    public String getTravelID() {
        return travelID;
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Float getStars() {
        return stars;
    }

    public void setStars(Float stars) {
        this.stars = stars;
    }
}
