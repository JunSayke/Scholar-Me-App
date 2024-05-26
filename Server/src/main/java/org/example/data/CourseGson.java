package org.example.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class CourseGson extends GsonData {
    protected int courseId;
    protected UserGson author;
    protected String title;
    protected String description;
    protected String thumbnail;
    protected int views;
    protected int totalDuration;
    protected LocalDateTime dateAdded;
    protected LocalDateTime dateUpdated;
}
