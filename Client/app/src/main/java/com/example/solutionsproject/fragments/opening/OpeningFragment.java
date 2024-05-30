package com.example.solutionsproject.fragments.opening;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.opening.OnBoardingAdapter;
import com.example.solutionsproject.classes.opening.OnBoardingModel;
import com.example.solutionsproject.databinding.FragmentOpeningBinding;

import java.util.ArrayList;
import java.util.List;

public class OpeningFragment extends Fragment {
    private OnBoardingAdapter onBoardingAdapter;
    private MainFacade mainFacade;
    private FragmentOpeningBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOpeningBinding.inflate(inflater, container, false);
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

        List<OnBoardingModel> onBoardingModels = new ArrayList<>();
        onBoardingModels.add(new OnBoardingModel(R.drawable.onboarding_img_1, "Numerous free trial courses", "Free courses for you to discover!"));
        onBoardingModels.add(new OnBoardingModel(R.drawable.onboarding_img_2, "Quick and easy learning", "Accessible services provided in various ways, to accompany all your learning styles!"));
        onBoardingModels.add(new OnBoardingModel(R.drawable.onboarding_img_3, "Create your own study plan", "Study at your own pace! Making yourself consistent and motivated."));

        onBoardingAdapter = new OnBoardingAdapter(onBoardingModels);
        binding.viewPagerOnboarding.setAdapter(onBoardingAdapter);

        // Page Dot Indicators
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(mainFacade.getMainActivity().getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(mainFacade.getMainActivity().getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            binding.linearOnboardingIndicator.addView(indicators[i]);
        }

        setCurrentOnBoardingIndicator(0);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
            super.onPageSelected(position);
            setCurrentOnBoardingIndicator(position);
            }
        });

        binding.btnSkipOpening.setOnClickListener(v ->
            binding.viewPagerOnboarding.setCurrentItem(onBoardingAdapter.getItemCount() - 1)
        );

        binding.btnLogin.setOnClickListener(v ->
            mainFacade.getOpeningNavController().navigate(R.id.action_openingFragment_to_loginFragment)
        );

        binding.btnSignup.setOnClickListener(v ->
            mainFacade.getOpeningNavController().navigate(R.id.action_openingFragment_to_signupFragment)
        );
    }

    private void setCurrentOnBoardingIndicator(int index) {
        int childCount = binding.linearOnboardingIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.linearOnboardingIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(mainFacade.getMainActivity().getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(mainFacade.getMainActivity().getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }

        if (index == onBoardingAdapter.getItemCount() - 1) {
            if (binding.btnSkipOpening.getVisibility() != View.GONE) binding.btnSkipOpening.setVisibility(View.GONE);
            if (binding.authButtons.getVisibility() != View.VISIBLE)
                binding.authButtons.setVisibility(View.VISIBLE);
        } else {
            if (binding.btnSkipOpening.getVisibility() != View.VISIBLE) binding.btnSkipOpening.setVisibility(View.VISIBLE);
            if (binding.authButtons.getVisibility() != View.GONE) binding.authButtons.setVisibility(View.GONE);
        }
    }
}