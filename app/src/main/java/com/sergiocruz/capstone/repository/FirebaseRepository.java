package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.database.TravelPackLiveData;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class FirebaseRepository {
    private static FirebaseRepository sInstance;
    private static FirebaseDatabase firebaseDatabase;
    private TravelPackLiveData travelPacks;
    private DatabaseReference databaseReference;
    private User user;

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
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

        if (user != null) {
            userMutableLiveData.setValue(user);
            return userMutableLiveData;
        }

        // Authenticated user info
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        user = convertUser(firebaseUser);
        userMutableLiveData.setValue(user);

        // Database user info
        DatabaseReference reference = databaseReference.child("users/" + firebaseUser.getUid() + "/");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    userMutableLiveData.setValue(user);
                } else {
                    user = null;
                    userMutableLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                user = null;
                userMutableLiveData.setValue(null);
            }
        });

        return userMutableLiveData;
    }

    private User convertUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {

            String email = firebaseUser.getEmail();
            List<? extends UserInfo> providerData = firebaseUser.getProviderData();
            if (email == null && providerData != null && providerData.size() > 1) {
                email = providerData.get(1).getEmail();
            }

            String authProvider = "";
            List<String> providers = firebaseUser.getProviders();
            if (providers != null && providers.size() > 0) authProvider = providers.get(0);

            return new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    String.valueOf(firebaseUser.getPhotoUrl()),
                    email,
                    firebaseUser.getPhoneNumber(),
                    authProvider,
                    firebaseUser.isAnonymous());

        } else {
            return new User(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    true);
        }
    }

//    public DatabaseReference getDBUserRef(String userID) {
//        DatabaseReference reference = databaseReference.child("users/" + userID + "/");
//        return reference;
//    }
//
//
//    public void setupFirebaseDB(Context context, String userID) {
//
//        DatabaseReference reference = getDBUserRef(userID);
//
//        reference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Toast.makeText(context, "Logged in as " + snapshot.child("userName").getValue(), Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(context, "Error: Login failed", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(context, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }


}
