package com.example.solutionsproject.fragments.courses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentCourseDashboardBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;

import java.util.List;

public class CourseDashboardFragment extends Fragment {

    private final String TAG = "CourseDashboard_Fragment";
    private MainFacade mainFacade;
    private FragmentCourseDashboardBinding binding;
    private int courseCount;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseDashboardBinding.inflate(inflater, container, false);
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

        final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                binding.cdashListChoices.setAdapter(new CourseListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> func(itemId)//TODO: ADD ACTION
                ));
                binding.cdashListChoices.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getCreatorCourses(responseListener);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.cdashBtnBack.setOnClickListener(v ->{
            mainFacade.getCoursesNavController().navigate(R.id.action_courseDashboardFragment_to_coursesFragment);
        });

        binding.cdashBtnCreateCourse.setOnClickListener(v ->{
            mainFacade.getCoursesNavController().navigate(R.id.action_courseDashboardFragment_to_courseCreatorFragment);
        });

        binding.cdashBtnOpenAre.setOnClickListener(v ->{
            mainFacade.getCoursesNavController().navigate(R.id.action_courseDashboardFragment_to_contentCreatorFragment);
        });
    }

    private void func(String Id){

    }
}