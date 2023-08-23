package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@Validated
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createByAdmin(@RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Комментарий добавлен администратором: {}", newCommentDto.getText());
        return commentService.createByAdmin(newCommentDto);
    }*/

    @PatchMapping("/{commId}")
    public CommentDto updateByAdmin(@PathVariable Long commId, @RequestBody @Valid CommentDto commentDto) {
        log.info("Кoмментарий с id=" + commentDto.getId() + "изменён администратором. Текст: " + commentDto.getText());
        return commentService.updateByAdmin(commId, commentDto);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commId) {
        log.debug("Удаление администратором комментария с id=" + commId);
        commentService.deleteByAdmin(commId);
    }

    @GetMapping("/{commId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getByIdByAdmin(@PathVariable Long commId) {
        log.debug("Получение администратором комментария с id=" + commId);
        return commentService.getByIdByAdmin(commId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDto> findAllByAdmin(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> events,
                                           @RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос комментариев администратором за период с " + rangeStart + " по " + rangeEnd);
        return commentService.findAllByAdmin(text, events, users, rangeStart, rangeEnd, from, size);
    }
}