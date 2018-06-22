package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.repository.Repository;
import com.sergiocruz.capstone.model.User;

public class HomePageViewModel extends AndroidViewModel {
    private Repository repository;
    LiveData userLiveData;
    User user;

    public HomePageViewModel(@NonNull Application application) {
        super(application);

        if (this.repository == null) {
            this.repository = Repository.getInstance(application.getApplicationContext());
        }

        user = new User(
                null,
                "some user anonymous",
                null,
                null,
                null,
                null,
                true);
    }

    public User getUser() {
        return repository.getUser();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
