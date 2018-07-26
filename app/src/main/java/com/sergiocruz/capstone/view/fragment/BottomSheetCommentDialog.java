package com.sergiocruz.capstone.view.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.ItemBottomsheetCommentLayoutBinding;
import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.FirebaseRepository;
import com.sergiocruz.capstone.repository.Repository;
import com.sergiocruz.capstone.util.AppExecutors;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.text.DecimalFormat;

public class BottomSheetCommentDialog extends BottomSheetDialogFragment {
    private static final String RATING_KEY = "RATING_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";
    private static final String TRAVEL_ID_KEY = "TRAVEL_ID_KEY";
    private static final String IS_EDIT_MODE_KEY = "IS_EDIT_MODE_KEY";
    private static final String COMMENT_ID_KEY = "COMMENT_ID_KEY";
    private String travelID;
    private ItemBottomsheetCommentLayoutBinding binding;
    private MainViewModel viewModel;
    private Repository repository;
    private Boolean isEditMode;
    private Comment commentToEdit;

    public BottomSheetCommentDialog() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        repository = Repository.getInstance(getContext());
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }

    public void setEditMode(Boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void setCommentToEdit(Comment comment) {
        this.commentToEdit = comment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_bottomsheet_comment_layout, container, false);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RATING_KEY))
                binding.ratingBar.setRating(savedInstanceState.getLong(RATING_KEY));
            if (savedInstanceState.containsKey(MESSAGE_KEY))
                binding.commentEditText.setText(savedInstanceState.getString(MESSAGE_KEY));
            if (savedInstanceState.containsKey(TRAVEL_ID_KEY))
                this.travelID = savedInstanceState.getString(TRAVEL_ID_KEY);
            if (savedInstanceState.containsKey(IS_EDIT_MODE_KEY))
                this.isEditMode = savedInstanceState.getBoolean(IS_EDIT_MODE_KEY);
            if (savedInstanceState.containsKey(COMMENT_ID_KEY))
                this.commentToEdit = savedInstanceState.getParcelable(COMMENT_ID_KEY);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        if (isEditMode) {
            binding.setComment(commentToEdit);
        }

        bindBackedUpComment();

        binding.ratingBar.setOnRatingBarChangeListener(this::onRatingChanged);

        binding.sendButton.setOnClickListener(v -> {
            if (!validateData()) return;
            binding.sendButton.setEnabled(false);
            Comment comment = getCommentObject();
            if (isEditMode) {
                FirebaseRepository.getInstance().editComment(comment);
            } else {
                FirebaseRepository.getInstance().sendComment(comment);
            }

            deleteBackedUpComment(comment);
            clearUIDataAndExit();
        });


        return binding.getRoot();
    }

    private void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        String message = decimalFormat.format(rating) + " " +
                getResources().getQuantityString(R.plurals.stars, Math.round(rating)) + "\n";

        if (rating <= 1) { // 0; 0.5; 1
            message += "We are sad! \uD83D\uDE25";
        } else if (rating > 1 && rating <= 2) { // 1.5; 2
            message += "Just two stars? \uD83D\uDE15";
        } else if (rating == 2.5) {
            message += "OK \uD83D\uDE05";
        } else if (rating == 3) {
            message += "Good! \uD83D\uDC4D";
        } else if (rating > 3 && rating <= 4) { // 3.5; 4
            message += "Great!! \uD83D\uDE03";
        } else if (rating > 4) { // 4.5; 5
            message += "\uD83C\uDF1F Awesome!! \uD83C\uDF1F \uD83D\uDE0D";
        }

        Utils.showSlimToast(getContext(), message, Toast.LENGTH_SHORT);
        if (rating > 0) {
            binding.ratingTextview.setText(getString(R.string.your_rating));
        } else {
            binding.ratingTextview.setText(getString(R.string.rating_required));
        }
    }

    private void clearUIDataAndExit() {
        binding.ratingBar.setOnRatingBarChangeListener(null);
        binding.ratingBar.setRating(0);
        binding.commentEditText.setText(null);
        this.dismiss();
    }

    private Boolean validateData() {
        Boolean isValid = true;

        // normal text
        binding.ratingTextview.setTextColor(getResources().getColor(R.color.grey600));
        binding.ratingTextview.setTypeface(null, Typeface.NORMAL);
        binding.ratingTextview.setText(getString(R.string.your_rating));
        if (binding.ratingBar.getRating() == 0) {
            binding.ratingTextview.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            binding.ratingTextview.setText(getString(R.string.rating_required));
            binding.ratingTextview.setTypeface(binding.ratingTextview.getTypeface(), Typeface.BOLD);
            isValid = false;
        }

        // clear error
        binding.commentEditText.setError(null);
        if (TextUtils.isEmpty(binding.commentEditText.getText().toString().trim())) {
            binding.commentEditText.setError(getString(R.string.empty_message));
            isValid = false;
        }
        return isValid;
    }

    @NonNull
    private Comment getCommentObject() {
        String commentText = binding.commentEditText.getText().toString().trim();
        Float rating = binding.ratingBar.getRating();
        User user = viewModel.getUser().getValue();
        String currentUserID = user.getUserID();
        String id = isEditMode ? commentToEdit.getCommentID() : travelID + currentUserID; /* custom unique key for Room, ignored for Firebase*/

        return new Comment(
                id,
                currentUserID,
                travelID,
                commentText,
                System.currentTimeMillis(),
                rating,
                user.getUserName(),
                user.getUserPhotoUri());
    }

    private void deleteBackedUpComment(Comment comment) {
        new AppExecutors().diskIO().execute(() -> {
            Comment backedUpComment = repository
                    .getBackedUpCommentByID(travelID + comment.getUserID());
            if (backedUpComment != null) {
                Repository.getInstance(getContext()).deleteBackedUpComment(comment.getCommentID());
            }
        });
    }

    private void bindBackedUpComment() {
        new AppExecutors().diskIO().execute(() -> {
            Comment backedUpComment = repository
                    .getBackedUpCommentByID(travelID + viewModel.getUser().getValue().getUserID());
            if (backedUpComment != null) {
                binding.setComment(backedUpComment);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void backupWrittenMessage() {
        String commentText = binding.commentEditText.getText().toString();
        Float rating = binding.ratingBar.getRating();

        if (rating == 0 && TextUtils.isEmpty(commentText))
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Comment comment = getCommentObject();
                repository.backUpComment(comment);
                return null;
            }
        }.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        float rating = binding.ratingBar.getRating();
        String commentText = binding.commentEditText.getText().toString().trim();

        if (rating != 0)
            outState.putFloat(RATING_KEY, rating);

        if (!TextUtils.isEmpty(commentText))
            outState.putString(MESSAGE_KEY, commentText);

        outState.putString(TRAVEL_ID_KEY, travelID);
        outState.putBoolean(IS_EDIT_MODE_KEY, isEditMode);
        outState.putParcelable(COMMENT_ID_KEY, commentToEdit);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        backupWrittenMessage();
    }


}
