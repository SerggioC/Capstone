package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.sergiocruz.capstone.Repository;
import com.sergiocruz.capstone.model.User;

public class HomePageViewModel extends ViewModel {
    LiveData userLiveData;
    User userHere;

    public HomePageViewModel() {
        String result = Repository.userName;
        userHere = new User(
                null,
                "some user anonymous",
                null,
                null,
                null,
                null,
                true);
    }

    public User getUserHere() {
        return userHere;
    }

    public void setUserHere(User userHere) {
        this.userHere = userHere;
    }
}
