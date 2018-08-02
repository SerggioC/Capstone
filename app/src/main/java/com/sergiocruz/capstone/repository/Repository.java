package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
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

    private Repository(FirebaseRepository remoteRepository, DatabaseDAO localRepositoryDAO) {
        this.remoteRepository = remoteRepository;
        this.localRepositoryDAO = localRepositoryDAO;
    }

    // Broken multithreaded version
    // Double-Checked Locking
    public static Repository getInstance(Context applicationContext) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository(FirebaseRepository.getInstance(), LocalRepository.getInstance(applicationContext).getDatabaseDAO());
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

    public LiveData<User> getUser() {
        return remoteRepository.getUser();
    }

    @NonNull
    public LiveData<List<Travel>> getTravelPacks() {
        return remoteRepository.getTravelPacks();
    }

    @NonNull
    public LiveData<List<Comment>> getCommentsForTravelID(String travelID) {
        return remoteRepository.getCommentsForTravelID(travelID);
    }

    @NonNull
    public LiveData<Long> getNumCommentsForTravelID(String travelID) {
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

    public LiveData<TravelStar> getStarsForTravelID(String travelID) {
        return remoteRepository.getStarsForTravelID(travelID);
    }

    public LiveData<List<TravelStar>> getTravelStars() {
        return remoteRepository.getTravelStars();
    }

    public LiveData<List<TravelComments>> getNumCommentsList() {
        return remoteRepository.getNumCommentsList();
    }

    public LiveData<List<Travel>> getFavoriteTravelPacks() {
        return remoteRepository.getFavoriteTravelPacks();
    }

    public Boolean saveTravelToFavorites(Travel travel) {
        return remoteRepository.saveTravelToFavorites(travel);
    }

    public void removeTravelFromFavorites(String travelID) {
        remoteRepository.removeTravelFromFavorites(travelID);
    }
}
