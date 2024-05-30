package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CourseLessonGson extends GsonData {
    private int courseLessonId;
    private int courseId;
    private String title;
    private int lessonNumber;
    private String description;
    private String content;
    private int duration;
    private boolean isLocked;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
