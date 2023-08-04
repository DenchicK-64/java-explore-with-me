package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Сохранение информации о том, что на uri = " + endpointHitDto.getUri() + " был отправлен запрос пользователем. " +
                "Название сервиса = " + endpointHitDto.getApp() + ", ip пользователя = " + endpointHitDto.getIp() +
                ", дата создания запроса = " + endpointHitDto.getTimestamp());
        return statsService.create(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получение статистики по посещениям." +
                " Дата и впемя начала диапозона, за который нужно выгрузить статистику = " + start +
                ", дата и впемя конца диапозона, за который нужно выгрузить статистику = " + end +
                ", список uri, для которых нужно выгрузить статистику = " + uris +
                ", нужно ли учитывать только уникальные посещения = " + unique);
        return statsService.getStats(start, end, uris, unique);
    }
}