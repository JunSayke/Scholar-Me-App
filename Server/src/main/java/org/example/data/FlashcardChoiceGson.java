package org.example.data;

import java.time.LocalDateTime;

public class FlashcardChoiceGson extends GsonData {
    private int flashcardChoiceId;
    private int flashcardId;
    private String choice;
    private Boolean isAnswer;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
