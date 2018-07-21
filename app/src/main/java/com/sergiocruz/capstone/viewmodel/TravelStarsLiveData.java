package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.TravelStar;

import timber.log.Timber;

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACK_STARS_REF;

public class TravelStarsLiveData extends LiveData<Star> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public TravelStarsLiveData(Query query) {
        this.query = query;
    }

    public TravelStarsLiveData(DatabaseReference databaseReference, String travelID) {
        databaseReference = databaseReference.child(TRAVEL_PACK_STARS_REF).child(travelID);
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
            Long totalComments = 0L;
            Float totalStars = 0f;

            if (dataSnapshot.hasChildren()) {
                totalComments = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                    TravelStar travelStar = snapshotChild.getValue(TravelStar.class);
                    totalStars += travelStar.getValue();
                }
            }

            Float rating = totalStars / totalComments;

            Star star = new Star(rating, totalComments);

            setValue(star);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }


}