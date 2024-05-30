package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ReplyGson extends GsonData {
    private int replyId;
    private int commentId;
    private int userId;
    private String reply;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
