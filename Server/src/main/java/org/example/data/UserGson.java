package org.example.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class UserGson extends GsonData {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePic;
    private String phoneNumber;
    private String role;
    private String status;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
