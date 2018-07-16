package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.ImageListAdapter;
import com.sergiocruz.capstone.databinding.FragmentTravelDetailsBinding;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import timber.log.Timber;

public class TravelDetailsFragment extends Fragment implements BaseAdapter.OnItemClickListener<String>, BaseAdapter.OnItemTouchListener {

    private static final String SOME_BUNDLE_KEY = "SOME_BUNDLE_KEY";
    private FragmentTravelDetailsBinding binding;
    private String someID;
    private MainViewModel viewModel;

    public TravelDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_details, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        if (savedInstanceState != null && savedInstanceState.containsKey(SOME_BUNDLE_KEY)) {
            someID = savedInstanceState.getString(SOME_BUNDLE_KEY);
        }

        // variable name "travel" in xml <data><variable> + set prefix.
        binding.setTravel(viewModel.getSelectedTravel());

        setupToolbar();

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupToolbar() {
        // Show Options menu
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        // Back navigation
        binding.backArrow.setOnClickListener(v -> getActivity().onBackPressed());

    }


    private void setupRecyclerView() {
        int spanCount = getResources().getInteger(R.integer.detailImagesSpanCount);
        binding.imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        binding.imagesRecyclerView.setHasFixedSize(true);
        ImageListAdapter adapter = new ImageListAdapter(viewModel.getSelectedTravel().getImages(), this, this);
        binding.imagesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(String url, View view,  Integer position) {
        Toast.makeText(getContext(), "Clicked " + url + " position = " + position, Toast.LENGTH_LONG).show();
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

        View viewToAnimate = view.findViewById(R.id.image_item);
        Boolean touched = (Boolean) viewToAnimate.getTag(R.id.touched);
        if (touched == null) touched = false;
        if (actionDown && !touched) {
            Utils.zoomInAnimation(viewToAnimate);
        } else if (touched) {
            Utils.zoomOutAnimation(viewToAnimate);
        }
        return false;
    }



}
