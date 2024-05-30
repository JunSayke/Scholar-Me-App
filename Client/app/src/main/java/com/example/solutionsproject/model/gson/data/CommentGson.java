package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class CommentGson extends GsonData {
    private int commentId;
    private UserGson user;
    private String comment;
    private String dateAdded;
    private String dateUpdated;
}