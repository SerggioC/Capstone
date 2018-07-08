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

import timber.log.Timber;

public class HomeFragment extends Fragment implements BaseAdapter.OnItemClickListener<Travel> {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;
    private MyTravelsAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        viewModel.getUser().observe(this, this::onUserInfo);

        setupRecyclerView();

        viewModel.getTravelPacks().observe(this, this::populateRecyclerView);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.travelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.travelsRecyclerView.setHasFixedSize(true);
        adapter = new MyTravelsAdapter(this);
        binding.travelsRecyclerView.setAdapter(adapter);
    }

    private void populateRecyclerView(List<Travel> travels) {
        adapter.swapData(travels);
    }

    private void onUserInfo(User user) {
        String message;
        if (user.getIsAnonymous()) {
            message = getString(R.string.logged_in) + " " + getString(R.string.anonymous);
        } else {
            message = getString(R.string.logged_in) + " " + getString(R.string.as) + " " + user.getUserName();
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Crashlytics.getInstance().crash(); // Force a crash
    }

    @Override
    public void onItemClick(Travel travel) {
        Toast.makeText(getContext(), "click click click" + travel.getCountry(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //remove listeners?
        Timber.i("Detaching " + this.getClass().getSimpleName());
    }

}
