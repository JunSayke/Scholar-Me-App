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
import com.example.solutionsproject.databinding.FragmentCourseDetailsBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.LessonGson;

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

        final ScholarMeServer.ResponseListener<List<CourseGson>> getCourseResponseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                for(CourseGson course: data){
                    //Log.d(TAG, data.toString());
                    if(courseId == Integer.parseInt(course.getId())){
                        fillPlaceholder(course);
                    }
                }
            }

            @Override
            public void onFailure(String message) {

            }
        };
        mainFacade.getCourses(getCourseResponseListener);

        final ScholarMeServer.ResponseListener<List<LessonGson>> getLessonsResponseListener = new ScholarMeServer.ResponseListener<List<LessonGson>>() {
            @Override
            public void onSuccess(List<LessonGson> data) {
                if(!data.isEmpty()) binding.cdTxtNoCourses.setVisibility(View.GONE);
                binding.cdListCourses.setAdapter(new LessonListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        (courseId, courseLessonId) -> {
                            CourseDetailsFragmentDirections.ActionCourseDetailsFragmentToLessonDetailsFragment action =
                                    CourseDetailsFragmentDirections.actionCourseDetailsFragmentToLessonDetailsFragment(courseId, courseLessonId);
                            action.setCourseId(courseId);
                            action.setCourseLessonId(courseLessonId);

                            mainFacade.getCoursesNavController().navigate(action);
                        }

                ));
                binding.cdListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getCourseLesson(getLessonsResponseListener, courseId);

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
        binding.cdBtnBack.setOnClickListener(v -> {
            mainFacade.getCoursesNavController().navigate(R.id.action_courseDetailsFragment_to_coursesFragment);
        });

        binding.cdBtnStart.setOnClickListener(v -> {
            mainFacade.popupPomodoro(v);
        });

        binding.cdBtnUnenroll.setOnClickListener(v -> {
            mainFacade.popupUnenrollWarning(v, courseId);
        });
    }
}