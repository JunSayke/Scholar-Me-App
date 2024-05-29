package com.example.solutionsproject.model.gson.data.response;

import com.example.solutionsproject.model.gson.data.GsonData;

import lombok.Getter;

@Getter
public abstract class ResponseGson<T> extends GsonData {
    private boolean status;
    private String message;
    private int code;

    public boolean getStatus() {
        return status;
    }
}