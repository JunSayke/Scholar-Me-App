package com.example.solutionsproject.fragments.courses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentCourseDetailsBinding;

public class CourseDetailsFragment extends Fragment {
    private final String TAG = "CourseDetails_Fragment";
    private MainFacade mainFacade;
    private FragmentCourseDetailsBinding binding;
    private int courseId;
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}