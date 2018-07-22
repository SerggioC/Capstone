package com.sergiocruz.capstone.model;
/** Float rating;
 Float number;
 String travelID; */
public class TravelStar {
    Float rating;
    Float number;
    String travelID;

    public TravelStar() {
    }

    public TravelStar(Float rating, Float number, String travelID) {
        this.rating = rating;
        this.number = number;
        this.travelID = travelID;
    }

    public String getTravelID() {
        return travelID;
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Float getNumber() {
        return number;
    }

    public void setNumber(Float number) {
        this.number = number;
    }
}
