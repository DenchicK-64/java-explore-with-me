package ru.practicum.main.request.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Validated
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam @NonNull Long eventId) {
        log.info("Добавление запроса от пользователя с id=" + userId + " на участие в событии c id=" + eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена пользователем с id=" + userId + " своего запроса c id=" + requestId);
        return requestService.cancel(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> findAllUserRequestsByUserId(@PathVariable Long userId) {
        log.info("Запрос всех заявок пользователя с id=" + userId + " на участие в чужих событиях");
        return requestService.findAllUserRequestsByUserId(userId);
    }
}