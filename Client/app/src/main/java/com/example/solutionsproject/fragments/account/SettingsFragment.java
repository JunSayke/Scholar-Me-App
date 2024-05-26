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

import com.example.solutionsproject.adapter.ApplicantListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentSettingsBinding;
import com.example.solutionsproject.model.gson.data.ApplicantsGson;
import com.example.solutionsproject.model.gson.data.GsonData;

import java.util.List;

public class SettingsFragment extends Fragment {

    private final String TAG = "Settings_Fragment";
    private MainFacade mainFacade;
    private FragmentSettingsBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
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
        final ScholarMeServer.ResponseListener<List<ApplicantsGson>> responseListener = new ScholarMeServer.ResponseListener<List<ApplicantsGson>>() {
            @Override
            public void onSuccess(List<ApplicantsGson> data) {
                binding.settingsListRequests.setAdapter(new ApplicantListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> acceptApplication(Integer.parseInt(itemId)),
                        itemId -> rejectApplication(Integer.parseInt(itemId))
                ));
                binding.settingsListRequests.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext()));
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.getApplicants(responseListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void acceptApplication(int itemId){
        final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
            @Override
            public void onSuccess(GsonData data) {
                mainFacade.makeToast("Application accepted", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.approveCreator(responseListener, String.valueOf(itemId));
    }

    public void rejectApplication(int itemId){
        final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
            @Override
            public void onSuccess(GsonData data) {
                mainFacade.makeToast("Application rejected", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };

        mainFacade.rejectCreator(responseListener, String.valueOf(itemId));
    }
}