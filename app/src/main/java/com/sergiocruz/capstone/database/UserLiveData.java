package com.sergiocruz.capstone.database;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.model.User;

import timber.log.Timber;

public class UserLiveData extends LiveData<User> {
    private Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private final MyAuthStateChangeListener authListener = new MyAuthStateChangeListener();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;

    public UserLiveData(Query query) {
        this.query = query;
    }

    public UserLiveData(DatabaseReference databaseReference) {
        Timber.w("creating new UserLiveData");

        this.databaseReference = databaseReference;

//        String firebaseUserUid = firebaseAuth.getCurrentUser().getUid();
//        String dbUserRef = "users/" + firebaseUserUid + "/";
//
//        this.query = databaseReference.child(dbUserRef);
    }

    // Detect User changes (logout/login)
    private class MyAuthStateChangeListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            Timber.e("auth state changed!");

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            String firebaseUserUid = currentUser == null ? null : currentUser.getUid();
            String dbUserRef = "users/" + firebaseUserUid + "/";

            query = databaseReference.child(dbUserRef);

            query.removeEventListener(listener);
            query.addValueEventListener(listener);
        }
    }


    @Override
    protected void onActive() {
        Timber.w("creating user Listener");
        //query.addValueEventListener(listener);
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onInactive() {
        Timber.w("removing user Listener");
        query.removeEventListener(listener);
        firebaseAuth.removeAuthStateListener(authListener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                setValue(user);
            } else {
                setValue(getNullUser());
            }
            Timber.w("getting user from source!");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("Can't listen to query " + query + " " + databaseError.toException());
            //setValue(getNullUser());
        }
    }

    @NonNull
    private User getNullUser() {
        return new User(null, null, null, null, null, null, true);
    }


}