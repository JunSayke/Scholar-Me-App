package com.example.solutionsproject.model.gson.data;

import lombok.Getter;

@Getter
public class FlashcardChoiceGson extends GsonData {
    private int flashcardChoiceId;
    private int flashcardId;
    private String choice;
    private boolean isAnswer;
    private String dateAdded;
    private String dateUpdated;
}
