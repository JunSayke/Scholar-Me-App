package com.example.solutionsproject.fragments.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentLessonEditorBinding;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.LessonGson;

import java.util.List;

public class LessonEditorFragment extends Fragment {

    private final String TAG = "LessonEditor_Fragment";
    private MainFacade mainFacade;
    private FragmentLessonEditorBinding binding;
    private int courseId;
    private int courseLessonId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLessonEditorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        LessonEditorFragmentArgs args = LessonEditorFragmentArgs.fromBundle(getArguments());
        courseId = args.getCourseId();
        courseLessonId = args.getCourseLessonId();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<LessonGson>> responseListener = new ScholarMeServer.ResponseListener<List<LessonGson>>() {
            @Override
            public void onSuccess(List<LessonGson> data) {
                for(LessonGson lesson : data){
                    if(lesson.getCourseLessonId() == courseLessonId){
                        fillPlaceholder(lesson);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(String message) {

            }
        };
        mainFacade.getCourseLesson(responseListener, courseId);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.editLessonBtnBack.setOnClickListener(v ->{
            lessonEditorToLessonDashboard();
        });

        binding.editLessonBtnDelete.setOnClickListener(v -> {
            mainFacade.popupDeleteLessonWarning(v, courseLessonId);
        });

        binding.editLessonBtnUpdate.setOnClickListener(v -> {
            showLoadingScreen();

            String title = String.valueOf(binding.editLessonEttTitle.getText());
            String lessonContent = binding.editLessonAreditor.getHtml();
            String lessonNumber = String.valueOf(binding.editLessonEttLessonno.getText());
            String duration = String.valueOf(binding.editLessonEttDuration.getText());

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Lesson Updated Successfully!", Toast.LENGTH_SHORT);
                    lessonEditorToLessonDashboard();
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.updateLesson(responseListener, courseId, title, lessonNumber, "null", lessonContent, duration);
        });

    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.editLessonBtnUpdate.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.editLessonBtnUpdate.setClickable(true);
    }

    private void lessonEditorToLessonDashboard(){
        LessonEditorFragmentDirections.ActionLessonEditorFragmentToLessonDashboardFragment action =
                LessonEditorFragmentDirections.actionLessonEditorFragmentToLessonDashboardFragment(courseId);
        action.setCourseId(courseId);
        mainFacade.getCoursesNavController().navigate(action);
    }

    // -- FILL PLACEHOLDER --
    private void fillPlaceholder(LessonGson lesson){
        binding.editLessonEttTitle.setText(lesson.getTitle());
        binding.editLessonEttLessonno.setText(lesson.getLessonNumber());
        binding.editLessonEttDuration.setText(lesson.getDuration());
        binding.editLessonAreditor.fromHtml(lesson.getContent());
    }

}