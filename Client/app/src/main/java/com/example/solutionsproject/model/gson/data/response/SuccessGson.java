package com.example.solutionsproject.model.gson.data.response;

public class SuccessGson<T> extends ResponseGson {
    private T data;

    public T getData() {
        return data;
    }
}
