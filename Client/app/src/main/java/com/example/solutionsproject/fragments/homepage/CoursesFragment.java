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
import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentCoursesBinding;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.UserGson;

import java.util.List;

public class CoursesFragment extends Fragment {
    private final String TAG = "Courses_Fragment";
    private MainFacade mainFacade;
    private FragmentCoursesBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        UserGson userGson = mainFacade.getSessionManager().getUserGson();
        if(userGson.getRole().equals("creator")){
            binding.coursesBtnCreateCourse.setVisibility(View.VISIBLE);
        }else{
            binding.coursesBtnCreateCourse.setVisibility(View.GONE);
        }


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                binding.courseListCourses.setAdapter(new CourseListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> {
                            CoursesFragmentDirections.ActionCoursesFragmentToCourseDetailsFragment action =
                                    CoursesFragmentDirections.actionCoursesFragmentToCourseDetailsFragment(Integer.parseInt(itemId));
                            action.setCourseId(Integer.parseInt(itemId));
                            //Log.d(TAG, itemId);
                            mainFacade.getCoursesNavController().navigate(action);
                        }
                ));
                binding.courseListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getCourses(responseListener);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.coursesBtnCreateCourse.setOnClickListener(v ->{
            mainFacade.getCoursesNavController().navigate(R.id.action_coursesFragment_to_courseDashboardFragment);
        });
    }
}


	
	