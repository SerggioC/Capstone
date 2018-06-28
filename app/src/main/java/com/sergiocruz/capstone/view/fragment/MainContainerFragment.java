package com.sergiocruz.capstone.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentMainContainerBinding;
import com.sergiocruz.capstone.viewmodel.MainViewModel;

import static com.sergiocruz.capstone.view.fragment.HomeFragment.ROOT_FRAGMENT_NAME;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainContainerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainContainerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentMainContainerBinding binding;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public MainContainerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainContainerFragment.
     */
    public static MainContainerFragment newInstance(String param1, String param2) {
        MainContainerFragment fragment = new MainContainerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_container, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // Obtain the ViewModel component.
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // variable name "viewModel" in xml <data><variable> + set prefix.
        binding.setViewModel(viewModel);

        // setup the menu and toolbar
        setupToolbar();

        // setup Bottom navigation Menu
        setupBottomNavigation();

        return binding.getRoot();
    }


    private void setupBottomNavigation() {
        binding.bottomNavigationAh.setOnTabSelectedListener(this::switchFragmentContent);

        // navigation item from menu
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(binding.bottomNavigationAh);

        // Enable the Y translation inside the CoordinatorLayout
        binding.bottomNavigationAh.setBehaviorTranslationEnabled(true);

        // Change colors
        binding.bottomNavigationAh.setAccentColor(ContextCompat.getColor(getContext(), R.color.email_light));
        binding.bottomNavigationAh.setInactiveColor(ContextCompat.getColor(getContext(), R.color.black));
        binding.bottomNavigationAh.setDefaultBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        binding.bottomNavigationAh.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
    }

    public void selectHomeNavigation() {
        binding.bottomNavigationAh.setCurrentItem(0);
    }

    private boolean switchFragmentContent(int position, boolean wasSelected) {
        if (wasSelected) return true;

        Fragment fragment = null;
        Boolean isHomeFragment = false;

        switch (position) {
            case 0:
                isHomeFragment = true;
                break;
            case 1:
                fragment = new MapFragment();

                break;
            case 2:

                break;
            case 3:

                break;
        }


        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int stackEntryCount = fragmentManager.getBackStackEntryCount();
        if (isHomeFragment) {
            if (stackEntryCount >= 2) {
                // pop out upto HomeFragment exclusivé
                fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, 0);
            }
        } else if (fragment != null) {

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            String tagName = fragment.getClass().getSimpleName();

            if (stackEntryCount >= 2) {
                // pop out upto HomeFragment exclusivé
                fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, 0);
                transaction.add(R.id.frame_content_holder, fragment);
                if (!tagName.equals(ROOT_FRAGMENT_NAME))
                    transaction.addToBackStack(tagName);
            } else {
                transaction.add(R.id.frame_content_holder, fragment);
                transaction.addToBackStack(tagName);
            }
            transaction.commit();
        }

        return true;
    }


    private void setupToolbar() {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);
        binding.toolbarLayout.userIcon.setOnClickListener(v ->
                Toast.makeText(getContext(), "Should Open Account Drawer", Toast.LENGTH_LONG).show());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logoutFromFirebase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        // Pop out all the fragments including home and add the login fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(ROOT_FRAGMENT_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager
                .beginTransaction()
                .add(R.id.root_fragment_container, new LoginFragment(), LoginFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
