package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

public class CommentsViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Comment>> commentListLiveData;

    public CommentsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRepository(Repository repository) {
        if (this.repository == null) {
            this.repository = repository;
        }
    }

    public LiveData<List<Comment>> getCommentsForTravelID(String travelID) {
        if (commentListLiveData == null)
            commentListLiveData = repository.getCommentsForTravelID(travelID);
        return commentListLiveData;
    }



}
