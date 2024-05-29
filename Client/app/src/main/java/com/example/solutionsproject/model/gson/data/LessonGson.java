package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class LessonGson {
    @SerializedName("courseLessonId")
    private int courseLessonId;

    @SerializedName("courseId")
    private int courseId;

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
