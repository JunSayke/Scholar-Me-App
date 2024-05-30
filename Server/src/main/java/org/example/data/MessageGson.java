package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageGson extends GsonData {
    private int discussionCommentId;
    private int commendId;
    private String comment;
    private UserGson user;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
