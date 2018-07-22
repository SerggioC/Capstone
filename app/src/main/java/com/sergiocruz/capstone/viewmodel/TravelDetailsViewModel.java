package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

public class TravelDetailsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Comment>> commentListLiveData;
    private LiveData<Long> numComments;
    private Travel travel;
    private LiveData<Star> travelStarsLiveData;

    public TravelDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRepository(Repository repository) {
        if (this.repository == null) {
            this.repository = repository;
        }
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public LiveData<List<Comment>> getCommentsForTravelID() {
        if (commentListLiveData == null)
            commentListLiveData = repository.getCommentsForTravelID(getTravel().getID());
        return commentListLiveData;
    }

    public Travel getTravel() {
        return travel;
    }

    public LiveData<Star> getStars() {
        if (travelStarsLiveData == null) {
            travelStarsLiveData = repository.getStarsForTravelID(getTravel().getID());
        }
        return travelStarsLiveData;
    }

    public LiveData<Long> getNumComments() {
        if (numComments == null) {
            numComments = repository.getNumCommentsForTravelID(getTravel().getID());
        }
        return numComments;
    }

}
