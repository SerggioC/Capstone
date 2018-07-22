package com.sergiocruz.capstone.model;

public class TravelComments {
    Long number;
    String travelID;

    public TravelComments() {
    }

    public TravelComments(Long number, String travelID) {
        this.number = number;
        this.travelID = travelID;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getTravelID() {
        return travelID;
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }
}
