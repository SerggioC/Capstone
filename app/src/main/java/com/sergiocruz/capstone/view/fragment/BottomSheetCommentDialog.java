package com.sergiocruz.capstone.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.sergiocruz.capstone.R;

import timber.log.Timber;

public class BottomSheetCommentDialog extends BottomSheetDialogFragment {
    public BottomSheetCommentDialog() {
        super();
    }

    private static void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Timber.i("rating " + rating);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_bottomsheet_comment_layout, container, false);
        ((AppCompatRatingBar) view.findViewById(R.id.ratingBar)).setOnRatingBarChangeListener(BottomSheetCommentDialog::onRatingChanged);
        return view;
    }
}
