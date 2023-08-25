package ru.practicum.main.comment.model;

import lombok.*;
@Getter
@Setter
@Builder
public class CommentCounter {
    private Long eventId;
    private Long counter;

    public CommentCounter(Long eventId, Long counter) {
        this.eventId = eventId;
        this.counter = counter;
    }
}