package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.service.CommentService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@Validated
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/{commId}")
    public CommentDto updateByAdmin(@PathVariable Long commId, @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("Кoмментарий с id=" + commId + "изменён администратором. Текст: " + updateCommentRequest.getText());
        return commentService.updateByAdmin(commId, updateCommentRequest);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commId) {
        log.debug("Удаление администратором комментария с id=" + commId);
        commentService.deleteByAdmin(commId);
    }
}