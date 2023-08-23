package ru.practicum.main.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndAuthorId(Long commId, Long userId);

    @Query("SELECT c FROM Comment c WHERE (c.author.id = :userId) AND " +
            "(c.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Comment> findAllByAuthor(@Param("userId") Long userId,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  PageRequest pageRequest);

    @Query("SELECT c FROM Comment c WHERE " +
            "(lower(c.text) LIKE lower(concat('%', :text, '%')) OR :text IS NULL) AND " +
            "(c.event.id IN :events OR :events IS NULL) AND " +
            "(c.author.id IN :users OR :users IS NULL) AND " +
            "(c.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Comment> findAllByAdmin(@Param("text") String text,
                                 @Param("events") List<Long> events,
                                 @Param("users") List<Long> users,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 PageRequest pageRequest);

    @Query("SELECT c FROM Comment c " +
            "WHERE (c.event.id = :eventId) " +
            "AND (c.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Comment> findAllByPublicUser(@Param("eventId") Long eventId,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      PageRequest pageRequest);
}