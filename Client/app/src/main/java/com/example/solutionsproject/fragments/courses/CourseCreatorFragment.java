package com.example.solutionsproject.fragments.courses;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.imagetools.FileUtils;
import com.example.solutionsproject.classes.imagetools.ImagePicker;
import com.example.solutionsproject.databinding.FragmentCourseCreatorBinding;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CourseCreatorFragment extends Fragment implements ImagePicker.OnImageSelectedListener {

    private final String TAG = "CourseCreator_Fragment";
    private MainFacade mainFacade;
    private FragmentCourseCreatorBinding binding;
    private ImagePicker imagePicker;
    private Uri imageData;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseCreatorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        initImagePicker();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.cdashBtnBack.setOnClickListener(v ->{
            mainFacade.getCoursesNavController().navigate(R.id.action_courseCreatorFragment_to_courseDashboardFragment);
        });

        binding.cdashBtnThumbnail.setOnClickListener(v -> {
            imagePicker.selectImage(this);
        });

        binding.cdashBtnCourse.setOnClickListener(v ->{
            showLoadingScreen();

            File thumbnail = FileUtils.uriToFile(requireContext(), imageData);
            String title = String.valueOf(binding.cdashEttTitle.getText());
            String description = String.valueOf(binding.cdashEttDescription.getText());

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Course created!", Toast.LENGTH_SHORT);
                    mainFacade.getCoursesNavController().navigate(R.id.action_courseCreatorFragment_to_courseDashboardFragment);
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };

            mainFacade.createCourse(responseListener, thumbnail, title, description);
        });
    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.cdashBtnCourse.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.cdashBtnCourse.setClickable(true);
    }

    // -- IMAGE PICKER METHODS --
    private void initImagePicker() {
        imagePicker = new ImagePicker(mainFacade.getMainActivity().getActivityResultRegistry());
        getLifecycle().addObserver(imagePicker);
    }

    @Override
    public void onImageSelected(Uri uri) {
        if(uri != null) {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.__aa_container_white_box)
                    .error(R.drawable.__aa_container_white_box)
                    .into(binding.cdashBtnThumbnail);
            imageData = uri;
        } else {
            mainFacade.makeToast("Image selection canceled", Toast.LENGTH_SHORT);
        }
    }

}