package com.sergiocruz.capstone.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentPagerBinding;


/**
 * Main Fragment with App content, a ViewPager with bottom navigation in this case.
 */
public class PagerFragment extends Fragment {

    public PagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        FragmentPagerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pager, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

}
