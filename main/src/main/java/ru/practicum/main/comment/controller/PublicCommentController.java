package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@Validated
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{commId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getByIdByPublicUser(@PathVariable Long commId) {
        log.debug("Запрос публичным пользователем комментария с id=" + commId);
        return commentService.getByIdByPublicUser(commId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> findAllByPublicUser(@RequestParam(required = false) Long eventId,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос комментариев к событию с id=" + eventId + " публичным пользователем за период с " + rangeStart + " по " + rangeEnd);
        return commentService.findAllByPublicUser(eventId, rangeStart, rangeEnd, from, size);
    }
}