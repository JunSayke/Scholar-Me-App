package com.example.solutionsproject.fragments.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solutionsproject.R;
import com.example.solutionsproject.activities.MainActivity;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.databinding.FragmentAccountBinding;
import com.example.solutionsproject.model.gson.data.UserGson;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import coil.transform.CircleCropTransformation;

public class AccountFragment extends Fragment {
    private final String TAG = "Account_Fragment";
    private MainFacade mainFacade;
    private FragmentAccountBinding binding;
    ImageLoader imageLoader;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            mainFacade = MainFacade.getInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        UserGson userGson = mainFacade.getSessionManager().getUserGson();
        binding.accountTxtRole.setText(userGson.getRole());
        binding.accountTxtName.setText(userGson.getUserName());

        imageLoader = Coil.imageLoader(mainFacade.getMainActivity().getApplicationContext());
        String imageUrl = "http://" + mainFacade.getIpAddress() + ":" + mainFacade.getServerPort() + userGson.getProfilePicUrl();
        ImageRequest request = new ImageRequest.Builder(mainFacade.getMainActivity().getApplicationContext())
                .data(imageUrl)
                .error(R.drawable.vector_wrong_mark)
                .target(binding.accountIvProfile)
                .transformations(new CircleCropTransformation())
                .build();
        imageLoader.enqueue(request);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.accountBtnFavorite.setOnClickListener(v -> {
            mainFacade.getAccountNavController().navigate(R.id.action_accountFragment_to_favoritesFragment);
        });

        binding.accountBtnEditAccount.setOnClickListener(v -> {
            mainFacade.getAccountNavController().navigate(R.id.action_accountFragment_to_editAccountFragment);
        });

        binding.accountBtnSettings.setOnClickListener(v -> {
            mainFacade.getAccountNavController().navigate(R.id.action_accountFragment_to_settingsFragment);
        });

        binding.accountBtnHelp.setOnClickListener(v -> {
            mainFacade.getAccountNavController().navigate(R.id.action_accountFragment_to_helpFragment);
        });

        binding.accountBtnTimer.setOnClickListener(v -> {
            mainFacade.popupPomodoro(binding.getRoot());
        });

        binding.accountBtnLogout.setOnClickListener(v -> {
            mainFacade.stopLoginSession();
            startActivity(new Intent(mainFacade.getMainActivity().getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



	