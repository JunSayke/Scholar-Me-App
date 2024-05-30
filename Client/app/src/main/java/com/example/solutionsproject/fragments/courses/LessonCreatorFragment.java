package com.example.solutionsproject.fragments.courses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentLessonCreatorBinding;
import com.example.solutionsproject.model.gson.data.GsonData;

public class LessonCreatorFragment extends Fragment {

    private final String TAG = "LessonCreator_Fragment";
    private MainFacade mainFacade;
    private FragmentLessonCreatorBinding binding;
    private int courseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLessonCreatorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        LessonCreatorFragmentArgs args = LessonCreatorFragmentArgs.fromBundle(getArguments());
        courseId = args.getCourseId();

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
        binding.lessonBtnBack.setOnClickListener(v ->{
            LessonCreatorFragmentDirections.ActionLessonCreatorFragmentToLessonDashboardFragment action =
                    LessonCreatorFragmentDirections.actionLessonCreatorFragmentToLessonDashboardFragment(courseId);
            action.setCourseId(courseId);
            mainFacade.getCoursesNavController().navigate(action);
        });

        binding.lessonBtnSubmit.setOnClickListener(v -> {
            showLoadingScreen();

            String title = String.valueOf(binding.lessonEttTitle.getText());
            Log.d(TAG, title);
            String lessonContent = binding.lessonAreditor.getHtml();
            String lessonNumber = String.valueOf(binding.lessonEttLessonno.getText());
            String duration = String.valueOf(binding.lessonEttDuration.getText());

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Lesson Added Successfully!", Toast.LENGTH_SHORT);
                    mainFacade.getOpeningNavController().navigate(R.id.action_signupFragment_to_loginFragment);
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.createLesson(responseListener, courseId, title, lessonNumber, "null", lessonContent, duration);
        });

    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.lessonBtnSubmit.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.lessonBtnSubmit.setClickable(true);
    }

}