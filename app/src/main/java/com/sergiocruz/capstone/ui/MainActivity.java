package com.sergiocruz.capstone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Fragment startFragment;
        String fragmentTag;
        if (currentUser == null) {
            startFragment = new LoginFragment();
            fragmentTag = LoginFragment.class.getSimpleName();
        } else {
            startFragment = new PagerFragment();
            fragmentTag = PagerFragment.class.getSimpleName();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment_container, startFragment, fragmentTag)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }
}
