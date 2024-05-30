package com.example.solutionsproject.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.solutionsproject.R;
import com.example.solutionsproject.adapter.DiscussionCommentListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.general.ScholarMeServer;
import com.example.solutionsproject.classes.retrofit.ChatWebSocketService;
import com.example.solutionsproject.databinding.FragmentMessagesBinding;
import com.example.solutionsproject.model.gson.data.CommentGson;

import java.util.List;
import java.util.Objects;

public class MessagesFragment extends Fragment {
    private final String TAG = "Messages_Fragment";
    private MainFacade mainFacade;
    private FragmentMessagesBinding binding;

    private final ChatWebSocketService chatWebSocketService = new ChatWebSocketService();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
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

                binding.messageChatList.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mainFacade.getMainActivity().getApplicationContext());
                layoutManager.setStackFromEnd(true); // This will make the layout start from the end of the list

                binding.messageChatList.setLayoutManager(layoutManager);
                // Set the adapter in the ChatWebSocketService
                chatWebSocketService.setAdapter(adapter);

                chatWebSocketService.connect();
                binding.messageBtnSend.setOnClickListener(v -> {
                    chatWebSocketService.sendMessage(Integer.parseInt(mainFacade.getSessionManager().getUserGson().getUserId()), binding.messageEttChat.getText().toString());
                    Objects.requireNonNull(binding.messageChatList.getLayoutManager()).scrollToPosition(adapter.getItemCount() - 1);
                });
            }

            @Override
            public void onFailure(String message) {
                mainFacade.makeToast("Failed to load comments: " + message, Toast.LENGTH_SHORT);
            }
        });

        initActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initActions(){
        binding.messageTxtNotification.setOnClickListener(v -> {
            mainFacade.getMessagesNavController().navigate(R.id.action_messagesFragment_to_notificationsFragment);
        });
    }
}


	
	