package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Star;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelComments;
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
    private static final String TRAVEL_PACKS_REF = "travel-packs";
    public static final String TRAVEL_PACK_COMMENTS_REF = "travel-pack-comments";
    public static final String TRAVEL_PACK_STARS_REF = "travel-pack-stars";
    private static final String USERS_FAVORITES_REF = "users-favorites";

    private static FirebaseRepository sInstance;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private TravelPacksLiveData travelPacks;
    private UserLiveData userLiveData;
    private TravelPacksLiveData favoriteTravelPacks;

    private FirebaseRepository() {
        // Enable Offline Capabilities of Firebase https://firebase.google.com/docs/database/android/offline-capabilities
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
        //Data that is kept in sync is not purged from the cache.
        databaseReference.keepSynced(true);
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
    public MutableLiveData<List<Travel>> getTravelPacks() {
        if (travelPacks == null) {
            travelPacks = new TravelPacksLiveData(
                    databaseReference
                            .child(TRAVEL_PACKS_REF));
            Timber.i("getting travel packs from TravelPacksLiveData");
        }
        return travelPacks;
    }

    @NonNull
    /** Favorites for user ID get.Id() */
    MutableLiveData<List<Travel>> getFavoriteTravelPacks() {
        String userID = getUser().getValue().getUserID();
        if (!TextUtils.isEmpty(userID)) {
            favoriteTravelPacks = new TravelPacksLiveData(
                    databaseReference
                            .child(USERS_FAVORITES_REF)
                            .child(userID));
        }
        return favoriteTravelPacks;
    }

    public MutableLiveData<User> getUser() {
        if (userLiveData == null) {
            userLiveData = new UserLiveData(databaseReference);
        }
        return userLiveData;
    }

    MutableLiveData<List<Comment>> getCommentsForTravelID(String travelID) {
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

    public void editComment(Comment comment) {
        databaseReference
                .child(TRAVEL_PACK_COMMENTS_REF)
                .child(comment.getTravelID())
                .child(comment.getCommentID())
                .setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.i("edit comment done");
                        updateTravelPackStars(comment);
                    }
                });
    }

    private void updateTravelPackStars(Comment comment) {
        // Update Number of comments, Number of stars and rating
        // for travel pack ID
        Star travelStar =
                new Star(
                        comment.getStars(), // the number of stars given in the comment
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

    public MutableLiveData<Long> getNumCommentsForTravelID(String travelID) {
        return new NumberOfCommentsLiveData(databaseReference, travelID);
    }

    public MutableLiveData<TravelStar> getStarsForTravelID(String travelID) {
        return new StarsLiveData(databaseReference, travelID);
    }

    public MutableLiveData<List<TravelStar>> getTravelStars() {
        return new TravelStarsListLiveData(databaseReference);
    }

    public MutableLiveData<List<TravelComments>> getNumCommentsList() {
        return new NumCommentsListLiveData(databaseReference);
    }

    Boolean saveTravelToFavorites(Travel travel) {
        User user = getUser().getValue();
        if (user.getIsAnonymous()) return false;

        databaseReference
                .child(USERS_FAVORITES_REF)
                .child(user.getUserID())
                .child(travel.getID())
                .setValue(travel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.i("Done saving favorite");
                    } else {
                        Timber.w("Failed saving favorite!");
                    }

                });

        return true;
    }

    public void removeTravelFromFavorites(String travelID) {
        if (TextUtils.isEmpty(travelID)) return;

        databaseReference
                .child(USERS_FAVORITES_REF)
                .child(getUser().getValue().getUserID())
                .child(travelID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.i("Done removing favorite");
                    } else {
                        Timber.w("Failed to remove favorite!");
                    }

                });
    }

}
