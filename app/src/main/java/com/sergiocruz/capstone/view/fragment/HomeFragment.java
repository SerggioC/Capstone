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
import com.sergiocruz.capstone.model.TravelData;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HomeFragment extends Fragment implements BaseAdapter.OnItemInteraction<TravelData> {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    private static final String USER_ID_BUNDLE_KEY = "USER_ID_BUNDLE_KEY";
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

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        Utils.animateViewsOnPreDraw(binding.homeFrameLayout, new View[]{binding.usernameTextView});

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(getActivity());

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

        viewModel.getUser().observe(this, this::onUserInfo);

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            int spanCount = getResources().getInteger(R.integer.spanCount);
            binding.travelsRecyclerView.setLayoutManager(
                    new GridLayoutManager(getContext(), spanCount));
        } else {
            binding.travelsRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

        binding.travelsRecyclerView.setHasFixedSize(true);
        adapter = new TravelsAdapter(this);

        binding.travelsRecyclerView.setAdapter(adapter);

        viewModel.getTravelData().observe(this, this::populateRecyclerView);

    }

    private void populateRecyclerView(List<TravelData> travelDataList) {
        if (travelDataList != null) {
            adapter.swapTravelsData(travelDataList);
        }
        //prepareExitSharedElementsTransitions();
    }

    private void onUserInfo(User user) {
        if (userID != null && user != null && userID.equals(user.getUserID())) {
            return;
        }

        String message;
        if (user == null || user.getIsAnonymous() || user.getUserName() == null) {
            message = getString(R.string.logged_in) + " " + getString(R.string.anonymous);
            userID = null;
        } else {
            message = getString(R.string.logged_in) + " " + getString(R.string.as) + " " + user.getUserName();
            userID = user.getUserID();
        }
        Utils.showSlimToast(getContext(), message, Toast.LENGTH_SHORT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Crashlytics.getInstance().crash(); // Force a crash
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
