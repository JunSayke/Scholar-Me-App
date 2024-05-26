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
public class CommentGson extends GsonData {
    private int commentId;
    private int userId;
    private String comment;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
