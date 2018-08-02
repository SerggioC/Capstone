package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Status;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

public class TravelDetailsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Comment>> commentListLiveData;
    private LiveData<Long> numComments;
    private Travel travel;
    private LiveData<TravelStar> travelStarsLiveData;
    private MutableLiveData<Status> currentStatus = new MutableLiveData<>();

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
        if (commentListLiveData == null) {
            commentListLiveData = repository.getCommentsForTravelID(getTravel().getID());
        }
        return commentListLiveData;
    }

    public Travel getTravel() {
        return travel;
    }

    public LiveData<TravelStar> getStars() {
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

    public LiveData<Status> getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(Status currentStatus) {
        this.currentStatus.setValue(currentStatus);
    }

    public Boolean saveToFavorites(Travel selectedTravel) {
        return repository.saveTravelToFavorites(selectedTravel);
    }

    public void removeFromFavorites(String travelID) {
        repository.removeTravelFromFavorites(travelID);
    }

}
