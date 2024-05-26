package com.example.solutionsproject.fragments.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentFlashcardQuestionCreatorBinding;

public class FlashcardQuestionCreatorFragment extends Fragment {

    private final String TAG = "FlashcardQuestionCreator_Fragment";
    private MainFacade mainFacade;
    private FragmentFlashcardQuestionCreatorBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFlashcardQuestionCreatorBinding.inflate(inflater, container, false);
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

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.fqcBtnBack.setOnClickListener(v ->{
            mainFacade.getHomeNavController().navigate(R.id.action_flashcardQuestionCreatorFragment_to_homeFragment);
        });

        binding.fqcBtnFlashcard.setOnClickListener(v ->{
            mainFacade.getHomeNavController().navigate(R.id.action_flashcardQuestionCreatorFragment_to_flashcardChoiceCreatorFragment);
        });
    }
}