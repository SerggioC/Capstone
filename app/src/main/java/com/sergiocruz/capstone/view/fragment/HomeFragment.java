package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.TravelsAdapter;
import com.sergiocruz.capstone.databinding.FragmentHomeBinding;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HomeFragment extends Fragment implements BaseAdapter.OnItemClickListener<Travel>, BaseAdapter.OnItemTouchListener {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    public static final String USER_ID_BUNDLE_KEY = "USER_ID_BUNDLE_KEY";
    private static final String CLICKED_POSITION_KEY = "BUNDLE_KEY_CLICKED_POSITION";
    private FragmentHomeBinding binding;
    private TravelsAdapter adapter;
    private String userID;
    private MainViewModel viewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        int clickedPosition = 0;
        if (savedInstanceState != null && savedInstanceState.containsKey(USER_ID_BUNDLE_KEY)) {
            userID = savedInstanceState.getString(USER_ID_BUNDLE_KEY);
            clickedPosition = savedInstanceState.getInt(CLICKED_POSITION_KEY, 0);
        }

        viewModel.setClickedPosition(clickedPosition);
        viewModel.setUserID(userID);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        viewModel.getUser().observe(this, this::onUserInfo);

        setupRecyclerView();

        viewModel.getTravelPacks().observe(this, this::populateRecyclerView);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        Boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            int spanCount = getResources().getInteger(R.integer.spanCount);
            binding.travelsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        } else {
            binding.travelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        binding.travelsRecyclerView.setHasFixedSize(true);
        adapter = new TravelsAdapter(this, this);

        binding.travelsRecyclerView.setAdapter(adapter);
    }

    private void populateRecyclerView(List<Travel> travels) {
        adapter.swapTravelsData(travels);
        prepareExitSharedElementsTransitions();
    }

    private void onUserInfo(User user) {
        if (userID != null && user != null && userID.equals(user.getUserID())) {
            return;
        }

        String message;
        if (user == null || user.getIsAnonymous()) {
            message = getString(R.string.logged_in) + " " + getString(R.string.anonymous);
            userID = null;
        } else {
            message = getString(R.string.logged_in) + " " + getString(R.string.as) + " " + user.getUserName();
            userID = user.getUserID();
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Crashlytics.getInstance().crash(); // Force a crash
    }

    @Override
    public void onItemClick(Travel travel, View view, Integer position) {
        Toast.makeText(getContext(), "Clicked " + travel.getCountry(), Toast.LENGTH_LONG).show();

        viewModel.setClickedPosition(position);
        viewModel.setSelectedTravel(travel);

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

        View viewToMove = view.findViewById(R.id.overlay);
        Boolean touched = (Boolean) viewToMove.getTag(R.id.touched);
        if (touched == null) touched = false;
        if (actionDown && !touched) {
            Utils.moveUpAnimation(viewToMove);
        } else if (touched) {
            Utils.moveDownAnimation(viewToMove);
        }
        return false;
    }

    /**
     * Prepares the shared element transition to the details fragment,
     * as well as the other transitions that affect the flow.
     */
    private void prepareExitSharedElementsTransitions() {
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.grid_exit_transition);
        setExitTransition(transition);

        // A similar mapping is set at the ArticlePagerFragment with a setEnterSharedElementCallback.
        SharedElementCallback exitSharedElementCallback = new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                // Locate the ViewHolder for the clicked position.
                int clickedPosition = viewModel.getClickedPosition();
                RecyclerView.ViewHolder selectedViewHolder = binding.travelsRecyclerView.findViewHolderForAdapterPosition(clickedPosition);
                if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                    return;
                }

                // Map the first shared element name to the child ImageView.
                sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.image));
            }
        };

        setExitSharedElementCallback(exitSharedElementCallback);

        postponeEnterTransition();
    }


}
