package com.example.solutionsproject.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentFlashcardChoiceCreatorBinding;

public class FlashcardChoiceCreatorFragment extends Fragment {

    private final String TAG = "FlashcardChoiceCreator_Fragment";
    private MainFacade mainFacade;
    private FragmentFlashcardChoiceCreatorBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFlashcardChoiceCreatorBinding.inflate(inflater, container, false);
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
        binding.fccBtnBack.setOnClickListener(v ->{
            mainFacade.getHomeNavController().navigate(R.id.action_flashcardChoiceCreatorFragment_to_flashcardQuestionCreatorFragment);
        });
    }
}