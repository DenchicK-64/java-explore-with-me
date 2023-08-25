package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto updateByAdmin(Long commId, UpdateCommentRequest updateCommentRequest);

    void deleteByAdmin(Long commId);

    CommentDto createByAuthor(Long userId, NewCommentDto newCommentDto);

    CommentDto updateByAuthor(Long userId, Long commId, UpdateCommentRequest updateCommentRequest);

    void deleteByAuthor(Long userId, Long commId);

    CommentDto getByIdByAuthor(Long userId, Long commId);

    List<CommentShortDto> findAllByAuthor(Long userId, Integer from, Integer size);

    CommentDto getByIdByPublicUser(Long commId);

    List<CommentShortDto> findAllByPublicUser(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}