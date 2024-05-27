package com.example.solutionsproject.model.gson.data;

import lombok.Getter;

@Getter
public class FlashcardGson extends GsonData {
    private int flashcardId;
    private int userid;
    private String question;
    private String dateAdded;
    private String dateUpdated;
}