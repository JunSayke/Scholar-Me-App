package com.example.solutionsproject.model.gson.data;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class NotificationGson extends GsonData {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String dateAdded;
    private String dateUpdated;
}
