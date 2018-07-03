package com.sergiocruz.capstone.database;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Travel;

import java.util.ArrayList;
import java.util.List;

public class TravelPackLiveData extends LiveData<List<Travel>> {
    private static final String LOG_TAG = "TravelPackLiveData";
    private static String TRAVEL_PACKS_REF = "travel-packs";
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public TravelPackLiveData(Query query) {
        this.query = query;
    }

    public TravelPackLiveData(DatabaseReference databaseReference) {
        databaseReference = databaseReference.child(TRAVEL_PACKS_REF);
        this.query = databaseReference;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Travel> travelList = new ArrayList<>();
            if (dataSnapshot.hasChildren()) {
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {

                    Travel travel = snapshotChild.getValue(Travel.class);
                    travelList.add(travel);

                    Log.i("Sergio>", this + "Travel= " + travel.toString());
                }
            }
            setValue(travelList);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }

}