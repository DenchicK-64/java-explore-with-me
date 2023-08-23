package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@Validated
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createByAuthor(@PathVariable Long userId, @RequestParam(value = "eventId") Long eventId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Комментарий добавлен администратором c id=" + userId + " к событию с id=" + eventId + ". Текст комментария: " + newCommentDto.getText());
        return commentService.createByAuthor(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commId}")
    public CommentDto updateByAuthor(@PathVariable Long userId, @PathVariable Long commId, @RequestBody @Valid CommentDto commentDto) {
        log.info("Кoмментарий с id=" + commentDto.getId() + " изменён автором с id=" + userId + ". Текст: " + commentDto.getText());
        return commentService.updateByAuthor(userId, commId, commentDto);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAuthor(@PathVariable Long userId, @PathVariable Long commId) {
        log.debug("Удаление автором с id=" + userId + " своего комментария с id=" + commId);
        commentService.deleteByAuthor(userId, commId);
    }

    @GetMapping("/{commId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getByIdByAuthor(@PathVariable Long userId, @PathVariable Long commId) {
        log.debug("Получение автором с id=" + userId + " своего комментария с id=" + commId);
        return commentService.getByIdByAuthor(userId, commId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> findAllByAuthor(@PathVariable Long userId,
                                                 /*@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> events,*/
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос комментариев администратором за период с " + rangeStart + " по " + rangeEnd);
        return commentService.findAllByAuthor(userId, /*text, events,*/ rangeStart, rangeEnd, from, size);
    }
}
