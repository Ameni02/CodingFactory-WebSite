package com.example.gestion_event.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class FeedbackResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private int rating;
    private String comment;
}
