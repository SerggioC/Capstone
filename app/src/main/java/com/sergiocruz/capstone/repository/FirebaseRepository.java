package com.sergiocruz.capstone.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRepository {

    private static FirebaseRepository sInstance;

    public static FirebaseRepository getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseRepository();
        }
        return sInstance;
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

}
