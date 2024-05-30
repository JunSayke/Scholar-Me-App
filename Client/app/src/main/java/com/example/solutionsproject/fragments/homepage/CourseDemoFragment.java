package com.example.solutionsproject.fragments.homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.LessonDemoListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentCourseDemoBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.LessonGson;
import com.example.solutionsproject.model.gson.data.UserGson;

import java.util.List;

public class CourseDemoFragment extends Fragment {
    private final String TAG = "CourseDemo_Fragment";
    private MainFacade mainFacade;
    private FragmentCourseDemoBinding binding;
    private int courseId;
    private UserGson userGson;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseDemoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        CourseDemoFragmentArgs args = CourseDemoFragmentArgs.fromBundle(getArguments());
        courseId = args.getCourseId();

        userGson = mainFacade.getSessionManager().getUserGson();
        //Log.d(TAG, userGson.toString());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                if(!data.isEmpty()) binding.cdeTxtNoCourses.setVisibility(View.GONE);
                binding.cdeListCourses.setAdapter(new LessonDemoListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data
                ));
                binding.cdeListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
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
        binding.cdeTxtDetailTitle.setText(course.getTitle());
        binding.cdeTxtTitle.setText((course.getTitle()));
        binding.cdeTxtDescriptionContent.setText(course.getDescription());
        binding.cdeTxtTimeFrame.setText(course.getDateAdded());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.cdeBtnBack.setOnClickListener(v -> {
            mainFacade.getHomepageNavController().navigate(R.id.action_courseDemoFragment_to_menuSearch);
        });

        binding.cdeBtnFavorite.setOnClickListener(v -> {
            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    mainFacade.makeToast("Added to your favorites!", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.markFavoriteCourse(responseListener, courseId);
        });

        binding.cdeBtnStart.setOnClickListener(v -> {
            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    mainFacade.makeToast("Successfully enrolled!", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.enrollCourse(responseListener, courseId);
        });
    }
}