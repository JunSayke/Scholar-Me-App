package com.example.solutionsproject.fragments.courses;

import android.content.Intent;
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
import com.example.solutionsproject.adapter.LessonListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.general.TimerService;
import com.example.solutionsproject.databinding.FragmentLessonDetailsBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.LessonGson;

import java.util.List;

public class LessonDetailsFragment extends Fragment {

    private final String TAG = "LessonDetails_Fragment";
    private MainFacade mainFacade;
    private FragmentLessonDetailsBinding binding;
    private int courseId;
    private int lessonId;
    private boolean isRunning;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLessonDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        LessonDetailsFragmentArgs args = LessonDetailsFragmentArgs.fromBundle(getArguments());
        lessonId = args.getCourseLessonId();
        courseId = args.getCourseId();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<LessonGson>> responseListener = new ScholarMeServer.ResponseListener<List<LessonGson>>() {
            @Override
            public void onSuccess(List<LessonGson> data) {
                for(LessonGson lesson: data){
                    if(lessonId == Integer.parseInt(lesson.getCourseLessonId())){
                        fillPlaceholder(lesson);
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
        binding.lessonDetailsBtnBack.setOnClickListener(v -> {
            LessonDetailsFragmentDirections.ActionLessonDetailsFragmentToCourseDetailsFragment action = LessonDetailsFragmentDirections.actionLessonDetailsFragmentToCourseDetailsFragment(courseId);
            action.setCourseId(courseId);
            mainFacade.getCoursesNavController().navigate(action);
        });

        binding.lessonDetailsIvTimer.setOnClickListener(v -> {
            if(isRunning){
                stopService(v);
//                binding.cdBtnStart.setText("Start Timer");
                isRunning = false;
            }else{
                startService(v);
//                binding.cdBtnStart.setText("Stop Timer");
                isRunning = true;
            }
        });

    }

    private void fillPlaceholder(LessonGson lesson){
        binding.lessonDetailsTvTitle.setText(lesson.getTitle());
        binding.lessonDetailsAreTextView.fromHtml(lesson.getContent());
    }

    // -- TIMER FUNCS
    public void startService(View v){
        System.out.println("Starting");
        Intent serviceIntent = new Intent(mainFacade.getMainActivity().getApplicationContext(), TimerService.class);
        mainFacade.getMainActivity().startService(serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(mainFacade.getMainActivity().getApplicationContext(), TimerService.class);
        mainFacade.getMainActivity().stopService(serviceIntent);
    }

}