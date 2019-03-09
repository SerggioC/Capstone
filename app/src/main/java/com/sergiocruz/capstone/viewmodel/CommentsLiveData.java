package com.sergiocruz.capstone.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Comment;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.capstone.repository.FirebaseRepository.TRAVEL_PACK_COMMENTS_REF;

public class CommentsLiveData extends MutableLiveData<List<Comment>> {
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public CommentsLiveData(Query query) {
        this.query = query;
    }

    public CommentsLiveData(DatabaseReference databaseReference, String travelID) {
        databaseReference = databaseReference.child(TRAVEL_PACK_COMMENTS_REF).child(travelID);
        this.query = databaseReference.orderByChild("date"); // .limitToLast(10);
        query.addValueEventListener(listener);
    }

//    @Override
//    protected void onActive() {
//        Timber.d("onActive");
//        query.addValueEventListener(listener);
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
            List<Comment> commentList = new ArrayList<>();
            if (dataSnapshot.hasChildren()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
            }
            setValue(commentList);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + databaseError.toException());
        }
    }


}
