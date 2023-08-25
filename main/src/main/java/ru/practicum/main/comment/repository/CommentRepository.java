package ru.practicum.main.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.model.CommentCounter;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndAuthorId(Long commId, Long userId);

    List<Comment> findAllByEventId(Long eventId, PageRequest pageRequest);

    List<Comment> findAllByAuthorId(Long userId, PageRequest pageRequest);

    @Query("SELECT c FROM Comment c " +
            "WHERE (c.event.id = :eventId OR :eventId IS NULL) " +
            "AND (c.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Comment> findAllByPublicUser(@Param("eventId") Long eventId,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      PageRequest pageRequest);

    @Query("SELECT new ru.practicum.main.comment.model.CommentCounter(c.event.id, COUNT (c.id)) " +
            "FROM Comment c " +
            "WHERE (c.event.id IN :ids) " +
            "GROUP BY c.event.id")
    List<CommentCounter> findNumberAllCommentsByEventId(List<Long> ids);
}