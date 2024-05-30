package com.example.solutionsproject.fragments.navcontrollers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.ui.NavigationUI;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentHomepageNavContainerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomepageNavContainer extends Fragment {
    private final String TAG = "HomepageNavContainer";
    private FragmentHomepageNavContainerBinding binding;
    private BottomNavigationView bottomNavigationView;
    private MainFacade mainFacade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomepageNavContainerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch(Exception e){
            throw new RuntimeException();
        }

        bottomNavigationView = binding.homepageNavBottomView;

        NavHostFragment navHostFragment = (NavHostFragment)getChildFragmentManager().findFragmentById(R.id.homepage_nav_container);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        mainFacade.setHomepageNavController(navController);
        mainFacade.setCurrentNavController(navController);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.popBackStack(navController.getGraph().getStartDestinationId(), false);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}