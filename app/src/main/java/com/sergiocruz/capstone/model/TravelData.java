package com.sergiocruz.capstone.model;

public class TravelData {
    private Travel travel;
    private TravelStar star;
    private TravelComments comments;

    public TravelData(Travel travel, TravelStar star, TravelComments comments) {
        this.travel = travel;
        this.star = star;
        this.comments = comments;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public TravelStar getStar() {
        return star;
    }

    public void setStar(TravelStar star) {
        this.star = star;
    }

    public TravelComments getComments() {
        return comments;
    }

    public void setComments(TravelComments comments) {
        this.comments = comments;
    }
}
