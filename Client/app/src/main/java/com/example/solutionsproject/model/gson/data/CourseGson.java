package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class CourseGson extends GsonData{
    @SerializedName("courseId")
    private String id;

    @SerializedName("author")
    private UserGson author;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("thumbnail")
    private String thumbnailUrl;

    @SerializedName("totalDuration")
    private String totalDuration;

    @SerializedName("dateAdded")
    private String dateAdded;

    @SerializedName("dateUpdated")
    private String dateUpdated;
}
