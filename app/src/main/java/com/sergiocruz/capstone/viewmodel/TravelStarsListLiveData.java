package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACK_STARS_REF;

public class TravelStarsListLiveData extends LiveData<List<TravelStar>> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public TravelStarsListLiveData(Query query) {
        this.query = query;
    }

    public TravelStarsListLiveData(DatabaseReference databaseReference) {
        databaseReference = databaseReference.child(TRAVEL_PACK_STARS_REF);
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
                List<TravelStar> travelStarList = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                        TravelStar travelStar = StarsLiveData.getStar(snapshotChild, snapshotChild.getKey());
                        travelStarList.add(travelStar);
                    }
                }
                postValue(travelStarList);
            });
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }

}