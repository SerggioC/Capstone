package com.sergiocruz.capstone.view;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sergiocruz.capstone.R;

import java.util.Objects;

public class RegisterDialog extends DialogFragment {
    private OnOKClickedCallback onOKClickedCallback;
    private OnCancelClickedCallback onCancelClickedCallback;
    private String email;

    public static RegisterDialog newInstance(OnOKClickedCallback onOKClickedCallback, OnCancelClickedCallback onCancelClickedCallback, String email) {
        RegisterDialog dialog = new RegisterDialog();
        dialog.onOKClickedCallback = onOKClickedCallback;
        dialog.onCancelClickedCallback = onCancelClickedCallback;
        dialog.email = email;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setCancelable(false)
                .setMessage(getString(R.string.email) + "\n" +
                        email + "\n" +
                        getString(R.string.not_in_database) + "\n" +
                        getString(R.string.register_email))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> onOKClickedCallback.onOKClicked())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> onCancelClickedCallback.onCancelClicked())
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public interface OnOKClickedCallback {
        void onOKClicked();
    }

    public interface OnCancelClickedCallback {
        void onCancelClicked();
    }


}
