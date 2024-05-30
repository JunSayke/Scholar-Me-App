package com.example.solutionsproject.fragments.opening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentSplashArtBinding;
import com.example.solutionsproject.model.gson.data.UserGson;

public class SplashArtFragment extends Fragment {

    private final String TAG = "SplashArt_Fragment";
    private MainFacade mainFacade;
    private FragmentSplashArtBinding binding;
    private static final long SPLASH_SCREEN_DURATION = 1000;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSplashArtBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(() -> {
            if (mainFacade.isLoggedIn()) {
                mainFacade.getOpeningNavController().navigate(R.id.action_splashArtFragment_to_homepageNavController);
            } else {
                mainFacade.getOpeningNavController().navigate(R.id.action_splashArtFragment_to_openingFragment);
            }
        }, SPLASH_SCREEN_DURATION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}