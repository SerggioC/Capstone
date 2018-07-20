package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.CommentsAdapter;
import com.sergiocruz.capstone.adapter.ImageListAdapter;
import com.sergiocruz.capstone.databinding.FragmentTravelDetailsBinding;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.CommentsViewModel;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

import timber.log.Timber;

public class TravelDetailsFragment extends Fragment implements BaseAdapter.OnItemClickListener<String>, BaseAdapter.OnItemTouchListener {

    private static final String SOME_BUNDLE_KEY = "SOME_BUNDLE_KEY";
    private FragmentTravelDetailsBinding binding;
    private String someID;
    private MainViewModel viewModel;
    private Travel selectedTravel;
    private CommentsViewModel commentsViewModel;
    private CommentsAdapter commentsAdapter;

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
        selectedTravel = viewModel.getSelectedTravel();
        binding.setTravel(selectedTravel);

        setupToolbar();

        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        commentsViewModel.setRepository(viewModel.getRepository());
        commentsViewModel.getCommentsForTravelID(selectedTravel.getID()).observe(this, this::populateComentsReecyclerView);

        setupCommentsRecyclerView();

        setupImagesRecyclerView();

        binding.writeComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (viewModel.getUser().getValue().getIsAnonymous()) {
                    Utils.showSlimToast(getContext(), getString(R.string.sign_in_to_comment), Toast.LENGTH_LONG);
                    return;
                }
                BottomSheetCommentDialog commentDialog = new BottomSheetCommentDialog();
                String travelID = selectedTravel.getID();
                commentDialog.setTravelID(travelID);
                commentDialog.show(getActivity().getSupportFragmentManager(), BottomSheetCommentDialog.class.getSimpleName());
            }
        });


        return binding.getRoot();
    }

    private void populateComentsReecyclerView(List<Comment> commentList) {
        commentsAdapter.swapData(commentList);
    }

    private void setupCommentsRecyclerView() {
//        FirebaseRepository firebaseRepository = FirebaseRepository.getInstance();
//        String currentUserID = firebaseRepository.getUser().getValue().getUserID();
        String currentUserID = viewModel.getUser().getValue().getUserID();

        commentsAdapter = new CommentsAdapter(currentUserID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.commentsRecyclerView.setLayoutManager(layoutManager);
        binding.commentsRecyclerView.setAdapter(commentsAdapter);

    }



    private void setupToolbar() {
        // Show Options menu
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        // Back navigation
        binding.backArrow.setOnClickListener(v -> getActivity().onBackPressed());

    }

    private void setupImagesRecyclerView() {
        int spanCount = getResources().getInteger(R.integer.detailImagesSpanCount);
        binding.imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        binding.imagesRecyclerView.setHasFixedSize(true);
        ImageListAdapter adapter = new ImageListAdapter(selectedTravel.getImages(), this, this);
        binding.imagesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(String url, View view,  Integer position) {
        Toast.makeText(getContext(), "Clicked position = " + position, Toast.LENGTH_LONG).show();
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