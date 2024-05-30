package com.example.solutionsproject.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.adapter.FlashcardChoiceListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentFlashcardChoiceCreatorBinding;
import com.example.solutionsproject.model.gson.data.FlashcardChoiceGson;
import com.example.solutionsproject.model.gson.data.FlashcardGson;
import com.example.solutionsproject.model.gson.data.GsonData;

import java.util.List;

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

        FlashcardChoiceCreatorFragmentArgs args = FlashcardChoiceCreatorFragmentArgs.fromBundle(getArguments());
        FlashcardGson flashcard = args.getFlashcard();

        binding.fccTvQuestion.setText(flashcard.getQuestion());

        mainFacade.getFlashcardChoices(new ScholarMeServer.ResponseListener<>() {
            @Override
            public void onSuccess(List<FlashcardChoiceGson> data) {
                mainFacade.makeToast("Flashcard choices loaded", Toast.LENGTH_SHORT);
                binding.fccListChoices.setAdapter(new FlashcardChoiceListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data
                ));

                binding.fccListChoices.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        }, flashcard.getFlashcardId());

        binding.fccBtnNewChoice.setOnClickListener(v -> {
            mainFacade.addFlashcardChoice(new ScholarMeServer.ResponseListener<>() {
                @Override
                public void onSuccess(GsonData data) {
                    mainFacade.makeToast("Flashcard choice added", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            }, flashcard.getFlashcardId(), binding.fccEttChoice.getText().toString(), binding.fccCbChoice.isChecked());
        });

        binding.fccBtnBack.setOnClickListener(v -> {
            FlashcardChoiceCreatorFragmentDirections.ActionFlashcardChoiceCreatorFragmentToFlashcardQuestionCreatorFragment action = FlashcardChoiceCreatorFragmentDirections.actionFlashcardChoiceCreatorFragmentToFlashcardQuestionCreatorFragment(args.getFlashcardSet());
            mainFacade.getHomeNavController().navigate(action);
        });

        binding.fccBtnDelete.setOnClickListener(v -> {
            mainFacade.deleteFlashcard(new ScholarMeServer.ResponseListener<>() {
                @Override
                public void onSuccess(GsonData data) {
                    mainFacade.makeToast("Flashcard deleted", Toast.LENGTH_SHORT);
                    FlashcardChoiceCreatorFragmentDirections.ActionFlashcardChoiceCreatorFragmentToFlashcardQuestionCreatorFragment action = FlashcardChoiceCreatorFragmentDirections.actionFlashcardChoiceCreatorFragmentToFlashcardQuestionCreatorFragment(args.getFlashcardSet());
                    mainFacade.getHomeNavController().navigate(action);
                }

                @Override
                public void onFailure(String message) {
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            }, flashcard.getFlashcardId());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}