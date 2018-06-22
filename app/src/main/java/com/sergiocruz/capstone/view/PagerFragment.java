package com.sergiocruz.capstone.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentPagerBinding;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.HomePageViewModel;


/**
 * Main Fragment with App content, a ViewPager with bottom navigation in this case.
 */
public class PagerFragment extends Fragment {

    private FragmentPagerBinding binding;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    binding.messageTextview.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    binding.messageTextview.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    binding.messageTextview.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mFirebaseDatabase;
    private User user;

    public PagerFragment() {
        // Required empty public constructor
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url == null ? R.drawable.ic_user_icon_48dp : url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
                .onLoadFailed(ContextCompat.getDrawable(imageView.getContext(), R.drawable.ic_user_icon_48dp));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pager, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        HomePageViewModel viewModel = ViewModelProviders.of(this).get(HomePageViewModel.class);

        // variable name in xml <data><variable>
        binding.setViewModel(viewModel);


        binding.buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment_container, new LoginFragment(), LoginFragment.class.getSimpleName())
                    .commit();
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // setup the menu and toolbar
        setupToolbar();

        getAuthenticatedUserData();
        setupFirebase();

        return binding.getRoot();
    }

    private void setupToolbar() {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);

        Glide.with(this)
                .load(R.mipmap.ic_launcher)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.toolbarLayout.userIcon)
                .onLoadFailed(ContextCompat.getDrawable(getContext(), R.drawable.ic_user_icon_48dp));

        binding.toolbarLayout.userIcon.setOnClickListener(v ->
                Toast.makeText(getContext(), "Should Open Account Drawer", Toast.LENGTH_LONG).show());
    }

    private void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mFirebaseDatabase.child("users/" + user.getUserID() + "/");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "Logged in as " + snapshot.child("userName").getValue(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error: Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // get Firebase Authenticated user
    private void getAuthenticatedUserData() {
        firebaseAuth = FirebaseAuth.getInstance();
        copyUser();
        authStateListener = firebaseAuth -> {
            PagerFragment.this.firebaseAuth = firebaseAuth;
            copyUser();
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void copyUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {

            String email = firebaseUser.getEmail();
            if (email == null) {
                email = firebaseUser.getProviderData().get(1).getEmail();
            }

            user = new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    String.valueOf(firebaseUser.getPhotoUrl()),
                    email,
                    firebaseUser.getPhoneNumber(),
                    firebaseUser.getProviders() != null ? firebaseUser.getProviders().get(0) : null,
                    firebaseUser.isAnonymous());

        } else {

            user = new User(
                    null,
                    getString(R.string.anonymous),
                    null,
                    null,
                    null,
                    null,
                    true);
        }
        Log.i("Sergio>", this + " copyUser copied user ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
