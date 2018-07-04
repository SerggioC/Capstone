package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.TravelsAdapter;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    private com.sergiocruz.capstone.databinding.FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        viewModel.getUser().observe(this, this::onUserInfo);

        viewModel.getTravelPacks().observe(this, this::populateRecyclerView);

        return binding.getRoot();
    }

    private void populateRecyclerView(List<Travel> travels) {
        binding.travelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.travelsRecyclerView.setHasFixedSize(true);
        TravelsAdapter adapter = new TravelsAdapter(travels);
        binding.travelsRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //remove listeners;
    }


    private void onUserInfo(User user) {
        Toast.makeText(getContext(), "Logged in as " + user.getUserName(), Toast.LENGTH_LONG).show();
    }
}
