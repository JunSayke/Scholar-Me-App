package com.example.solutionsproject.model.gson.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class GsonData {
    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.toString(4); // Indent by 4 spaces for better formatting
        } catch (JSONException e) {
            return jsonString; // Return the original JSON string if formatting fails
        }
    }
}
