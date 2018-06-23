package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.Repository;

public class HomePageViewModel extends AndroidViewModel {
    private Repository repository;

    public ObservableField<User> user = new ObservableField<>();

    public HomePageViewModel(@NonNull Application application) {
        super(application);

        if (this.repository == null) {
            this.repository = Repository.getInstance(application.getApplicationContext());
        }

    }

    public User getUser() {
        LiveData<User> user = repository.getUser();
        return user.getValue();
    }

    public LiveData<User> getUserLiveData() {
        LiveData<User> user = repository.getUser();
        return user;
    }

    public void setUser(User user) {
        this.user.set(user);
    }
}
