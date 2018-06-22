package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.repository.Repository;
import com.sergiocruz.capstone.model.User;

public class HomePageViewModel extends AndroidViewModel {
    Repository repository;
    LiveData userLiveData;
    User user;

    public HomePageViewModel(@NonNull Application application) {
        super(application);

        if (this.repository != null) {
            // ViewModel is created per Activity, so instantiate once
            // we know the userId won't change
            return;
        }
        if (repository == null) {
            this.repository = Repository.getInstance();
        }


        String result = Repository.userName;
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
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
