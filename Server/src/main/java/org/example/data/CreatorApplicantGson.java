package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class CreatorApplicantGson extends GsonData {
    private int creatorApplicantId;
    private int userId;
    private String status;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
