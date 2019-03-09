package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.util.AppExecutors;

import timber.log.Timber;

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACK_STARS_REF;

public class StarsLiveData extends MutableLiveData<TravelStar> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public StarsLiveData(Query query) {
        this.query = query;
    }

    public StarsLiveData(DatabaseReference databaseReference, String travelID) {
        databaseReference = databaseReference.child(TRAVEL_PACK_STARS_REF).child(travelID);
        this.query = databaseReference;
        query.addValueEventListener(listener);
    }

//    @Override
//    protected void onActive() {
//        Timber.d("onActive");
//    }
//
//    @Override
//    protected void onInactive() {
//        Timber.d("onInactive");
//        query.removeEventListener(listener);
//    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            AppExecutors executors = new AppExecutors();
            executors.networkIO().execute(() -> {
                TravelStar travelStar = getStar(dataSnapshot, dataSnapshot.getKey());
                postValue(travelStar);
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }

    @NonNull
    public static TravelStar getStar(@NonNull DataSnapshot dataSnapshot, String travelID) {
        Long totalComments = 0L; // total comments = total ratings given
        Float totalStars = 0f;
        if (dataSnapshot.hasChildren()) {
            totalComments = dataSnapshot.getChildrenCount();
            for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                Star travelStar = snapshotChild.getValue(Star.class);
                totalStars += travelStar != null ? travelStar.getValue() : 0;
            }
        }

        Float rating = totalStars / totalComments;
        return new TravelStar(rating, totalStars, travelID);
    }


}