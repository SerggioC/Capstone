package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class Repository implements FirebaseRepository.ValueListener{
    private static Repository sInstance;
    private final FirebaseRepository remoteRepository;
    private final LocalRepository localRepository;

    private LiveData<User> user;
    private LiveData<List<Travel>> travelPacks;

    private Repository(FirebaseRepository remoteRepository, LocalRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
        travelPacks = remoteRepository.getTravelPacksLiveData();
    }

    public FirebaseRepository getRemoteRepository() {
        return remoteRepository;
    }

    public LocalRepository getLocalRepository() {
        return localRepository;
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

    public LiveData<User> getUser() {
        return remoteRepository.getUser(this);
    }


    @NonNull
    public LiveData<List<Travel>> getTravelPacksLiveData() {
        return travelPacks;
    }


    @Override
    public void onValueEvent(DataSnapshot dataSnapshot) {

    }

}
