package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentShortDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentRequest;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.exceptions.ValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.comment.mapper.CommentMapper.toComment;
import static ru.practicum.main.comment.mapper.CommentMapper.toCommentDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    //PRIVATE

    @Override
    public CommentDto createByAuthor(Long userId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в базе данных"));
        Long eventId = newCommentDto.getEventId();
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в базе данных"));
        Comment comment = toComment(newCommentDto, author, event);
        Comment newComment = commentRepository.save(comment);
        CommentDto commentDto = toCommentDto(newComment);
        log.info("Метод createByAuthor(), commentDto=" + commentDto.getText());
        return commentDto;
    }

    @Override
    public CommentDto updateByAuthor(Long userId, Long commId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = checkComment(commId, userId);
        if (updateCommentRequest.getText() != null) {
            comment.setText(updateCommentRequest.getText());
        }
        comment.setUpdatedOn(LocalDateTime.now());
        Comment updComment = commentRepository.save(comment);
        return toCommentDto(updComment);
    }

    @Override
    public void deleteByAuthor(Long userId, Long commId) {
        Comment comment = checkComment(commId, userId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto getByIdByAuthor(Long userId, Long commId) {
        Comment comment = checkComment(commId, userId);
        return toCommentDto(comment);
    }

    @Override
    public List<CommentShortDto> findAllByAuthor(Long userId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageRequest);
        List<CommentShortDto> dtos = comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
        log.info("findAllByAuthor, dtos = : " + dtos.size() + ". Created on: " + dtos.get(0).getCreatedOn() + ", " + dtos.get(1).getCreatedOn());
        return dtos;
    }

    //ADMIN

    @Override
    public CommentDto updateByAdmin(Long commId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findById(commId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commId + " не найден в базе данных"));
        if (updateCommentRequest.getText() != null) {
            comment.setText(updateCommentRequest.getText());
        }
        comment.setUpdatedOn(LocalDateTime.now());
        Comment updComment = commentRepository.save(comment);
        return toCommentDto(updComment);
    }

    @Override
    public void deleteByAdmin(Long commId) {
        Comment comment = commentRepository.findById(commId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commId + " не найден в базе данных"));
        commentRepository.delete(comment);
    }

    //PUBLIC

    @Override
    public CommentDto getByIdByPublicUser(Long commId) {
        Comment comment = commentRepository.findById(commId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commId + " не найден в базе данных"));
        return toCommentDto(comment);
    }

    @Override
    public List<CommentShortDto> findAllByPublicUser(Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Integer from, Integer size) {
        if ((rangeStart != null && rangeEnd != null) && (rangeEnd.isBefore(rangeStart) || rangeStart.isAfter(rangeEnd))) {
            throw new ValidationException("Дата конца периода поиска не может быть раньше даты начала поиска и дата начала периода" +
                    "поиска не может быть позже даты конца периода поиска");
        }
        List<Comment> comments;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        if (eventId == null) {
            comments = commentRepository.findAll(pageRequest).toList();
        }
        if ((rangeStart == null) && (rangeEnd == null)) {
            comments = commentRepository.findAllByEventId(eventId, pageRequest);
        }
        comments = commentRepository.findAllByPublicUser(eventId, rangeStart, rangeEnd, pageRequest);
        List<CommentShortDto> dtos = comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
        log.info("findAllByAuthor, dtos = : " + dtos.size() + ". Created on: " + dtos.get(0).getCreatedOn() + ", " + dtos.get(1).getCreatedOn());
        return dtos;
    }

    private Comment checkComment(Long commId, Long userId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commId, userId);
        if (comment == null) {
            throw new NotFoundException("Комментарий с id " + commId + " пользователся с id " + userId + " не найден в базе данных");
        }
        return comment;
    }
}