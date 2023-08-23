package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto updateByAdmin(Long commId, CommentDto commentDto);

    void deleteByAdmin(Long commId);

    CommentDto getByIdByAdmin(Long commId);

    List<CommentDto> findAllByAdmin(String text, List<Long> events, List<Long> users, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    CommentDto createByAuthor(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateByAuthor(Long userId, Long commId, CommentDto commentDto);

    void deleteByAuthor(Long userId, Long commId);

    CommentDto getByIdByAuthor(Long userId, Long commId);

    List<CommentShortDto> findAllByAuthor(Long userId, /*String text, List<Long> events,*/ LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<CommentShortDto> findAllByPublicUser(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}