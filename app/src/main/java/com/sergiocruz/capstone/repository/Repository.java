package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.database.DatabaseDAO;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelComments;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class Repository {
    private static Repository sInstance;
    private FirebaseRepository remoteRepository;
    private DatabaseDAO localRepositoryDAO;

    private Repository(FirebaseRepository remoteRepository, LocalRepository localRepositoryDAO) {
        this.remoteRepository = remoteRepository;
        this.localRepositoryDAO = LocalRepository.getDatabaseDAO();
    }

    // Broken multithreaded version
    // Double-Checked Locking
    public static Repository getInstance(Context applicationContext) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository(FirebaseRepository.getInstance(), LocalRepository.getInstance(applicationContext));
                }
            }
        }
        return sInstance;
    }

    public FirebaseRepository getRemoteRepository() {
        return remoteRepository;
    }

    public DatabaseDAO getLocalRepositoryDAO() {
        return localRepositoryDAO;
    }

    public MutableLiveData<User> getUser() {
        return remoteRepository.getUser();
    }

    @NonNull
    public MutableLiveData<List<Travel>> getTravelPacks() {
        return remoteRepository.getTravelPacks();
    }

    @NonNull
    public MutableLiveData<List<Comment>> getCommentsForTravelID(String travelID) {
        return remoteRepository.getCommentsForTravelID(travelID);
    }

    @NonNull
    public MutableLiveData<Long> getNumCommentsForTravelID(String travelID) {
        return remoteRepository.getNumCommentsForTravelID(travelID);
    }

    public Comment getBackedUpCommentByID(String commentID) {
        return localRepositoryDAO.getCommentByID(commentID);
    }

    public void backUpComment(Comment comment) {
        localRepositoryDAO.saveComment(comment);
    }

    public void deleteBackedUpComment(String commentID) {
        localRepositoryDAO.deleteCommentByID(commentID);
    }

    public MutableLiveData<TravelStar> getStarsForTravelID(String travelID) {
        return remoteRepository.getStarsForTravelID(travelID);
    }

    public MutableLiveData<List<TravelStar>> getTravelStars() {
        return remoteRepository.getTravelStars();
    }

    public MutableLiveData<List<TravelComments>> getNumCommentsList() {
        return remoteRepository.getNumCommentsList();
    }

    public MutableLiveData<List<Travel>> getFavoriteTravelPacks() {
        return remoteRepository.getFavoriteTravelPacks();
    }

    public Boolean saveTravelToFavorites(Travel travel) {
        return remoteRepository.saveTravelToFavorites(travel);
    }

    public void removeTravelFromFavorites(String travelID) {
        remoteRepository.removeTravelFromFavorites(travelID);
    }
}
