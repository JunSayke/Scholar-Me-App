package com.example.solutionsproject.model.gson.data.response;

import lombok.Getter;

@Getter
public class SuccessGson<T> extends ResponseGson {
    private T data;

}
