package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.EndpointHit;
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
        return EndpointHitMapper.toEndpointHitDto(newEndpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            if (unique) {
                return ViewStatsMapper.listToDto(statsRepository.getUniqueStats(start, end));
            } else {
                return ViewStatsMapper.listToDto(statsRepository.getAllStats(start, end));
            }
        } else {
            if (unique) {
                return ViewStatsMapper.listToDto(statsRepository.getUniqueStatsUri(start, end, uris));
            } else {
                return ViewStatsMapper.listToDto(statsRepository.getAllStatsUri(start, end, uris));
            }
        }
    }
}