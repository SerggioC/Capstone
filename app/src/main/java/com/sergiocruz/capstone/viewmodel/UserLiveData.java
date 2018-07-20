package com.sergiocruz.capstone.viewmodel;

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

import static com.sergiocruz.capstone.repository.FirebaseRepository.USERS_REF;

public class UserLiveData extends LiveData<User> {
    private Query query;
    private MyValueEventListener listener;
    private MyAuthStateChangeListener authListener;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;

    public UserLiveData(Query query) {
        this.query = query;
    }

    public UserLiveData(DatabaseReference databaseReference) {
        Timber.i("creating new UserLiveData");

        this.databaseReference = databaseReference;
        authListener = new MyAuthStateChangeListener();
        listener = new MyValueEventListener();

        String dbUserRef = getUserDBRefString(firebaseAuth);

        this.query = databaseReference.child(dbUserRef);
    }

    // Detect User changes (logout/login)
    private class MyAuthStateChangeListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            Timber.i("Auth state changed!");

            String dbUserRef = getUserDBRefString(firebaseAuth);

            // Re-Attach listener to new query
            query = databaseReference.child(dbUserRef);
            query.removeEventListener(listener);
            query.addValueEventListener(listener);
        }
    }

    @NonNull
    private String getUserDBRefString(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String firebaseUserUid = currentUser == null ? null : currentUser.getUid();
        return USERS_REF + "/" + firebaseUserUid + "/";
    }

    @Override
    protected void onActive() {
        Timber.i("Creating user Listener");
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onInactive() {
        Timber.w("Removing user Listener");
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