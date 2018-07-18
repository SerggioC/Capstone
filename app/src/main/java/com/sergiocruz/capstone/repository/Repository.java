package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class Repository {
    private static Repository sInstance;
    private FirebaseRepository remoteRepository;
    private LocalRepository localRepository;

    private Repository(FirebaseRepository remoteRepository, LocalRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
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

    public LocalRepository getLocalRepository() {
        return localRepository;
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


}
