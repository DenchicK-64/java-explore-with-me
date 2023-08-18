package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long EventId, RequestStatus status);

    List<Request> findAllByIdInAndAndEventId(List<Long> ids, Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);
}