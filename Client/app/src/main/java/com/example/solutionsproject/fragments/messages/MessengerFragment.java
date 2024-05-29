package com.example.solutionsproject.fragments.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.solutionsproject.adapter.DiscussionCommentListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.retrofit.ChatWebSocketService;
import com.example.solutionsproject.databinding.FragmentMessengerBinding;
import com.example.solutionsproject.model.gson.data.CommentGson;
import com.example.solutionsproject.model.gson.data.GsonData;

import java.util.List;

public class MessengerFragment extends Fragment {

    private final String TAG = "Messenger_Fragment";
    private MainFacade mainFacade;
    private FragmentMessengerBinding binding;

    private final ChatWebSocketService chatWebSocketService = new ChatWebSocketService();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessengerBinding.inflate(inflater, container, false);
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

        mainFacade.getDiscussionComments(new ScholarMeServer.ResponseListener<>() {
            @Override
            public void onSuccess(List<CommentGson> data) {
                mainFacade.makeToast("Comments loaded", Toast.LENGTH_SHORT);

                DiscussionCommentListRecyclerViewAdapter adapter = new DiscussionCommentListRecyclerViewAdapter(
                        mainFacade.getMainActivity().getApplicationContext(),
                        data
                );

                binding.messengerListChats.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext());
                layoutManager.setStackFromEnd(true); // This will make the layout start from the end of the list

                binding.messengerListChats.setLayoutManager(layoutManager);
                // Set the adapter in the ChatWebSocketService
                chatWebSocketService.setAdapter(adapter);
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast("Failed to load comments", Toast.LENGTH_SHORT);
            }
        });

        chatWebSocketService.connect();
        binding.cdashBtnSend.setOnClickListener(v -> {
            chatWebSocketService.sendMessage(Integer.parseInt(mainFacade.getSessionManager().getUserGson().getId()), binding.cdashEttMessage.getText().toString());

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        chatWebSocketService.close();
    }
}