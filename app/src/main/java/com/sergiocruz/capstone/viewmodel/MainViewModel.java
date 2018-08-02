package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Status;
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

import static com.sergiocruz.capstone.model.Status.LOADING;
import static com.sergiocruz.capstone.model.Status.PROCESSING;
import static com.sergiocruz.capstone.model.Status.SUCCESS;

public class MainViewModel extends AndroidViewModel {
    private static final Object LOCK = new Object();
    private int clickedPosition;
    private Repository repository;
    private LiveData<User> user;
    private MutableLiveData<String> userName = new MutableLiveData<>();
    private String userID;
    private Travel selectedTravel;
    private LiveData<List<Travel>> travelList;
    private LiveData<List<TravelStar>> travelStarsList;
    private LiveData<List<TravelComments>> numCommentsList;
    private MediatorLiveData<List<TravelData>> mediatorLiveData;
    private MutableLiveData<Status> currentStatus = new MutableLiveData<>();
    private String anonymUserString;
    private LiveData<List<Travel>> favoriteTravelList;
    private MediatorLiveData<List<TravelData>> favoritesMediatorLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        if (repository == null) {
            synchronized (LOCK) {
                if (repository == null) {
                    repository = Repository.getInstance(application.getApplicationContext());
                    anonymUserString = application.getApplicationContext().getString(R.string.anonymous_user);
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
        return user;
    }

    private void updateUser(User user) {
        if (user == null || user.getUserName() == null) {
            userName.setValue(anonymUserString);
        } else {
            userName.setValue(user.getUserName());
        }
    }

    public MutableLiveData<String> getUserName() {
        getUser().observeForever(this::updateUser);
        return userName;
    }

    public MutableLiveData<Status> getCurrentStatus() {
        return currentStatus;
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

    public LiveData<List<Travel>> getFavoriteTravelPacks() {
        if (favoriteTravelList == null) {
            favoriteTravelList = repository.getFavoriteTravelPacks();
        }
        return favoriteTravelList;
    }

    public LiveData<List<TravelData>> getTravelData() {
        if (mediatorLiveData != null)
            return mediatorLiveData;

        currentStatus.setValue(LOADING);;

        mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(getTravelPacks(), travels ->
                combineData(mediatorLiveData,
                        travels,
                        getTravelStars().getValue(),
                        getNumCommentsList().getValue()));

        mediatorLiveData.addSource(getTravelStars(), travelStars ->
                combineData(mediatorLiveData,
                        getTravelPacks().getValue(),
                        travelStars,
                        getNumCommentsList().getValue()));

        mediatorLiveData.addSource(getNumCommentsList(), commentsList ->
                combineData(mediatorLiveData,
                        getTravelPacks().getValue(),
                        getTravelStars().getValue(),
                        commentsList));

        return mediatorLiveData;
    }

    public LiveData<List<TravelData>> getFavoriteTravelData() {
        if (favoritesMediatorLiveData != null)
            return favoritesMediatorLiveData;

        currentStatus.setValue(LOADING);;

        favoritesMediatorLiveData = new MediatorLiveData<>();
        favoritesMediatorLiveData.addSource(getFavoriteTravelPacks(), travels ->
                combineData(favoritesMediatorLiveData,
                        travels, getTravelStars().getValue(),
                        getNumCommentsList().getValue()));

        favoritesMediatorLiveData.addSource(getTravelStars(), travelStars ->
                combineData(favoritesMediatorLiveData,
                        getFavoriteTravelPacks().getValue(),
                        travelStars,
                        getNumCommentsList().getValue()));

        favoritesMediatorLiveData.addSource(getNumCommentsList(), commentsList ->
                combineData(favoritesMediatorLiveData,
                        getFavoriteTravelPacks().getValue(),
                        getTravelStars().getValue(),
                        commentsList));

        return favoritesMediatorLiveData;
    }

    private void combineData(MediatorLiveData mediator, List<Travel> travels, List<TravelStar> travelStars, List<TravelComments> commentsList) {
        if (travels == null || travelStars == null || commentsList == null)
            return;

        currentStatus.setValue(PROCESSING);

        new AppExecutors().diskIO().execute(() -> {
            int size = travels.size();
            List<TravelData> travelDataList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Travel travel = travels.get(i);
                String travelID = travel.getID();

                TravelStar travelStar = new TravelStar(0f, 0f, "");
                starLoop:
                for (int j = 0; j < travelStars.size(); j++) {
                    TravelStar theTravelStar = travelStars.get(j);
                    String starTravelID = theTravelStar.getTravelID();
                    if (travelID.equals(starTravelID)) {
                        travelStar = theTravelStar;
                        break starLoop;
                    }
                }

                TravelComments travelComments = new TravelComments(0L, "");
                commentLoop:
                for (int k = 0; k < commentsList.size(); k++) {
                    TravelComments theTravelComment = commentsList.get(k);
                    String commentTravelID = theTravelComment.getTravelID();
                    if (travelID.equals(commentTravelID)) {
                        travelComments = theTravelComment;
                        break commentLoop;
                    }
                }

                travelDataList.add(new TravelData(travel, travelStar, travelComments));
            }
            mediator.postValue(travelDataList);

            currentStatus.postValue(SUCCESS);
        });
    }

}














