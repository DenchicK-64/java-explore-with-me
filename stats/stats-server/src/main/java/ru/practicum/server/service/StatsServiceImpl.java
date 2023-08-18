package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.exceptions.ValidationException;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.StatsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        EndpointHit newEndpointHit = statsRepository.save(endpointHit);
        log.info("Сохранённый эндпоинт: " + newEndpointHit);
        return EndpointHitMapper.toEndpointHitDto(newEndpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата конца периода поиска не может быть раньше даты начала поиска и дата начала периода " +
                    "поиска не может быть позже даты конца периода поиска");
        }
        if (uris == null) {
            if (unique) {
                log.info("Возвращаемое значение getStats: " + ViewStatsMapper.listToDto(statsRepository.getUniqueStats(start, end)).toString());
                List<ViewStats> viewStatsList = statsRepository.getUniqueStats(start, end);
                log.info("getStats, уникальные значения без URI:" + viewStatsList.toString());
                List<ViewStatsDto> viewStatsDtoList = ViewStatsMapper.listToDto(viewStatsList);
                log.info("getStats, DTO, уникальные значения без URI:" + viewStatsList);
                return viewStatsDtoList;

            } else {
                log.info("Возвращаемое значение getStats: " + ViewStatsMapper.listToDto(statsRepository.getAllStats(start, end)).toString());
                List<ViewStats> viewStatsList = statsRepository.getAllStats(start, end);
                log.info("getStats, все значения без URI:" + viewStatsList.toString());
                List<ViewStatsDto> viewStatsDtoList = ViewStatsMapper.listToDto(viewStatsList);
                log.info("getStats, DTO, все значения без URI:" + viewStatsList);
                return viewStatsDtoList;
            }
        } else {
            if (unique) {
                log.info("Возвращаемое значение getStats: " + ViewStatsMapper.listToDto(statsRepository.getUniqueStatsUri(start, end, uris)).toString());
                List<ViewStats> viewStatsList = statsRepository.getUniqueStatsUri(start, end, uris);
                log.info("getStats, уникальные значения c URI:" + viewStatsList.toString());
                List<ViewStatsDto> viewStatsDtoList = ViewStatsMapper.listToDto(viewStatsList);
                log.info("getStats, DTO, уникальные значения c URI:" + viewStatsList);
                return viewStatsDtoList;
            } else {
                log.info("Возвращаемое значение getStats: " + ViewStatsMapper.listToDto(statsRepository.getAllStatsUri(start, end, uris)).toString());
                List<ViewStats> viewStatsList = statsRepository.getAllStatsUri(start, end, uris);
                log.info("getStats, уникальные значения c URI:" + viewStatsList.toString());
                List<ViewStatsDto> viewStatsDtoList = ViewStatsMapper.listToDto(viewStatsList);
                log.info("getStats, DTO, уникальные значения c URI:" + viewStatsList);
                return viewStatsDtoList;
            }
        }
    }
}