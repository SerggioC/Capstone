package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentFeedbackBinding;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import timber.log.Timber;

public class FeedbackFragment extends Fragment {
    private static final String COMMENT_KEY = "comment_eky";
    private FragmentFeedbackBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        binding.sendButtton.setOnClickListener(this::sendFeedback);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(COMMENT_KEY))
                binding.content.setText(savedInstanceState.getString(COMMENT_KEY));
        }

        return binding.getRoot();
    }

    public void sendFeedback(View view) {
        String content = binding.content.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Utils.showSlimToast(getContext(), getString(R.string.empty_message), Toast.LENGTH_LONG);
            return;
        }

        // Obtain the ViewModel component to get the user info.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse(getString(R.string.app_email))); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + getString(R.string.email));
        String message = content + "\n" +
                viewModel.getUser().getValue().getUserName() + "\n" +
                viewModel.getUser().getValue().getUserEmail();
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            displayAlertDialogBox(intent);
        } else {
            Utils.showSlimToast(getContext(), getString(R.string.has_email), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COMMENT_KEY, binding.content.toString());
    }

    public void displayAlertDialogBox(final Intent intent) {
        DialogInterface.OnClickListener onOkListener = (dialog, id) -> {
            FeedbackFragment.this.startActivity(intent);
            dialog.cancel();
            FeedbackFragment.this.getActivity().getSupportFragmentManager().popBackStack();
        };

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.send_feedback))
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, onOkListener)
                .setNegativeButton(android.R.string.no, (dialog, id) -> dialog.cancel())
                .create();

        alertDialog.setOnShowListener(dialog -> {
            int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color);
        });

        alertDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("Detaching " + this.getClass().getSimpleName());
    }

}
