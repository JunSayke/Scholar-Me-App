package com.example.solutionsproject.classes.retrofit;

import androidx.annotation.NonNull;

import com.example.solutionsproject.model.gson.data.GsonData;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {

    private WebSocket webSocket;

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:6969/chat")
                .addHeader("Authorization", "scholarmeapp2024_api_key")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket is open");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                System.out.println("Received message: " + text);
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

    public void sendMessage(String userId, String message) {
        if (webSocket != null) {
            webSocket.send(GsonData.objectToJson(Map.of("userId", userId, "message", message)));
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection");
        }
    }
}