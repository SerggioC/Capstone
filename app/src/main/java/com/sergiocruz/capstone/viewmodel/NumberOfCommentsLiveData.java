package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class NumberOfCommentsLiveData extends LiveData<Long> {
    private static final String NODE_REFERENCE = "travel-pack-comments";
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public NumberOfCommentsLiveData(Query query) {
        this.query = query;
    }

    public NumberOfCommentsLiveData(DatabaseReference databaseReference, String travelID) {
        databaseReference = databaseReference.child(NODE_REFERENCE).child(travelID);
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
            Long commentCount = 0L;
            if (dataSnapshot.hasChildren()) {
                commentCount = dataSnapshot.getChildrenCount();
            }
            setValue(commentCount);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }


}
