package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final Object LOCK = new Object();
    private Repository repository;
    private LiveData<List<Travel>> travelList;
    private LiveData<User> user;

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
        return travelList;
    }

    public LiveData<User> getUser() {
        if (user == null) {
            user = repository.getUser();
        }
        return user;
    }

}
