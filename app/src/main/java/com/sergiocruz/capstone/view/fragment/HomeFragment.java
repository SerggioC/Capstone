package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.MyTravelsAdapter;
import com.sergiocruz.capstone.databinding.FragmentHomeBinding;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements BaseAdapter.OnItemClickListener, BaseAdapter.OnItemTouchListener {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;

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
        MyTravelsAdapter adapter = new MyTravelsAdapter(travels, this, this);
        binding.travelsRecyclerView.setAdapter(adapter);
    }

    private void onUserInfo(User user) {
        Toast.makeText(getContext(), "Logged in as " + user.getUserName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Crashlytics.getInstance().crash(); // Force a crash
    }

    @Override
    public void onItemClick(Object object) {

    }

    @Override
    public void onItemTouch(View view) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //remove listeners?
    }

}
