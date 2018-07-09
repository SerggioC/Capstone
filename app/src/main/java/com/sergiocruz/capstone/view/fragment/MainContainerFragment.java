package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentDrawerBinding;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import static com.sergiocruz.capstone.view.fragment.HomeFragment.ROOT_FRAGMENT_NAME;

public class MainContainerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    //FragmentMainContainerBinding binding;
    FragmentDrawerBinding binding;
    private MainViewModel viewModel;

    public MainContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_drawer, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        // setup the menu and toolbar
        setupToolbar();

        // Navigation Drawer item click listener
        binding.navView.setNavigationItemSelectedListener(this);

        // setup Bottom navigation Menu
        setupBottomNavigation();

        return binding.getRoot();
    }

    private void setupToolbar() {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);
        binding.toolbarLayout.userIcon.setOnClickListener(this::toggleDrawer);
    }

    @Deprecated
    private void setupDrawerNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), binding.drawerLayout,
                binding.toolbarLayout.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        // Replace toggle icon
        toggle.setDrawerIndicatorEnabled(false);
        //Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, getActivity().getTheme());
        toggle.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        toggle.setToolbarNavigationClickListener(this::toggleDrawer);

        binding.toolbarLayout.userIcon.setOnClickListener(this::toggleDrawer);

    }

    private void toggleDrawer(View v) {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void closeDrawer() {
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    public boolean isDrawerOpen() {
        return binding.drawerLayout.isDrawerVisible(Gravity.START);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationAh.setOnTabSelectedListener(this::switchFragmentContent);

        // navigation item from menu
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(binding.bottomNavigationAh);

        // Enable the Y translation inside the CoordinatorLayout
        binding.bottomNavigationAh.setBehaviorTranslationEnabled(true);

        // Change colors
        binding.bottomNavigationAh.setAccentColor(ContextCompat.getColor(getContext(), R.color.email_light));
        binding.bottomNavigationAh.setInactiveColor(ContextCompat.getColor(getContext(), R.color.black));
        binding.bottomNavigationAh.setDefaultBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.bottomNavigationAh.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
    }

    public void selectHomeNavigation() {
        binding.bottomNavigationAh.setCurrentItem(0);
    }

    private boolean switchFragmentContent(int position, boolean wasSelected) {
        if (wasSelected) return true;

        Fragment fragment = null;
        Boolean isHomeFragment = false;

        switch (position) {
            case 0:
                isHomeFragment = true;
                break;
            case 1:
                fragment = new MapFragment();

                break;
            case 2:

                break;
            case 3:

                break;
        }

        manageContentBackStack(fragment, isHomeFragment);

        return true;
    }

    // Allow only two fragments at a time
    private void manageContentBackStack(Fragment fragment, Boolean isHomeFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int stackEntryCount = fragmentManager.getBackStackEntryCount();
        if (isHomeFragment) {
            if (stackEntryCount >= 2) {
                // pop out upto HomeFragment exclusivé
                fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, 0);
            }
        } else if (fragment != null) {

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            String tagName = fragment.getClass().getSimpleName();

            if (stackEntryCount >= 2) {
                // pop out upto HomeFragment exclusivé
                fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, 0);
                transaction.add(R.id.frame_content_holder, fragment, tagName);
                if (!tagName.equals(ROOT_FRAGMENT_NAME))
                    transaction.addToBackStack(tagName);
            } else {
                transaction.add(R.id.frame_content_holder, fragment, tagName);
                transaction.addToBackStack(tagName);
            }
            transaction.commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logoutFromFirebase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        viewModel.logoutUser();

        // Pop out all the fragments including container and replace with the login fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        String name = fragmentManager.getBackStackEntryAt(0).getName();
        //fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
