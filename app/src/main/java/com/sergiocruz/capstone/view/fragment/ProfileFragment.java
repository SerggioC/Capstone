package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentProfileBinding;
import com.sergiocruz.capstone.util.Utils;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import timber.log.Timber;

import static com.sergiocruz.capstone.util.SessionUtils.logoutFromFirebase;

public class ProfileFragment extends Fragment {

    private static final String SOME_BUNDLE_KEY = "SOME_BUNDLE_KEY";
    private FragmentProfileBinding binding;
    private String someID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (savedInstanceState != null && savedInstanceState.containsKey(SOME_BUNDLE_KEY)) {
            someID = savedInstanceState.getString(SOME_BUNDLE_KEY);
        }

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        binding.register.setOnClickListener(v -> {
            logoutFromFirebase(getActivity());
            Utils.showSlimToast(getContext(), getString(R.string.login_with_accounts), Toast.LENGTH_LONG);
        });

        return binding.getRoot();
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


}
