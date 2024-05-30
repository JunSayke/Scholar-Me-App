package com.example.solutionsproject.classes.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.solutionsproject.adapter.DiscussionCommentListRecyclerViewAdapter;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.model.gson.data.CommentGson;
import com.example.solutionsproject.model.gson.data.response.SuccessGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatWebSocketService {
    private WebSocket webSocket;
    @Setter
    private DiscussionCommentListRecyclerViewAdapter adapter;

    private static String ipAddress;

    static {
//        try {
//            MainFacade mainFacade = MainFacade.getInstance();
//            ipAddress = mainFacade.getIpAddress() + ":" + mainFacade.getServerPort();
//        } catch (Exception e) {
//            ipAddress = "10.0.2.2:6969";
//            throw new RuntimeException(e);
//        }
        ipAddress = "10.0.2.2:6969";
    }

    Thread pingThread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            if (webSocket != null) {
                webSocket.send(ByteString.EMPTY);
            }

            try {
                Thread.sleep(25000); // Sleep for 25 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve the interrupt status
            }
        }
    });

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://" + ipAddress + "/chat")
                .addHeader("Authorization", "scholarmeapp2024_api_key")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket is open");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d("WebSocket TEST", "Received message: " + text);
                Type type = new TypeToken<SuccessGson<CommentGson>>(){}.getType();
                SuccessGson<CommentGson> response = new Gson().fromJson(text, type);

                if (response != null) {
                    CommentGson newComment = response.getData();

                    try {
                        MainFacade.getInstance().getMainActivity().runOnUiThread(() -> {
                            try {
                                adapter.addData(newComment);
                            } catch (Exception e) {
                                Log.e("TESTING1", "Error in addData: " + e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                System.out.println("WebSocket is closed");
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                System.out.println("WebSocket connection failed: " + t.getMessage());
            }
        });

        pingThread.start();
    }

    public void sendMessage(int userId, String message) {
        if (webSocket != null) {
            webSocket.send(String.valueOf(new JSONObject(Map.of(
                    "userId", userId,
                    "message", message))));
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
        }
        pingThread.interrupt();
    }

}