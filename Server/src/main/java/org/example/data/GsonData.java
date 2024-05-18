package org.example.data;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONException;

public abstract class GsonData {
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

    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }
}
