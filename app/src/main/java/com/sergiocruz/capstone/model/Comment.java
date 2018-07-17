package com.sergiocruz.capstone.model;

public class Comment {
    private String commentID;
    private String userID;
    private String content;
    private Long date;
    private int stars;
    private String userName;
    private String userImage;

    public Comment() {
    }

    public Comment(String commentID, String userID, String content, Long date, int stars, String userName, String userImage) {
        this.commentID = commentID;
        this.userID = userID;
        this.content = content;
        this.date = date;
        this.stars = stars;
        this.userName = userName;
        this.userImage = userImage;
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

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
