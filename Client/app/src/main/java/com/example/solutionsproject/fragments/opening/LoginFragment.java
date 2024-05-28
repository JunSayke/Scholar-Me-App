package com.example.solutionsproject.fragments.opening;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentLoginBinding;
import com.example.solutionsproject.model.gson.data.UserGson;


public class LoginFragment extends Fragment {
    private final String TAG = "Login_Fragment";
    private MainFacade mainFacade;
    private FragmentLoginBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
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

        initActions();
    }

    private void initActions(){
        binding.loginBtnDebug.setOnClickListener(v ->
                mainFacade.getOpeningNavController().navigate(R.id.action_loginFragment_to_homepageNavController)
        );

        binding.loginBtnSubmit.setOnClickListener(v -> {
            showLoadingScreen();
            String user = String.valueOf(binding.loginEttUser.getText());
            String password = String.valueOf(binding.loginEttPassword.getText());
            ScholarMeServer.ResponseListener<UserGson> responseListener = new ScholarMeServer.ResponseListener<UserGson>() {
                @Override
                public void onSuccess(UserGson data) {
                    hideLoadingScreen();
                    UserGson user = data;
                    Log.d(TAG, String.valueOf(data));
                    mainFacade.startLoginSession(user);
                    mainFacade.makeToast("Login Successful!", Toast.LENGTH_SHORT);
                    mainFacade.getOpeningNavController().navigate(R.id.action_loginFragment_to_homepageNavController);
                }
                @Override
                public void onFailure(String message) {
                    hideLoadingScreen();
                    mainFacade.makeToast(message, Toast.LENGTH_SHORT);
                }
            };
            mainFacade.login(responseListener, user, password);
        });

        binding.loginBtnSwitch.setOnClickListener(v -> {
            mainFacade.getOpeningNavController().navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // -- LOADING METHODS --
    private void showLoadingScreen(){
        mainFacade.showLoadingScreen();
        binding.loginBtnSubmit.setClickable(false);
    }

    private void hideLoadingScreen(){
        mainFacade.hideLoadingScreen();
        binding.loginBtnSubmit.setClickable(true);
    }
}
