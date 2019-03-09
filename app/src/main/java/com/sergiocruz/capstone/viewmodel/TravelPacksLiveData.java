package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Travel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TravelPacksLiveData extends MutableLiveData<List<Travel>> {
    private Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public TravelPacksLiveData(DatabaseReference databaseReference) {
        this.query = databaseReference;
        query.addValueEventListener(listener);
        Timber.d("onCreate TravelPacksLiveData");
    }

//    @Override
//    protected void onActive() {
//        query.addValueEventListener(listener);
//        Timber.d("onActive");
//    }
//
//    @Override
//    protected void onInactive() {
//        query.removeEventListener(listener);
//        Timber.d("onInactive");
//    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Travel> travelList = new ArrayList<>();
            if (dataSnapshot.hasChildren()) {
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                    Travel travel = snapshotChild.getValue(Travel.class);
                    travelList.add(travel);
                }
            }
            setValue(travelList);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }


}