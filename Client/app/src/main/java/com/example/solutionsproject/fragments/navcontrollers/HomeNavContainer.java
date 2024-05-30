package com.example.solutionsproject.fragments.navcontrollers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentHomeNavContainerBinding;

public class HomeNavContainer extends Fragment {

    private final String TAG = "HomeNavContainer_Fragment";
    private MainFacade mainFacade;
    private FragmentHomeNavContainerBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeNavContainerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.home_nav_container);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        mainFacade.setHomeNavController(navController);
        mainFacade.setCurrentNavController(navController);

        navController.popBackStack(navController.getGraph().getStartDestinationId(), false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}