package com.example.solutionsproject.fragments.opening;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solutionsproject.activities.TermsAndConditionsActivity;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.imagetools.FileUtils;
import com.example.solutionsproject.model.gson.data.GsonData;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.imagetools.ImagePicker;
import com.example.solutionsproject.databinding.FragmentSignupBinding;

import java.io.File;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;

public class SignupFragment extends Fragment implements ImagePicker.OnImageSelectedListener {
    private final String TAG = "Signup_Fragment";
    private MainFacade mainFacade;
    private FragmentSignupBinding binding;
    private ImagePicker imagePicker;
    private Uri imageData;
    private ImageLoader imageLoader;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
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

        binding.signupBtnProfile.setOnClickListener(v -> {
            imagePicker.selectImage(this);
        });

        binding.signupBtnSubmit.setOnClickListener(v -> {
            showLoadingScreen();
            File profileImageFile = FileUtils.uriToFile(requireContext(), imageData);
            String email = String.valueOf(binding.signupEttEmail.getText());
            String firstname = String.valueOf(binding.signupEttFirstname.getText());
            String lastname = String.valueOf(binding.signupEttLastname.getText());
            String username = String.valueOf(binding.signupEttUsername.getText());
            String password = String.valueOf(binding.signupEttPassword.getText());
            String repassword = String.valueOf(binding.signupEttReenterPassword.getText());
            String phoneno = String.valueOf(binding.signupEttNumber.getText());

            if(!binding.signupCbTerms.isChecked()){
                mainFacade.makeToast("Read the terms and conditions!", Toast.LENGTH_SHORT);
                hideLoadingScreen();
                return;
            }

            if(!repassword.equals(password)){
                mainFacade.makeToast("Password mismatch", Toast.LENGTH_SHORT);
                binding.signupEttPassword.setText("");
                binding.signupEttReenterPassword.setText("");
                hideLoadingScreen();
                return;
            }

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Registered Successfully!", Toast.LENGTH_SHORT);
                    mainFacade.getOpeningNavController().navigate(R.id.action_signupFragment_to_loginFragment);
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };

            mainFacade.register(responseListener, profileImageFile, email, firstname, lastname, username, password, phoneno);
        });

        binding.signupBtnTerms.setOnClickListener(v -> {
            startActivity(new Intent(mainFacade.getMainActivity(), TermsAndConditionsActivity.class));
        });

        binding.signupBtnSwitch.setOnClickListener(v -> {
            mainFacade.getOpeningNavController().navigate(R.id.action_signupFragment_to_loginFragment);
        });

    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.signupBtnSubmit.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.signupBtnSubmit.setClickable(true);
    }

    // -- IMAGE PICKER METHODS --
    private void initImagePicker() {
        imagePicker = new ImagePicker(mainFacade.getMainActivity().getActivityResultRegistry());
        getLifecycle().addObserver(imagePicker);
    }

    @Override
    public void onImageSelected(Uri uri) {
        if(uri != null) {
            imageLoader = Coil.imageLoader(mainFacade.getMainActivity().getApplicationContext());
            ImageRequest request = new ImageRequest.Builder(mainFacade.getMainActivity().getApplicationContext())
                    .data(uri)
                    .error(R.drawable.vector_wrong_mark)
                    .target(binding.signupBtnProfile)
                    .transformations(new CircleCropTransformation())
                    .build();
            imageLoader.enqueue(request);
            imageData = uri;
        } else {
            mainFacade.makeToast("Image selection canceled", Toast.LENGTH_SHORT);
        }
    }
}
