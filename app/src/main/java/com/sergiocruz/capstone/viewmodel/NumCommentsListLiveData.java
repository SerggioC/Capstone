package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.TravelComments;
import com.sergiocruz.capstone.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACK_COMMENTS_REF;

public class NumCommentsListLiveData extends LiveData<List<TravelComments>> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public NumCommentsListLiveData(Query query) {
        this.query = query;
    }

    public NumCommentsListLiveData(DatabaseReference databaseReference) {
        databaseReference = databaseReference.child(TRAVEL_PACK_COMMENTS_REF);
        this.query = databaseReference;
    }

    @Override
    protected void onActive() {
        Timber.d("onActive");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Timber.d("onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            new AppExecutors().networkIO().execute(() -> {
                List<TravelComments> numCommentsList = new ArrayList<>();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                        String travelID = snapshotChild.getKey();
                        Long count = snapshotChild.hasChildren() ? snapshotChild.getChildrenCount() : 0L;
                        numCommentsList.add(new TravelComments(count, travelID));
                    }
                }

                postValue(numCommentsList);
            });
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }

}