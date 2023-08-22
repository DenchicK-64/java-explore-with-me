package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.DataConflictException;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.request.mapper.RequestMapper.toParticipationRequestDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DataConflictException("Нельзя добавить повторный запрос");
        }
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в базе данных"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в базе данных"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new DataConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        Long confirmedRequestsLimit = event.getParticipantLimit() - event.getConfirmedRequests();
        if (event.getParticipantLimit() != 0L && confirmedRequestsLimit <= 0L) {
            throw new DataConflictException("Достигнут лимит по количеству запросов на участие");
        }
        Request request;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0L) {
            request = Request.builder()
                    .created(LocalDateTime.now())
                    .event(event)
                    .requester(requester)
                    .status(RequestStatus.CONFIRMED)
                    .build();
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            request = Request.builder()
                    .created(LocalDateTime.now())
                    .event(event)
                    .requester(requester)
                    .status(RequestStatus.PENDING)
                    .build();
        }
        Request newRequest = requestRepository.save(request);
        return toParticipationRequestDto(newRequest);
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в базе данных"));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id " + requestId + " не найден в базе данных"));
        Event event = eventRepository.findById(request.getEvent().getId()).orElseThrow(() ->
                new NotFoundException("Событие с id " + request.getEvent().getId() + " не найдено в базе данных"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new DataConflictException("Пользователь не является автором запроса");
        }
        request.setStatus(RequestStatus.CANCELED);
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        Request updRequest = requestRepository.save(request);
        return toParticipationRequestDto(updRequest);
    }

    @Override
    public List<ParticipationRequestDto> findAllUserRequestsByUserId(Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в базе данных"));
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        if (!requests.isEmpty()) {
            return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}