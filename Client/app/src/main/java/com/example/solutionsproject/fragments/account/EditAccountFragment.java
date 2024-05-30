package com.example.solutionsproject.fragments.account;

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
import com.example.solutionsproject.databinding.FragmentEditAccountBinding;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.UserGson;

import java.io.File;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;

public class EditAccountFragment extends Fragment implements ImagePicker.OnImageSelectedListener  {
    private final String TAG = "EditAccount_Fragment";
    private MainFacade mainFacade;
    private FragmentEditAccountBinding binding;
    private ImagePicker imagePicker;
    private Uri imageData;
    private ImageLoader imageLoader;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);
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

        binding.editBtnProfile.setOnClickListener(v -> {
            imagePicker.selectImage(this);
        });

        binding.editBtnEdit.setOnClickListener(v -> {
            showLoadingScreen();

            File profileImageFile = FileUtils.uriToFile(requireContext(), imageData);
            String email = String.valueOf(binding.editEttEmail.getText());
            String firstname = String.valueOf(binding.editEttFirstname.getText());
            String lastname = String.valueOf(binding.editEttLastname.getText());
            String password = String.valueOf(binding.editEttPassword.getText());
            String repassword = String.valueOf(binding.editEttReenterPassword.getText());
            String phoneno = String.valueOf(binding.editEttNumber.getText());

            if(!repassword.equals(password)){
                mainFacade.makeToast("Password mismatch", Toast.LENGTH_SHORT);
                binding.editEttPassword.setText("");
                binding.editEttReenterPassword.setText("");
                return;
            }

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Updated Successfully!", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };

            mainFacade.updateProfile(responseListener, profileImageFile, email.isEmpty() ? null : email, firstname.isEmpty() ? null : firstname, lastname.isEmpty() ? null : lastname, password.isEmpty() ? null : password, phoneno.isEmpty() ? null : phoneno);
        });


        binding.editBtnSubmit.setOnClickListener(v -> {
            showLoadingScreen();

            final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    hideLoadingScreen();
                    mainFacade.makeToast("Request Submitted", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.applyCreator(responseListener);
        });
    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.editBtnEdit.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.editBtnEdit.setClickable(true);
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
                    .target(binding.editBtnProfile)
                    .transformations(new CircleCropTransformation())
                    .build();
            imageLoader.enqueue(request);
            imageData = uri;
        } else {
            mainFacade.makeToast("Image selection canceled", Toast.LENGTH_SHORT);
        }
    }
}