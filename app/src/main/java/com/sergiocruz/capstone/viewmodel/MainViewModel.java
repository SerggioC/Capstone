package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelComments;
import com.sergiocruz.capstone.model.TravelData;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.Repository;
import com.sergiocruz.capstone.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {
    private static final Object LOCK = new Object();
    private final MediatorLiveData<List<TravelData>> mediatorLiveData = new MediatorLiveData<>();
    private int clickedPosition;
    private Repository repository;
    private LiveData<User> user;
    private String userID;
    private Travel selectedTravel;
    private LiveData<List<Travel>> travelList;
    private LiveData<List<TravelStar>> travelStarsList;
    private LiveData<List<TravelComments>> numCommentsList;

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
    private LiveData<List<TravelStar>> getTravelStars() {
        if (travelStarsList == null) {
            travelStarsList = repository.getTravelStars();
        }
        return travelStarsList;
    }

    // number of comments for each travel
    private LiveData<List<TravelComments>> getNumCommentsList() {
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

    public LiveData<List<TravelData>> getTravelData() {

        mediatorLiveData.addSource(getTravelPacks(), new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                combineData(travels, getTravelStars().getValue(), getNumCommentsList().getValue());
            }
        });

        mediatorLiveData.addSource(getTravelStars(), new Observer<List<TravelStar>>() {
            @Override
            public void onChanged(@Nullable List<TravelStar> travelStars) {
                combineData(getTravelPacks().getValue(), travelStars, getNumCommentsList().getValue());
            }
        });

        mediatorLiveData.addSource(getNumCommentsList(), new Observer<List<TravelComments>>() {
            @Override
            public void onChanged(@Nullable List<TravelComments> commentsList) {
                combineData(getTravelPacks().getValue(), getTravelStars().getValue(), commentsList);
            }
        });

        return mediatorLiveData;
    }

    private void combineData(List<Travel> travels, List<TravelStar> travelTravelStars, List<TravelComments> commentsList) {
        if (travels == null || travelTravelStars == null || commentsList == null)
            return;
        new AppExecutors().diskIO().execute(() -> {
            int size = travels.size();
            List<TravelData> travelDataList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Travel travel = travels.get(i);
                String travelID = travel.getID();

                TravelStar travelStar = new TravelStar(0f, 0f, "");
                if (i < travelTravelStars.size()) {
                    TravelStar theTravelStar = travelTravelStars.get(i);
                    String starTravelID = theTravelStar.getTravelID();
                    if (travelID.equals(starTravelID)) {
                        travelStar = theTravelStar;
                    }
                }

                TravelComments travelComments = new TravelComments(0l, "");
                if (i < commentsList.size()) {
                    TravelComments thetravelComment = commentsList.get(i);
                    String commentTravelID = thetravelComment.getTravelID();
                    if (travelID.equals(commentTravelID)) {
                        travelComments = thetravelComment;
                    }
                }

                travelDataList.add(new TravelData(travel, travelStar, travelComments));
            }
            mediatorLiveData.postValue(travelDataList);
        });
    }


}














