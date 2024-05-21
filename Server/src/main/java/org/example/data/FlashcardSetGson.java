package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class FlashcardSetGson extends GsonData {
    private int flashcardSetId;
    private int userid;
    private String title;
    private String description;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
