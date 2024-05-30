package com.example.solutionsproject.fragments.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.NotificationListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.databinding.FragmentNotificationsBinding;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.NotificationGson;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private final String TAG = "Notifications_Fragment";
    private MainFacade mainFacade;
    private FragmentNotificationsBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
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

        final ScholarMeServer.ResponseListener<List<NotificationGson>> responseListener = new ScholarMeServer.ResponseListener<List<NotificationGson>>() {
            @Override
            public void onSuccess(List<NotificationGson> data) {
                if(!data.isEmpty()) binding.notificationsTxtEmpty.setVisibility(View.GONE);
                binding.notificationsListMessage.setAdapter(new NotificationListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data,
                        itemId -> {
                            deleteNotification(itemId);
                        }
                ));
                binding.notificationsListMessage.setLayoutManager(new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onFailure(String message) {

            }
        };
        mainFacade.getUserNotifications(responseListener);

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.messageTxtMessage.setOnClickListener(v -> {
           mainFacade.getMessagesNavController().navigate(R.id.action_notificationsFragment_to_messagesFragment);
        });

    }

    private void deleteNotification(int notificaitonId){
        final ScholarMeServer.ResponseListener<GsonData> responseListener = new ScholarMeServer.ResponseListener<GsonData>() {
            @Override
            public void onSuccess(GsonData data) {
                mainFacade.makeToast("Successfully deleted course", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast(message, Toast.LENGTH_SHORT);
            }
        };
        mainFacade.deleteNotification(responseListener, notificaitonId);

    }
}