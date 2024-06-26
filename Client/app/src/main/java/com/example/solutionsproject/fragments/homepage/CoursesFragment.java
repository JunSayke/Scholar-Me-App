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

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;

public class CoursesFragment extends Fragment {
    private final String TAG = "Courses_Fragment";
    private MainFacade mainFacade;
    private FragmentCoursesBinding binding;
    ImageLoader imageLoader;
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

        imageLoader = Coil.imageLoader(mainFacade.getMainActivity().getApplicationContext());
        String imageUrl = "http://" + mainFacade.getIpAddress() + ":" + mainFacade.getServerPort() + userGson.getProfilePicUrl();
        ImageRequest request = new ImageRequest.Builder(mainFacade.getMainActivity().getApplicationContext())
                .data(imageUrl)
                .error(R.drawable.vector_wrong_mark)
                .target(binding.coursesIvProfile)
                .transformations(new CircleCropTransformation())
                .build();
        imageLoader.enqueue(request);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener = new ScholarMeServer.ResponseListener<List<CourseGson>>() {
            @Override
            public void onSuccess(List<CourseGson> data) {
                if(!data.isEmpty()) binding.courseTxtNoCourses.setVisibility(View.GONE);
                binding.courseListCourses.setAdapter(new CourseListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> {
                            CoursesFragmentDirections.ActionCoursesFragmentToCourseDetailsFragment action =
                                    CoursesFragmentDirections.actionCoursesFragmentToCourseDetailsFragment(Integer.parseInt(itemId));
                            action.setCourseId(Integer.parseInt(itemId));
                            mainFacade.getCoursesNavController().navigate(action);
                        }, mainFacade
                ));
                binding.courseListCourses.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getUserCourses(responseListener);

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


	
	