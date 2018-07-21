package com.sergiocruz.capstone.model;

public class Star {
    Float rating;
    Long number;

    public Star() {
    }

    public Star(Float rating, Long number) {
        this.rating = rating;
        this.number = number;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
