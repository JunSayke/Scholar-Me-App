package com.example.solutionsproject.model.gson.data;

import lombok.Getter;

@Getter
public class FlashcardSetGson extends GsonData {
    private int flashcardSetId;
    private int userid;
    private String title;
    private String description;
    private String dateAdded;
    private String dateUpdated;
}
