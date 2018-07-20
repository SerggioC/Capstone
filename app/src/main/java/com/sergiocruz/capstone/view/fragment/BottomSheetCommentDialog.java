package com.sergiocruz.capstone.view.fragment;

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

public class BottomSheetCommentDialog extends BottomSheetDialogFragment {
    public static final String RATING_KEY = "RATING_KEY";
    public static final String MESSAGE_KEY = "MESSAGE_KEY";
    private String travelID;
    private ItemBottomsheetCommentLayoutBinding binding;
    private MainViewModel viewModel;
    private Repository repository;

    public BottomSheetCommentDialog() {
        super();
    }

    private void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        String message = Math.round(rating) + " " +
                getResources().getQuantityString(R.plurals.stars, Math.round(rating)) + "\n";

        if (rating <= 1) {
            message += "We are sad! \uD83D\uDE25";
        } else if (rating > 1 && rating <= 2){
            message += "Just two stars? \uD83D\uDE15";
        } else if (rating > 2 && rating <= 3) {
            message += "Good! \uD83D\uDC4D";
        } else if (rating == 4) {
            message += "Great!! \uD83D\uDE03";
        } else if (rating > 4){
            message += "\uD83C\uDF1F Awesome!! \uD83E\uDD29 \uD83D\uDE0D";
        }

        Utils.showSlimToast(getContext(), message, Toast.LENGTH_SHORT);
        if (rating > 0) {
            binding.ratingTextview.setText(getString(R.string.your_rating));
        } else {
            binding.ratingTextview.setText(getString(R.string.rating_required));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        repository = Repository.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_bottomsheet_comment_layout, container, false);
        binding.ratingBar.setOnRatingBarChangeListener(this::onRatingChanged);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RATING_KEY))
                binding.ratingBar.setRating(savedInstanceState.getLong(RATING_KEY));
            if (savedInstanceState.containsKey(MESSAGE_KEY))
                binding.commentEditText.setText(savedInstanceState.getString(MESSAGE_KEY));
        }
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        getBackedUpComment();

        binding.sendButton.setOnClickListener(v -> {
            if (!validateData()) return;
            binding.sendButton.setEnabled(false);
            Comment comment = getCommentObject();
            FirebaseRepository.getInstance().sendComment(comment);
            deleteBackedUpComment(comment);
            clearUIDataAndExit();
        });

        return binding.getRoot();
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

        return new Comment(
                travelID + currentUserID, /* custom unique key for Room ignored for Firebase*/
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

    private void getBackedUpComment() {
        new AppExecutors().diskIO().execute(() -> {
            Comment backedUpComment = repository
                    .getBackedUpCommentByID(travelID + viewModel.getUser().getValue().getUserID());
            if (backedUpComment != null) {
                binding.setCommente(backedUpComment);
            }
        });
    }

    private void backupWrittenMessage() {
        String commentText = binding.commentEditText.getText().toString();
        Float rating = binding.ratingBar.getRating();

        if (rating == 0 && TextUtils.isEmpty(commentText)) {
            return;
        }

        Comment comment = getCommentObject();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Repository.getInstance(getContext()).backUpComment(comment);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        backupWrittenMessage();
    }

    public void setTravelID(String travelID) {
        this.travelID = travelID;
    }
}
