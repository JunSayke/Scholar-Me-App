package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class LessonGson {
    private String courseLessonId;

    @SerializedName("courseId")
    private String courseId;

    @SerializedName("title")
    private String title;

    @SerializedName("lessonNumber")
    private String lessonNumber;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("duration")
    private String duration;
}
