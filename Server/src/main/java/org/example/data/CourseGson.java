package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CourseGson extends GsonData {
    protected int courseId;
    protected int author;
    protected String title;
    protected String description;
    protected String thumbnail;
    protected int visits;
    protected int dateAdded;
    protected int dateUpdated;
}
