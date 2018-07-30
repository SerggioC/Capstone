package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentDrawerBinding;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
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
        binding.headerLayout.setViewModel(viewModel);
        binding.headerLayout.backArrow.setOnClickListener(v -> closeDrawer());

        // setup Bottom navigation Menu
        setupBottomNavigation();

        // change menu option for anonymous user
        viewModel.getUser().observe(this, user ->
                binding.navView.getMenu().findItem(R.id.nav_login).setVisible(user.getIsAnonymous()));

        return binding.getRoot();
    }

    private void setupToolbar() {
        setHasOptionsMenu(false); // menu options on the drawer
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);
        binding.toolbarLayout.userIcon.setOnClickListener(this::toggleDrawer);
    }

    @SuppressWarnings("unused")
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_login:
                logIn();
                break;
            case R.id.nav_favorites:
                goToFavorites();
                break;

            case R.id.nav_account:
                goToAccount();
                break;

            case R.id.nav_logout:
                logoutFromFirebase();
                break;

            case R.id.nav_share:
                shareApp();
                break;

            case R.id.nav_feedback:
                sendFeedback();
                break;

            case R.id.nav_options:
                showSettings();
                break;
        }

        closeDrawer();
        return true;
    }

    private void logIn() {
        Utils.showSlimToast(getContext(), getString(R.string.login_with_accounts), Toast.LENGTH_LONG);
        logoutFromFirebase();
    }

    private void showSettings() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings_popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupView.setFocusable(true); // lets taps outside the popup also dismiss it
        popupWindow.setOutsideTouchable(true);

        popupWindow.setAnimationStyle(R.style.PopupWindowAnimationStyle);

        // https://material.io/design/environment/elevation.html#default-elevations
        popupWindow.setElevation(getContext().getResources().getDimension(R.dimen.popup_window_elevation));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(binding.frameContentHolder, Gravity.CENTER, 0, 0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean defaultNotify = getContext().getResources().getBoolean(R.bool.default_notifications);
        Boolean prefNotify = sharedPreferences.getBoolean(getString(R.string.notification_pref_key), defaultNotify);

        Switch switchButton = popupView.findViewById(R.id.notifications_switch);
        switchButton.setChecked(prefNotify);

        TextView statusTextView = popupView.findViewById(R.id.status);
        statusTextView.setText(switchButton.isChecked() ? R.string.notifications_on : R.string.notifications_off);

        switchButton.setOnClickListener(v -> {
            statusTextView.setText(switchButton.isChecked() ? R.string.notifications_on : R.string.notifications_off);
//            saveSettings(sharedPreferences, switchButton.isChecked());
        });

        popupView.findViewById(R.id.cancel_button).setOnClickListener(v -> popupWindow.dismiss());
        popupView.findViewById(R.id.ok_button).setOnClickListener(v -> {
            saveSettings(sharedPreferences, switchButton.isChecked());
            popupWindow.dismiss();
        });

    }

    private void saveSettings(SharedPreferences sharedPreferences, Boolean shouldNotify) {
        sharedPreferences
                .edit()
                .putBoolean(getString(R.string.notification_pref_key), shouldNotify)
                .apply();
    }

    private void sendFeedback() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_fragment_container, new FeedbackFragment(), FeedbackFragment.class.getSimpleName())
                .addToBackStack(FeedbackFragment.class.getSimpleName())
                .commit();
    }

    private void shareApp() {
        String message = getString(R.string.share_app_text) + " " + getString(R.string.app_url);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
    }

    private void goToAccount() {
        Toast.makeText(getContext(), "Account", Toast.LENGTH_LONG).show();
    }

    private void goToFavorites() {
        Toast.makeText(getContext(), "Favorites", Toast.LENGTH_LONG).show();
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
        binding.bottomNavigationAh.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        binding.bottomNavigationAh.setInactiveColor(ContextCompat.getColor(getContext(), R.color.black));
        binding.bottomNavigationAh.setDefaultBackgroundResource(R.drawable.background_bottom_navigation);
        //binding.bottomNavigationAh.setDefaultBackgroundColor((ContextCompat.getColor(getContext(), R.color.colorPrimary));
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
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
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

    private void logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        viewModel.logoutUser();

        // Pop out all the fragments including container and replace with the login fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
