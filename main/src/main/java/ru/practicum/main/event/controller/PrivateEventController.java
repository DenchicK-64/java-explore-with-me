package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createByInitiator(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Событие добавлено: {}", newEventDto.toString());
        return eventService.createByInitiator(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByInitiator(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Событие изменено: {}", updateEventUserRequest.getTitle());
        return eventService.updateByInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    public List<EventShortDto> findAllOwnEventsByInitiator(@PathVariable Long userId,
                                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Запрос пользователяем с id=" + userId + " всех добавленных им событий");
        return eventService.findAllOwnEventsByInitiator(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOneEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос пользователяем с id=" + userId + " добавленного им события с id=" + eventId);
        return eventService.getOneEventByInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllEventRequestsByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение пользователем с id=" + userId + " всех запросов на участие в его событии с id=" + eventId);
        return eventService.getAllEventRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsByInitiator(@PathVariable Long userId,
                                                                         @PathVariable Long eventId,
                                                                         @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменеие пользователем с id=" + userId + " запросов на участие в его событии с id=" + eventId);
        return eventService.updateEventRequestsByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }
}