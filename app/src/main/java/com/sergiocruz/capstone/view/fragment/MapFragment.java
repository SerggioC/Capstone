package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentMapBinding;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import timber.log.Timber;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate view and obtain an instance of the binding class.
        FragmentMapBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Obtain the ViewModel component.
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        viewModel
                .getTravelPacks()
                .observe(this, travels -> {
                            if (travels == null) return;

                            // Add a marker for each location
                            for (Travel travel : travels) {
                                LatLng location = new LatLng(
                                        travel.getLatitude(),
                                        travel.getLongitude());

                                Marker marker = mMap.addMarker(
                                        new MarkerOptions()
                                                .position(location)
                                                .title(travel.getName())
                                                .visible(true));

                                marker.setTag(travel);
                            }

                            googleMap.setOnInfoWindowClickListener(marker -> {
                                if (marker.getTitle().equals(getString(R.string.lisbon))) return;

                                viewModel.setSelectedTravel((Travel) marker.getTag());

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setReorderingAllowed(true)
                                        .add(R.id.root_fragment_container, new TravelDetailsFragment(), TravelDetailsFragment.class.getSimpleName())
                                        .addToBackStack(TravelDetailsFragment.class.getSimpleName())
                                        .commit();
                            });
                        }
                );

        // Add a marker in Lisbon, Portugal, and move the camera.
        LatLng lisbon = new LatLng(38.736946, -9.142685);
        mMap.addMarker(new MarkerOptions().position(lisbon).title(getString(R.string.lisbon)).visible(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lisbon));
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("Detaching " + this.getClass().getSimpleName());
    }

}
