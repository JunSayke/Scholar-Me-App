package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CourseLearnerGson extends CourseGson {
    private int userId;
    private LocalDateTime dateEnrolled;

    @Builder(builderMethodName = "CourseLearnerGsonBuilder")
    public CourseLearnerGson(int courseId, UserGson author, String title, String description, String thumbnail, int views, int totalDuration, LocalDateTime dateAdded, LocalDateTime dateUpdated, int userId, LocalDateTime dateEnrolled) {
        super(courseId, author, title, description, thumbnail, views, totalDuration, dateAdded, dateUpdated);
        this.userId = userId;
        this.dateEnrolled = dateEnrolled;
    }
}