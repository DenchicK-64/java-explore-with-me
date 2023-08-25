package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public CommentDto createByAuthor(@PathVariable Long userId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Комментарий добавлен пользователем c id=" + userId + " к событию с id=" + newCommentDto.getEventId() + ". Текст комментария: " + newCommentDto.getText());
        return commentService.createByAuthor(userId, newCommentDto);
    }

    @PatchMapping("/{commId}")
    public CommentDto updateByAuthor(@PathVariable Long userId, @PathVariable Long commId, @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("Кoмментарий с id=" + commId + " изменён автором с id=" + userId + ". Текст: " + updateCommentRequest.getText());
        return commentService.updateByAuthor(userId, commId, updateCommentRequest);
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
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос комментариев администратором за период с id=" + userId);
        return commentService.findAllByAuthor(userId, from, size);
    }
}