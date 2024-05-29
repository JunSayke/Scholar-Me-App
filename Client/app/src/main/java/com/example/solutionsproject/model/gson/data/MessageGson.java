package com.example.solutionsproject.model.gson.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageGson extends GsonData {
    private int discussionCommentId;
    private int commendId;
    private String comment;
    private int userId;
    private String dateAdded;
    private String dateUpdated;
}
