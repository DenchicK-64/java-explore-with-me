package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.enums.EventSort;
import ru.practicum.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUser(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                  HttpServletRequest httpRequest) {
        log.info("client ip: {}", httpRequest.getRemoteAddr());
        log.info("endpoint path: {}", httpRequest.getRequestURI());
        log.info("Публичный запрос на получение полной информации обо всех событиях, подходящих под переданные условия");
        return eventService.getAllEventsByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getOneEventByUser(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Публичный запрос на получение полной информации о событии с id=" + id);
        return eventService.getOneEventByUser(id, httpRequest);
    }
}