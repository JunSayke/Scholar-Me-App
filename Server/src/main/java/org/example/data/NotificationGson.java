package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class NotificationGson extends GsonData {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
