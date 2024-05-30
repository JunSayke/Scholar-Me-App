package com.example.solutionsproject.classes.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.MessageGson;
import com.example.solutionsproject.model.gson.data.response.ResponseGson;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatWebSocketService {

    private WebSocket webSocket;

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:6969/chat")
                .addHeader("Authorization", MainFacade.API_KEY)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket is open");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d("WebSocket TEST", "Received message: " + text);
                ResponseGson<?> message = GsonData.jsonToObject(text, ResponseGson.class);

                if (message != null) {
                    Log.d("WebSocket TEST", message.toString());
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
    }
}