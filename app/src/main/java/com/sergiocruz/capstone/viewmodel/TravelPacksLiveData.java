package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
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

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACKS_REF;

public class TravelPacksLiveData extends LiveData<List<Travel>> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public TravelPacksLiveData(Query query) {
        this.query = query;
    }

    public TravelPacksLiveData(DatabaseReference databaseReference) {
        databaseReference = databaseReference.child(TRAVEL_PACKS_REF);
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