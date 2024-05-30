package com.example.solutionsproject.fragments.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.adapter.LessonListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentLessonDashboardBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.LessonGson;
import com.google.gson.Gson;

import java.util.List;

public class LessonDashboardFragment extends Fragment {

    private final String TAG = "LessonDashboard_Fragment";
    private MainFacade mainFacade;
    private FragmentLessonDashboardBinding binding;
    private int courseId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLessonDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        LessonDashboardFragmentArgs args = LessonDashboardFragmentArgs.fromBundle(getArguments());
        courseId = args.getCourseId();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<LessonGson>> responseListener = new ScholarMeServer.ResponseListener<List<LessonGson>>() {
            @Override
            public void onSuccess(List<LessonGson> data) {
                binding.ldashListChoices.setAdapter(new LessonListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        (courseId, courseLessonId) -> {
                            LessonDashboardFragmentDirections.ActionLessonDashboardFragmentToLessonEditorFragment action =
                                    LessonDashboardFragmentDirections.actionLessonDashboardFragmentToLessonEditorFragment(courseId, courseLessonId);
                            action.setCourseId(courseId);
                            action.setCourseLessonId(courseLessonId);
                            mainFacade.getCoursesNavController().navigate(action);
                        }
                ));
                binding.ldashListChoices.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
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
        binding.ldashBtnBack.setOnClickListener(v -> {
            mainFacade.getCoursesNavController().navigate(R.id.action_lessonDashboardFragment_to_courseDashboardFragment);
        });

        binding.ldashBtnEditCourse.setOnClickListener(v -> {
            mainFacade.popupUpdateCourse(v, courseId);
        });

        binding.ldashBtnDelete.setOnClickListener(v -> {
            mainFacade.popupDeleteCourseWarning(v, courseId);
        });

        binding.ldashBtnCreateCourse.setOnClickListener(v -> {
            LessonDashboardFragmentDirections.ActionLessonDashboardFragmentToLessonCreatorFragment action =
                    LessonDashboardFragmentDirections.actionLessonDashboardFragmentToLessonCreatorFragment(courseId);
            action.setCourseId(courseId);
            mainFacade.getCoursesNavController().navigate(action);
        });
    }
}