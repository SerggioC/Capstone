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
import com.sergiocruz.capstone.viewmodel.MainViewModel;
import com.sergiocruz.capstone.viewmodel.TravelDetailsViewModel;

import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.capstone.model.Status.LOADING;
import static com.sergiocruz.capstone.model.Status.SUCCESS;

public class TravelDetailsFragment extends Fragment implements BaseAdapter.OnItemClickListener<String>, BaseAdapter.OnItemTouchListener, CommentsAdapter.OnEditClickListener, BottomSheetCommentDialog.NewCommentInterface {

    private static final String TRAVEL_ID_KEY = "TRAVEL_ID_KEY";
    private FragmentTravelDetailsBinding binding;
    private String travelID;
    private MainViewModel viewModel;
    private Travel selectedTravel;
    private TravelDetailsViewModel detailsViewModel;
    private CommentsAdapter commentsAdapter;
    private Boolean hasNewComment = false;

    public TravelDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_details, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(TRAVEL_ID_KEY)) {
            travelID = savedInstanceState.getString(TRAVEL_ID_KEY);
        }

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        // variable name "travel" in xml <data><variable> + set prefix.
        selectedTravel = viewModel.getSelectedTravel();

        setupToolbar();

        detailsViewModel = ViewModelProviders.of(this).get(TravelDetailsViewModel.class);
        detailsViewModel.setRepository(viewModel.getRepository());  // init vars
        detailsViewModel.setTravel(selectedTravel);                 // init vars

        binding.setViewModel(detailsViewModel);

        String currentUserID = viewModel.getUser().getValue().getUserID();

        setupCommentsRecyclerView(currentUserID); // user ID to enable editing comment

        setupImagesRecyclerView();

        binding.writeComment.setOnClickListener(v -> onClickToComment(v, false, null));

        binding.fab.setOnClickListener(v -> {
            Boolean result = detailsViewModel.saveToFavorites(selectedTravel);
            Utils.showSlimToast(getContext(), result ? "Favorite saved.\nLong Press to remove." : "Error. not saved.", Toast.LENGTH_LONG);
        });

        binding.fab.setOnLongClickListener(v -> {
            detailsViewModel.removeFromFavorites(selectedTravel.getID());
            Utils.showSlimToast(getContext(), "Removed from Favorites.", Toast.LENGTH_LONG);
            return true;
        });

        return binding.getRoot();
    }

    private void setupCommentsRecyclerView(String currentUserID) {
        detailsViewModel.setCurrentStatus(LOADING);

        detailsViewModel.getCommentsForTravelID().observe(this, this::populateCommentsRecyclerView);


        commentsAdapter = new CommentsAdapter(currentUserID, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.commentsRecyclerView.setLayoutManager(layoutManager);
        binding.commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void populateCommentsRecyclerView(List<Comment> commentList) {
        commentsAdapter.swapData(commentList);
        detailsViewModel.setCurrentStatus(SUCCESS);
        if (hasNewComment) {
            binding.nestedScrollview.fullScroll(View.FOCUS_DOWN);
            hasNewComment = false;
        }
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
    public void onItemClick(String url, View view, Integer position) {
        Toast.makeText(getContext(), "Clicked position = " + position, Toast.LENGTH_LONG).show();
    }

    private void onClickToComment(View v, Boolean editMode, Comment comment) {
        if (viewModel.getUser().getValue().getIsAnonymous()) {
            Utils.showSlimToast(getContext(), getString(R.string.sign_in_to_comment), Toast.LENGTH_LONG);
            return;
        }
        BottomSheetCommentDialog commentDialog = new BottomSheetCommentDialog();
        String travelID = selectedTravel.getID();
        commentDialog.setTravelID(travelID);
        commentDialog.setEditMode(editMode);
        commentDialog.setCommentToEdit(comment);
        commentDialog.setNewCommentInterface(this);
        commentDialog.show(getActivity().getSupportFragmentManager(), BottomSheetCommentDialog.class.getSimpleName());
    }

    @Override
    public void onNewComment() {
        hasNewComment = true;
    }

    @Override
    public void onEditClick(Comment comment) {
        onClickToComment(null, true, comment);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TRAVEL_ID_KEY, travelID);
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