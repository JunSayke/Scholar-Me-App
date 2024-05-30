package com.example.solutionsproject.fragments.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.FlashcardListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentFlashcardQuestionCreatorBinding;
import com.example.solutionsproject.model.gson.data.FlashcardGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;
import com.example.solutionsproject.model.gson.data.GsonData;

import java.util.List;

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

        FlashcardQuestionCreatorFragmentArgs args = FlashcardQuestionCreatorFragmentArgs.fromBundle(getArguments());
        FlashcardSetGson flashcardSet = args.getFlashcardSet();

        mainFacade.getFlashcardSetFlashcards(new ScholarMeServer.ResponseListener<List<FlashcardGson>>() {
            @Override
            public void onSuccess(List<FlashcardGson> data) {
                mainFacade.makeToast("Flashcards loaded!", Toast.LENGTH_SHORT);

                binding.fqcListQuestions.setAdapter(new FlashcardListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        flashcard -> {
                            FlashcardQuestionCreatorFragmentDirections.ActionFlashcardQuestionCreatorFragmentToFlashcardChoiceCreatorFragment action =
                                    FlashcardQuestionCreatorFragmentDirections.actionFlashcardQuestionCreatorFragmentToFlashcardChoiceCreatorFragment(flashcard, flashcardSet);
                            mainFacade.getHomeNavController().navigate(action);
                        }
                ));
                binding.fqcListQuestions.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        }, flashcardSet.getFlashcardSetId());

        binding.fqcBtnFlashcard.setOnClickListener(v -> {
            mainFacade.createFlashcard(new ScholarMeServer.ResponseListener<>() {
                @Override
                public void onSuccess(GsonData data) {
                    mainFacade.makeToast("Flashcard created!", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            }, flashcardSet.getFlashcardSetId(), binding.fqcEttQuestion.getText().toString());
        });

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
    }
}