package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class NotificationGson extends GsonData {
    @SerializedName("notificationId")
    private int notificationId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("dateAdded")
    private String dateAdded;

    @SerializedName("dateUpdated")
    private String dateUpdated;
}
