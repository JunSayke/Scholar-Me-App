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
import android.widget.TextView;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentAccountNavContainerBinding;

public class AccountNavContainer extends Fragment {

    private final String TAG = "AccountNavContainer_Fragment";
    private MainFacade mainFacade;
    private FragmentAccountNavContainerBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountNavContainerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.account_nav_container);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        mainFacade.setAccountNavController(navController);
        mainFacade.setCurrentNavController(navController);

        navController.popBackStack(navController.getGraph().getStartDestinationId(), false);

        mainFacade.getAccountNavController().addOnDestinationChangedListener((controller, destination, arguments) -> {
            View mainHeader = binding.accountNavContainerHeader;
            TextView headerText = binding.accountNavTvTitle;

            if (destination.getId() == R.id.editAccountFragment) {
                mainHeader.setVisibility(View.VISIBLE);
                headerText.setText(R.string.title_edit_account);
            } else if (destination.getId() == R.id.favoritesFragment) {
                mainHeader.setVisibility(View.VISIBLE);
                headerText.setText(R.string.title_favorite);
            } else if (destination.getId() == R.id.helpFragment) {
                mainHeader.setVisibility(View.VISIBLE);
                headerText.setText(R.string.title_help);
            } else if (destination.getId() == R.id.settingsFragment) {
                mainHeader.setVisibility(View.VISIBLE);
                headerText.setText(R.string.title_setting);
            } else{
                mainHeader.setVisibility(View.GONE);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.accountNavBtnBack.setOnClickListener(v -> mainFacade.getAccountNavController().popBackStack());
    }
}