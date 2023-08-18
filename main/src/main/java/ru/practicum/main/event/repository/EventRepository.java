package ru.practicum.main.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Set<Event> findAllByIdIn(Set<Long> ids);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    Boolean existsByCategory(Category category);

    @Query("SELECT e FROM Event e WHERE " +
            "(e.initiator.id IN :users OR :users IS NULL) AND " +
            "(e.state IN :states OR :states IS NULL) AND " +
            "(e.category.id IN :categories OR :categories IS NULL) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> getEventsByAdmin(@Param("users") List<Long> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND" +
            "((lower(e.annotation) LIKE lower(concat('%', :text, '%')) OR " +
            "(lower(e.description) LIKE lower(concat('%', :text, '%')))) OR :text IS NULL) AND " +
            "(e.category.id IN :categories OR :categories IS NULL) AND " +
            "(e.paid = :paid OR :paid IS NULL) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> getAllEvents(@Param("text") String text,
                             @Param("categories") List<Long> categories,
                             @Param("paid") Boolean paid,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND" +
            "((lower(e.annotation) LIKE lower(concat('%', :text, '%')) OR " +
            "(lower(e.description) LIKE lower(concat('%', :text, '%')))) OR :text IS NULL) AND " +
            "(e.category.id IN :categories OR :categories IS NULL) AND " +
            "(e.paid = :paid OR :paid IS NULL) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd) AND " +
            "e.participantLimit > e.confirmedRequests OR e.participantLimit = 0")
    List<Event> getAvailableEventsWithoutSorting(@Param("text") String text,
                                                 @Param("categories") List<Long> categories,
                                                 @Param("paid") Boolean paid,
                                                 @Param("rangeStart") LocalDateTime rangeStart,
                                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                                 PageRequest pageRequest);
}