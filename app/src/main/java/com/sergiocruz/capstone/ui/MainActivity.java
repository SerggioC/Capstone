package com.sergiocruz.capstone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sergiocruz.capstone.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            // Return here to prevent adding additional
            // Fragments when changing orientation.
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Fragment startFragment;
        String fragmentTag;
        if (currentUser == null) {
            startFragment = new LoginFragment();
            fragmentTag = LoginFragment.class.getSimpleName();
        }
        else {
            startFragment = new PagerFragment();
            fragmentTag = PagerFragment.class.getSimpleName();
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, startFragment, fragmentTag)
                .commit();
    }

}
