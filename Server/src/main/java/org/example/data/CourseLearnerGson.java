package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseLearnerGson extends CourseGson {
    private int userId;
    private int dateEnrolled;

    @Builder(builderMethodName = "CourseLearnerGsonBuilder")
    public CourseLearnerGson(int courseId, UserGson author, String title, String description, String thumbnail, int visits, int dateAdded, int dateUpdated, int userId, int dateEnrolled) {
        super(courseId, author, title, description, thumbnail, visits, dateAdded, dateUpdated);
        this.userId = userId;
        this.dateEnrolled = dateEnrolled;
    }
}