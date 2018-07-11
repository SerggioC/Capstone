package com.sergiocruz.capstone.view.fragment;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.adapter.BaseAdapter;
import com.sergiocruz.capstone.adapter.TravelsAdapter;
import com.sergiocruz.capstone.databinding.FragmentHomeBinding;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import java.util.List;

import timber.log.Timber;

public class HomeFragment extends Fragment implements BaseAdapter.OnItemClickListener<Travel>, BaseAdapter.OnItemTouchListener {

    public static final String ROOT_FRAGMENT_NAME = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;
    private TravelsAdapter adapter;

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
        adapter = new TravelsAdapter(this, this);
        binding.travelsRecyclerView.setAdapter(adapter);
    }

    private void populateRecyclerView(List<Travel> travels) {
        adapter.swapTravelsData(travels);
    }

    private void onUserInfo(User user) {
        String message;

        if (user == null || user.getIsAnonymous()) {
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
        Toast.makeText(getContext(), "Clicked " + travel.getCountry(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //remove listeners?
        Timber.i("Detaching " + this.getClass().getSimpleName());
    }

    @Override
    public boolean onItemTouch(View view, MotionEvent event) {

        boolean actionDown = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Timber.w("Action DOWN");
                actionDown = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Timber.w("Action MOVE");
                actionDown = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                Timber.w("Action UP");
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                Timber.w("Action CANCEL");
                break;
            }
        }

        Boolean touched = (Boolean) view.getTag(R.id.touched);
        if (touched == null) touched = false;
        if (actionDown && !touched) {
            moveUpAnimation(view);
        } else if (touched) {
            moveDownAnimation(view);
        }
        return false;
    }

    public int dpToPx(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private void moveUpAnimation(View view) {
        Timber.i("Going UP");
        view.setTag(R.id.touched, true);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.overlay), "translationY", dpToPx(-54));
        animation.setDuration(200);
        animation.start();

    }

    private void moveDownAnimation(View view) {
        Timber.i("Going Down");
        view.setTag(R.id.touched, false);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.overlay), "translationY", dpToPx(0));
        animation.setDuration(1000);
        animation.setStartDelay(2500);
        animation.start();
    }

}
