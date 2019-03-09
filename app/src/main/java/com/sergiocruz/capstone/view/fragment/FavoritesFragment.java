package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.TravelsAdapter;
import com.sergiocruz.capstone.databinding.FragmentFavoritesBinding;
import com.sergiocruz.capstone.model.TravelData;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

import timber.log.Timber;

public class FavoritesFragment extends Fragment implements BaseAdapter.OnItemClickListener<TravelData>, BaseAdapter.OnItemTouchListener {
    public static final String USER_ID_BUNDLE_KEY = "USER_ID_BUNDLE_KEY";
    private static final String CLICKED_POSITION_KEY = "BUNDLE_KEY_CLICKED_POSITION";
    private FragmentFavoritesBinding binding;
    private TravelsAdapter adapter;
    private String userID;
    private MainViewModel viewModel;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false);

        Utils.animateViewsOnPreDraw(binding.homeFrameLayout, new View[]{binding.usernameTextView});

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(getActivity());

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        int clickedPosition = 0;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(USER_ID_BUNDLE_KEY))
                userID = savedInstanceState.getString(USER_ID_BUNDLE_KEY);
            if (savedInstanceState.containsKey(CLICKED_POSITION_KEY))
                clickedPosition = savedInstanceState.getInt(CLICKED_POSITION_KEY, 0);
        }

        viewModel.setClickedPosition(clickedPosition);
        viewModel.setUserID(userID);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        Boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            int spanCount = getResources().getInteger(R.integer.spanCount);
            binding.travelsRecyclerView.setLayoutManager(
                    new GridLayoutManager(getContext(), spanCount));
        } else {
            binding.travelsRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

        binding.travelsRecyclerView.setHasFixedSize(true);
        adapter = new TravelsAdapter(this, this);

        binding.travelsRecyclerView.setAdapter(adapter);
        if (!viewModel.getUser().getValue().getIsAnonymous()) {
            viewModel.getFavoriteTravelData().observe(this, this::populateRecyclerView);
        }


    }

    private void populateRecyclerView(List<TravelData> travelDataList) {
        if (travelDataList != null) {
            adapter.swapTravelsData(travelDataList);
        }
        //prepareExitSharedElementsTransitions();
    }

    @Override
    public void onItemClick(TravelData travelData, View view, Integer position) {
        viewModel.setClickedPosition(position);
        viewModel.setSelectedTravel(travelData.getTravel());

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
//                .addSharedElement(view, travel.getID())
                .add(R.id.root_fragment_container, new TravelDetailsFragment(), TravelDetailsFragment.class.getSimpleName())
                .addToBackStack(TravelDetailsFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_ID_BUNDLE_KEY, userID);
        outState.putInt(CLICKED_POSITION_KEY, viewModel.getClickedPosition());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("Detaching %s", this.getClass().getSimpleName());
    }

    @Override
    public boolean onItemTouch(View view, MotionEvent event) {
        return Utils.upDownAnimation(view, event);
    }

}