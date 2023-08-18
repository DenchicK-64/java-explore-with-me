package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.EventSort;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto createByInitiator(Long userId, NewEventDto newEventDto);

    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> findAllOwnEventsByInitiator(Long userId, int from, int size);

    EventFullDto getOneEventByInitiator(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllEventRequestsByInitiator(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestsByInitiator(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventShortDto> getAllEventsByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                           HttpServletRequest httpRequest);

    EventFullDto getOneEventByUser(Long id, HttpServletRequest httpRequest);
}