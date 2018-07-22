package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.CommentsLiveData;
import com.sergiocruz.capstone.viewmodel.NumCommentsListLiveData;
import com.sergiocruz.capstone.viewmodel.NumberOfCommentsLiveData;
import com.sergiocruz.capstone.viewmodel.StarsLiveData;
import com.sergiocruz.capstone.viewmodel.TravelPacksLiveData;
import com.sergiocruz.capstone.viewmodel.TravelStarsListLiveData;
import com.sergiocruz.capstone.viewmodel.UserLiveData;

import java.util.List;

import timber.log.Timber;

public class FirebaseRepository {

    // DB node references
    public static final String USERS_REF = "users";
    public static final String TRAVEL_PACKS_REF = "travel-packs";
    public static final String TRAVEL_PACK_COMMENTS_REF = "travel-pack-comments";
    public static final String TRAVEL_PACK_STARS_REF = "travel-pack-stars";

    private static FirebaseRepository sInstance;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static TravelPacksLiveData travelPacks;
    private static UserLiveData userLiveData;

    private FirebaseRepository() {
        firebaseDatabase.setPersistenceEnabled(true); // Enable Offline Capabilities of Firebase https://firebase.google.com/docs/database/android/offline-capabilities
        databaseReference = firebaseDatabase.getReference();
    }

    public static FirebaseRepository getInstance() {
        if (sInstance == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            sInstance = new FirebaseRepository();
        }
        return sInstance;
    }

    /**
     * Call FirebaseRepository.getInstance(); First
     */
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    @NonNull
    public LiveData<List<Travel>> getTravelPacks() {
        if (travelPacks == null) {
            travelPacks = new TravelPacksLiveData(databaseReference);
        }
        return travelPacks;
    }

    public LiveData<User> getUser() {
        if (userLiveData == null) {
            userLiveData = new UserLiveData(databaseReference);
        }
        return userLiveData;
    }

    public LiveData<List<Comment>> getCommentsForTravelID(String travelID) {
        return new CommentsLiveData(databaseReference, travelID);
    }

    // db structure
    //    travel-pack-comments       |  travel-pack-stars
    //        pack_0                 |      pack ID
    //            Comment ID         |          comment ID
    //                (Comment)      |              (TravelStar)
    public void sendComment(Comment comment) {
        final DatabaseReference referenceForTravelID =
                databaseReference
                        .child(TRAVEL_PACK_COMMENTS_REF)
                        .child(comment.getTravelID());

        referenceForTravelID
                .push() // generate new comment node
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // resulting new snapshot with nodeID (commentID)
                        String commentID = dataSnapshot.getKey();
                        comment.setCommentID(commentID);    // update comment ID value in Comment
                        dataSnapshot
                                .getRef()
                                .setValue(comment)         // save the new comment object to db
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                        updateTravelPackStars(comment);
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO callbacks
                    }
                });
    }

    private void updateTravelPackStars(Comment comment) {
        // Update Number of comments, Number of stars and rating
        // for travel pack ID
        TravelStar travelStar =
                new TravelStar(
                        comment.getStars(), // the value
                        comment.getTravelID(),
                        comment.getCommentID(),
                        comment.getUserID());

        databaseReference
                .child(TRAVEL_PACK_STARS_REF)
                .child(comment.getTravelID())
                .child(comment.getCommentID())
                .setValue(travelStar)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Timber.i("update stars ok");
                });

    }

    public LiveData<Long> getNumCommentsForTravelID(String travelID) {
        return new NumberOfCommentsLiveData(databaseReference, travelID);
    }

    public LiveData<Star> getStarsForTravelID(String travelID) {
        return new StarsLiveData(databaseReference, travelID);
    }

    public LiveData<List<Star>> getTravelStars() {
        return new TravelStarsListLiveData(databaseReference);
    }

    public LiveData<List<Long>> getNumCommentsList() {
        return new NumCommentsListLiveData(databaseReference);
    }
}
