package org.example.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseLearnerGson extends CourseGson {
    private int userId;
    private int dateEnrolled;

    CourseLearnerGson(int courseId, int author, String title, String description, String thumbnail, int visits, int dateAdded, int dateUpdated) {
        super(courseId, author, title, description, thumbnail, visits, dateAdded, dateUpdated);
    }
}