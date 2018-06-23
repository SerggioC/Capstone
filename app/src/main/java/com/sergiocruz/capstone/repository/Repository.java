package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.sergiocruz.capstone.model.User;

public class Repository implements FirebaseRepository.ValueListener{
    private static Repository sInstance;
    private final FirebaseRepository remoteRepository;
    private final LocalRepository localRepository;

    private LiveData<User> user;

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

    public LiveData<User> getUser() {
        return remoteRepository.getUser(this);
    }

    @Override
    public void onValueEvent(DataSnapshot dataSnapshot) {

    }

}
