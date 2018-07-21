package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.CommentsLiveData;
import com.sergiocruz.capstone.viewmodel.TravelPacksLiveData;
import com.sergiocruz.capstone.viewmodel.UserLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FirebaseRepository {
    public static final String USERS_REF = "users";
    public static final String TRAVEL_PACKS_REF = "travel-packs";
    public static final String COMMENTS_KEY = "comments"; // key 'comments' in travel packs
    public static final String STARS_KEY = "stars"; // key 'stars' in travel packs
    public static final String RATING_KEY = "rating"; // key 'rating' in travel packs

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
    //                (Comment)      |              user ID : value
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
                                        updateTravelPack(referenceForTravelID, comment);
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO callbacks
                    }
                });
    }

    private void updateTravelPack(DatabaseReference referenceForTravelID, Comment comment) {
        // Update Number of comments, Number of stars and rating
        // in travel pack ID
        referenceForTravelID
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long numComments = dataSnapshot.getChildrenCount();
                        long starSum = 0;

                        // sum the total stars for the travelID
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment comment = snapshot.getValue(Comment.class);
                            starSum += comment.getStars();
                        }

                        float rating = starSum / numComments;

                        Map<String, Object> map = new HashMap<>();
                        map.put(COMMENTS_KEY, numComments);
                        map.put(STARS_KEY, starSum);
                        map.put(RATING_KEY, rating);

                        databaseReference
                                .child(TRAVEL_PACKS_REF)
                                .child(comment.getTravelID())
                                .updateChildren(map, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Timber.i("ye!");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

//    @Deprecated
//    public LiveData<User> getUser0() {
//        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
//
//        if (user != null) {
//            userMutableLiveData.setValue(user);
//            return userMutableLiveData;
//        }
//
//        // Authenticated user info
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        //user = convertUser(firebaseUser);
//        userMutableLiveData.setValue(user);
//
//        // Database user info
//        DatabaseReference reference = databaseReference.child("users/" + firebaseUser.getUid() + "/");
//        reference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    user = snapshot.getValue(User.class);
//                    userMutableLiveData.setValue(user);
//                } else {
//                    user = null;
//                    userMutableLiveData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                user = null;
//                userMutableLiveData.setValue(null);
//            }
//        });
//
//        return userMutableLiveData;
//    }


}
