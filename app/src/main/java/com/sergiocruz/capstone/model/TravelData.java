package com.sergiocruz.capstone.model;

import java.util.List;

public class TravelData {
    private List<Travel> travel;
    private List<Star> star;
    private List<Long> numComments;

    public TravelData(List<Travel> travel, List<Star> star, List<Long> numComments) {
        this.travel = travel;
        this.star = star;
        this.numComments = numComments;
    }

    public List<Travel> getTravel() {
        return travel;
    }

    public void setTravel(List<Travel> travel) {
        this.travel = travel;
    }

    public List<Star> getStar() {
        return star;
    }

    public void setStar(List<Star> star) {
        this.star = star;
    }

    public List<Long> getNumComments() {
        return numComments;
    }

    public void setNumComments(List<Long> numComments) {
        this.numComments = numComments;
    }
}
