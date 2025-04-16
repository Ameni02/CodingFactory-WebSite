package com.example.gestion_event.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class RatingRequest {
    private Long eventId;
    private Long userId;
    private int rating; // e.g., 1 to 5

}
