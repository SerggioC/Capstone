package com.sergiocruz.capstone.view;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class RegisterDialog extends DialogFragment {
    private static onOKClicked onOKClickedInterface;
    private onCancelClicked onCancelClickedInterface;
    private String email;

    public static RegisterDialog newInstance(onOKClicked onOKClickedInterface, onCancelClicked onCancelClickedInterface, String email) {
        RegisterDialog dialog = new RegisterDialog();
        dialog.onOKClickedInterface = onOKClickedInterface;
        dialog.onCancelClickedInterface = onCancelClickedInterface;
        dialog.email = email;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage("Register with this email?\n" + email)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> onOKClickedInterface.onOKClicked())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> onCancelClickedInterface.onCancelClicked())
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public interface onOKClicked {
        void onOKClicked();
    }

    public interface onCancelClicked {
        void onCancelClicked();
    }


}
