package com.sergiocruz.capstone.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

import java.util.List;

public class FirebaseRepository {
    private static FirebaseRepository sInstance;
    private DatabaseReference databaseReference;
    private User user;
    private ValueListener valueListener;

    private FirebaseRepository(FirebaseDatabase firebaseDatabase) {
        firebaseDatabase.setPersistenceEnabled(true); // Enable Offline Capabilities of Firebase https://firebase.google.com/docs/database/android/offline-capabilities
        databaseReference = firebaseDatabase.getReference();
        getTravelPacksList();
    }

    public static FirebaseRepository getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseRepository(FirebaseDatabase.getInstance());
        }
        return sInstance;
    }

    public void getTravelPacksList() {
        DatabaseReference packsReference = databaseReference.child("travel-packs").child("Pack id 1");
        packsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
/*
                HashMap<String, String> value11 = (HashMap<String, String>) dataSnapshot.getValue();
                List<String> images = Collections.singletonList(value11.get("images"));*/

//                HashMap<String, String> value1 = (HashMap<String, String>) dataSnapshot.child("images").getValue();
                //HashMap<String, String> value1 = (HashMap<String, String>) dataSnapshot.child("pack-type").getValue();
//                for (Map.Entry entry : value1.entrySet()) {
//                    System.out.println("Key: " + entry.getKey() + " & Value: " + entry.getValue());
//                }

                @SuppressWarnings("unchecked")
                List<String> imageUrlList = (List<String>) dataSnapshot.child("images").getValue();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Object value = snapshot.child(key).getValue();
                        snapshot.getValue();
                    }
                } else {
                    String key = dataSnapshot.getKey();
                    Object value = dataSnapshot.child(key).getValue();
                }
                Travel travel = dataSnapshot.getValue(Travel.class);
                Log.i("Sergio>", this + " onDataChange\nTravel= " + travel.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public LiveData<User> getUser(ValueListener valueListener) {
        final MutableLiveData<User> data = new MutableLiveData<>();

        if (user != null) {
            data.setValue(user);
            return data;
        }

        // Authenticated user info
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        user = convertUser(firebaseUser);
        data.setValue(user);

        // Database user info
        DatabaseReference reference = databaseReference.child("users/" + firebaseUser.getUid() + "/");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    Toast.makeText(context, "Logged in as " + snapshot.child("userName").getValue(), Toast.LENGTH_LONG).show();
                    user = snapshot.getValue(User.class);
                    data.setValue(user);
                } else {
//                    Toast.makeText(context, "Error: Login failed", Toast.LENGTH_LONG).show();
                    user = null;
                    data.setValue(user);
                }

                if (valueListener != null) {
                    valueListener.onValueEvent(snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(context, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                user = null;
                data.setValue(user);
            }
        });

        return data;
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

    public DatabaseReference getDBUserRef(String userID) {
        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mFirebaseDatabase.child("users/" + userID + "/");
        return reference;
    }

    public void setupFirebaseDB(Context context, String userID) {

        DatabaseReference reference = getDBUserRef(userID);

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context, "Logged in as " + snapshot.child("userName").getValue(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error: Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface ValueListener {
        void onValueEvent(DataSnapshot dataSnapshot);

    }


}
