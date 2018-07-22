package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelData;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {
    private static final Object LOCK = new Object();
    private int clickedPosition;
    private Repository repository;
    private LiveData<User> user;
    private String userID;
    private Travel selectedTravel;
    private LiveData<List<Travel>> travelList;
    private LiveData<List<Star>> travelStarsList;
    private LiveData<List<Long>> numCommentsList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        if (repository == null) {
            synchronized (LOCK) {
                if (repository == null) {
                    repository = Repository.getInstance(application.getApplicationContext());
                }
            }
        }
    }

    public LiveData<List<Travel>> getTravelPacks() {
        if (travelList == null) {
            travelList = repository.getTravelPacks();
        }
        Timber.i("getting travel packs");
        return travelList;
    }

    // rating and number of stars for each Travel
    public LiveData<List<Star>> getTravelStars() {
        if (travelStarsList == null) {
            travelStarsList = repository.getTravelStars();
        }
        return travelStarsList;
    }

    // number of comments for each travel
    public LiveData<List<Long>> getNumCommentsList() {
        if (numCommentsList == null) {
            numCommentsList = repository.getNumCommentsList();
        }
        return numCommentsList;
    }


    public LiveData<User> getUser() {
        if (user == null) {
            user = repository.getUser();
        }
        Timber.i("getting user");
        return user;
    }

    public void logoutUser() {
        this.user = null;
        this.userID = null;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Travel getSelectedTravel() {
        return selectedTravel;
    }

    public void setSelectedTravel(Travel selectedTravel) {
        this.selectedTravel = selectedTravel;
    }

    public int getClickedPosition() {
        return clickedPosition;
    }

    public void setClickedPosition(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }

    public Repository getRepository() {
        return repository;
    }

    private final MediatorLiveData<TravelData> mediatorLiveData = new MediatorLiveData<>();

    public LiveData<TravelData> getTravelData() {
//        LiveData<List<Travel>> travelsLD = getTravelPacks();
//        LiveData<List<Star>> travelStarsLD = getTravelStars();
//        LiveData<List<Long>> commentsListLD = getNumCommentsList();

        mediatorLiveData.addSource(getTravelPacks(), new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                mediatorLiveData.setValue(combineData(travels, getTravelStars().getValue(), getNumCommentsList().getValue()));
            }
        });

        mediatorLiveData.addSource(getTravelStars(), new Observer<List<Star>>() {
            @Override
            public void onChanged(@Nullable List<Star> stars) {
                mediatorLiveData.setValue(combineData(getTravelPacks().getValue(), stars, getNumCommentsList().getValue()));
            }
        });

        mediatorLiveData.addSource(getNumCommentsList(), new Observer<List<Long>>() {
            @Override
            public void onChanged(@Nullable List<Long> commentsList) {
                mediatorLiveData.setValue(combineData(getTravelPacks().getValue(), getTravelStars().getValue(), commentsList));
            }
        });

        return mediatorLiveData;
    }

    private TravelData combineData(List<Travel> travels, List<Star> travelStars, List<Long> commentsList) {
        return new TravelData(travels, travelStars, commentsList);
    }


}
