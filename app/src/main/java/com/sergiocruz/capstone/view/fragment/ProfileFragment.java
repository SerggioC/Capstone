package com.sergiocruz.capstone.view.fragment;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.TravelsAdapter;
import com.sergiocruz.capstone.databinding.FragmentProfileBinding;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

import timber.log.Timber;

public class ProfileFragment extends Fragment implements BaseAdapter.OnItemClickListener<Travel>, BaseAdapter.OnItemTouchListener {

    private static final String SOME_BUNDLE_KEY = "SOME_BUNDLE_KEY";
    private FragmentProfileBinding binding;
    private TravelsAdapter adapter;
    private String someID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_promotion, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (savedInstanceState != null && savedInstanceState.containsKey(SOME_BUNDLE_KEY)) {
            someID = savedInstanceState.getString(SOME_BUNDLE_KEY);
        }


        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        setupRecyclerView();

        viewModel.getTravelPacks().observe(this, this::populateRecyclerView);

        return binding.getRoot();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupRecyclerView() {
        Boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            int spanCount = getResources().getInteger(R.integer.spanCount);
            binding.profileRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        } else {
            binding.profileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        binding.profileRecyclerView.setHasFixedSize(true);
        adapter = new TravelsAdapter(this, this);
        binding.profileRecyclerView.setAdapter(adapter);
    }

    private void populateRecyclerView(List<Travel> travels) {
        adapter.swapTravelsData(travels);
    }

    private void onUserInfo(User user) {
        if (someID != null && user != null && someID.equals(user.getUserID())) {
            return;
        }

        String message;
        if (user == null || user.getIsAnonymous()) {
            message = getString(R.string.logged_in) + " " + getString(R.string.anonymous);
            someID = null;
        } else {
            message = getString(R.string.logged_in) + " " + getString(R.string.as) + " " + user.getUserName();
            someID = user.getUserID();
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Crashlytics.getInstance().crash(); // Force a crash
    }

    @Override
    public void onItemClick(Travel travel, View view,  Integer position) {
        Toast.makeText(getContext(), "Clicked " + travel.getCountry(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SOME_BUNDLE_KEY, someID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //remove listeners?
        Timber.i("Detaching %s", this.getClass().getSimpleName());
    }

    @Override
    public boolean onItemTouch(View view, MotionEvent event) {

        boolean actionDown = false;

        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN: {
                actionDown = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                actionDown = true;
                break;
            }
        }

        Timber.i("MotionEvent Action= %s", event.getAction());

        Boolean touched = (Boolean) view.getTag(R.id.touched);
        if (touched == null) touched = false;
        if (actionDown && !touched) {
            Utils.moveUpAnimation(view, getContext());
        } else if (touched) {
            Utils.moveDownAnimation(view, getContext());
        }
        return false;
    }



}
