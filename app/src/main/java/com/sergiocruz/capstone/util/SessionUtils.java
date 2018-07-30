package com.sergiocruz.capstone.util;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentManager;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.view.fragment.LoginFragment;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

public class SessionUtils {

    public static void logoutFromFirebase(android.support.v4.app.FragmentActivity activity) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        ViewModelProviders.of(activity).get(MainViewModel.class).logoutUser();

        // Pop out all the fragments including container and replace with the login fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        String name = fragmentManager.getBackStackEntryAt(0).getName();
        fragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Alternative pop all fragments from stack
//        int stackEntryCount = fragmentManager.getBackStackEntryCount();
//        for (int i = 0; i < stackEntryCount; i++) {
//            fragmentManager.popBackStack();
//        }

        if (fragmentManager.findFragmentByTag(LoginFragment.class.getSimpleName()) == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.root_fragment_container, new LoginFragment(), LoginFragment.class.getSimpleName())
                    .commit();
        }

    }
}
