package com.example.solutionsproject.classes.general;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.solutionsproject.model.gson.data.UserGson;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private final SharedPreferences sharedpreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedpreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void startSession(UserGson userGson, Set<String> cookies) {
        editor.putString("usergson", new Gson().toJson(userGson));
        editor.putStringSet("cookies", cookies);
        editor.apply();
    }

    public void stopSession() {
        editor.clear();
        editor.apply();
    }

    public void setUserGson(UserGson userGson) {
        editor.putString("usergson", new Gson().toJson(userGson));
        editor.apply();
    }

    public UserGson getUserGson() {
        return new Gson().fromJson(sharedpreferences.getString("usergson", null), UserGson.class);
    }

    public void setCookies(Set<String> cookies) {
        editor.putStringSet("cookies", cookies);
        editor.apply();
    }

    public Set<String> getCookies() {
        return sharedpreferences.getStringSet("cookies", new HashSet<>());
    }
}
