package com.sergiocruz.capstone.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.view.fragment.HomeFragment;
import com.sergiocruz.capstone.view.fragment.LoginFragment;
import com.sergiocruz.capstone.view.fragment.MainContainerFragment;

import static com.sergiocruz.capstone.view.fragment.HomeFragment.ROOT_FRAGMENT_NAME;

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
        if (currentUser == null) {

            // Boot up to login
            startFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_fragment_container, startFragment, startFragment.getClass().getSimpleName())
                    .commit();
        } else {

            // Dual Boot to main content holder fragment + content fragment
            startFragment = new MainContainerFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_fragment_container, startFragment, startFragment.getClass().getSimpleName())
                    .add(R.id.frame_content_holder, new HomeFragment(), HomeFragment.class.getSimpleName())
                    .addToBackStack(HomeFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment loginFragment = fragmentManager.findFragmentByTag(LoginFragment.class.getSimpleName());
        if (loginFragment != null && loginFragment.isVisible()) {
            ((LoginFragment) loginFragment).onLoginBackPressed();
        } else {
            int stackEntryCount = fragmentManager.getBackStackEntryCount();
            if (stackEntryCount >= 2) {

                // pop out upto HomeFragment exclusivé
                fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, 0);

                // Update selected navigation icon on bottom navigation bar
                Fragment container = fragmentManager.findFragmentByTag(MainContainerFragment.class.getSimpleName());
                if (container != null && container.isVisible()) {
                    ((MainContainerFragment) container).selectHomeNavigation();
                }

            } else {
                finish();
            }
        }
    }

}
