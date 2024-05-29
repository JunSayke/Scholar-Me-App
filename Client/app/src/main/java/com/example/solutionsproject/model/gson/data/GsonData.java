package com.example.solutionsproject.model.gson.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public abstract class GsonData {
    private static final Gson gson = new GsonBuilder()
            .create();

    @NonNull
    @Override
    public String toString() {
        String jsonString = gson.toJson(this);

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.toString(4); // Indent by 4 spaces for better formatting
        } catch (JSONException e) {
            return jsonString; // Return the original JSON string if formatting fails
        }
    }

    public static String objectToJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
