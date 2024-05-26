package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
public class FlashcardGson extends GsonData {
    private int flashcardId;
    private int userid;
    private String question;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
