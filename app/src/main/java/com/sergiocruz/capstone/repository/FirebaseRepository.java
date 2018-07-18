package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sergiocruz.capstone.database.CommentsLiveData;
import com.sergiocruz.capstone.database.TravelPackLiveData;
import com.sergiocruz.capstone.database.UserLiveData;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class FirebaseRepository {
    private static FirebaseRepository sInstance;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static TravelPackLiveData travelPacks;
    private static UserLiveData userLiveData;
    private CommentsLiveData commentsLiveData;

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
            travelPacks = new TravelPackLiveData(databaseReference);
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
        if (commentsLiveData == null) {
            commentsLiveData = new CommentsLiveData(databaseReference, travelID);
        }
        return commentsLiveData;
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
