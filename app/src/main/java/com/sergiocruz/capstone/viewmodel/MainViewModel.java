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
    private static Repository repository;
    public User user;

    public MainViewModel(@NonNull Application application) {
        super(application);

        if (this.repository == null) {
            this.repository = Repository.getInstance(application.getApplicationContext());
        }
        
    }

    public LiveData<List<Travel>> getTravelPacks() {
        return repository.getTravelPacksLiveData();
    }

    public User getUser() {
        LiveData<User> user = repository.getUser();
        this.user = user.getValue();
        return user.getValue();
        //return this.user;
    }

    public LiveData<User> getUserLiveData() {
        LiveData<User> user = repository.getUser();
        return user;
    }



}
