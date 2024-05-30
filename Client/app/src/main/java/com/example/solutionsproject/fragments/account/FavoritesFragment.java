package com.example.solutionsproject.fragments.account;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.adapter.CourseListRecyclerViewAdapter;
import com.example.solutionsproject.adapter.FaveCourseListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentFavoritesBinding;
import com.example.solutionsproject.fragments.homepage.CoursesFragmentDirections;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.GsonData;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private final String TAG = "Favorite_Fragment";
    private MainFacade mainFacade;
    private FragmentFavoritesBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
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
                if(!data.isEmpty()) binding.faveTxtNoCourses.setVisibility(View.GONE);
                binding.faveListChoices.setAdapter(new FaveCourseListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> {
                            unmarkCourse(Integer.parseInt(itemId));
                        }, mainFacade
                ));
                binding.faveListChoices.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getUserCourseFavorites(responseListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void unmarkCourse(int courseId){
        final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
            @Override
            public void onSuccess(GsonData data) {
                mainFacade.makeToast("Removed from favorites", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };
        mainFacade.unmarkFavoriteCourse(responseListener, courseId);
    }
}