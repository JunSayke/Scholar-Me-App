package com.example.solutionsproject.fragments.courses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.general.TimerService;
import com.example.solutionsproject.databinding.FragmentCourseDetailsBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;

import java.util.List;
import java.util.Timer;

public class CourseDetailsFragment extends Fragment {
    private final String TAG = "CourseDetails_Fragment";
    private MainFacade mainFacade;
    private FragmentCourseDetailsBinding binding;
    private int courseId;
    private TimerService timerService;
    private boolean isRunning;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseDetailsBinding.inflate(inflater, container, false);
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
        CourseDetailsFragmentArgs args = CourseDetailsFragmentArgs.fromBundle(getArguments());
        courseId = args.getCourseId();
        Log.d(TAG, "ID:" + courseId);

        final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                for(CourseGson course: data){
                    if(courseId == Integer.parseInt(course.getId())){
                        fillPlaceholder(course);
                    }
                }
            }

            @Override
            public void onFailure(String message) {

            }
        };
        mainFacade.getCourses(responseListener);

        initActions();
    }

    private void fillPlaceholder(CourseGson course) {
        binding.cdTxtDetailTitle.setText(course.getTitle());
        binding.cdTxtTitle.setText((course.getTitle()));
        binding.cdTxtDescriptionContent.setText(course.getDescription());
        binding.cdTxtTimeFrame.setText(course.getDateAdded());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){


        binding.cdBtnStart.setOnClickListener(v -> {
            if(isRunning){
                stopService(v);
                binding.cdBtnStart.setText("Start Timer");
                isRunning = false;
            }else{
                startService(v);
                binding.cdBtnStart.setText("Stop Timer");
                isRunning = true;
            }
        });
    }

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