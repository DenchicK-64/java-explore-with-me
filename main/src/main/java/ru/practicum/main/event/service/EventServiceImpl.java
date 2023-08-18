package ru.practicum.main.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.EventSort;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.EventStateAction;
import ru.practicum.main.event.location.mapper.LocationMapper;
import ru.practicum.main.event.location.model.Location;
import ru.practicum.main.event.location.repository.LocationRepository;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.DataConflictException;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.exceptions.ValidationException;

import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main.event.mapper.EventMapper.toEvent;
import static ru.practicum.main.event.mapper.EventMapper.toEventFullDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@ComponentScan("ru.practicum.client")
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    LocalDateTime DEFAULT_START_SEARCH_TIME = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
    LocalDateTime DEFAULT_END_SEARCH_TIME = LocalDateTime.of(2222, 1, 1, 1, 1, 1);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper;

    /*PRIVATE*/

    @Override
    public EventFullDto createByInitiator(Long userId, NewEventDto newEventDto) {
        checkTime(newEventDto.getEventDate());
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в базе данных"));
        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id" + catId + "не найдена в базе данных"));
        Location location = LocationMapper.toLocation(newEventDto.getLocation());
        locationRepository.save(location);
        Event event = toEvent(newEventDto, category, initiator, location);
        Event newEvent = eventRepository.save(event);
        EventFullDto eventFullDto = toEventFullDto(newEvent);
        log.info("Метод createByInitiator(), eventFullDto=" + eventFullDto.getAnnotation());
        return eventFullDto;
    }

    @Override
    public EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = checkEvent(userId, eventId);
        checkEventState(event.getState());
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id" + updateEventUserRequest.getCategory() + "не найдена в базе данных"));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            checkTime(updateEventUserRequest.getEventDate());
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = event.getLocation();
            location.setLon(updateEventUserRequest.getLocation().getLon());
            location.setLat(updateEventUserRequest.getLocation().getLat());
            locationRepository.save(location);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            updateEventStateByInitiator(event, updateEventUserRequest.getStateAction());
        }
        Event updEvent = eventRepository.save(event);
        return toEventFullDto(updEvent);
    }

    @Override
    public EventFullDto getOneEventByInitiator(Long userId, Long eventId) {
        Event event = checkEvent(userId, eventId);
        return toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findAllOwnEventsByInitiator(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        return events.stream().map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getAllEventRequestsByInitiator(Long userId, Long eventId) {
        Event event = checkEvent(userId, eventId);
        return requestRepository.findAllByEventId(eventId).stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsByInitiator(Long userId, Long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = checkEvent(userId, eventId);
        Long confirmedRequestsLimit = event.getParticipantLimit() - event.getConfirmedRequests();
        if (event.getRequestModeration() && confirmedRequestsLimit <= 0L) {
            throw new DataConflictException("Достигнут лимит по количеству запросов на участие");
        }
        List<Request> requests = requestRepository.findAllByIdInAndAndEventId(eventRequestStatusUpdateRequest.getRequestIds(), eventId);
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0L) {
            confirmedRequests.addAll(requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()));
        }
        if (event.getRequestModeration() && confirmedRequestsLimit > 0L) {
            for (Request request : requests) {
                if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    requestRepository.save(request);
                    ParticipationRequestDto participationRequestDto = RequestMapper.toParticipationRequestDto(request);
                    confirmedRequests.add(participationRequestDto);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(request);
                    ParticipationRequestDto participationRequestDto = RequestMapper.toParticipationRequestDto(request);
                    rejectedRequests.add(participationRequestDto);
                }
            }
        }
        return eventRequestStatusUpdateResult;
    }

    //ADMIN
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в базе данных"));
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id" + updateEventAdminRequest.getCategory() + "не найдена в базе данных"));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            checkTime(updateEventAdminRequest.getEventDate());
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = event.getLocation();
            location.setLon(updateEventAdminRequest.getLocation().getLon());
            location.setLat(updateEventAdminRequest.getLocation().getLat());
            locationRepository.save(location);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new DataConflictException("Событие можно отклонить, только если оно еще не опубликовано");
                }
                event.setState(EventState.CANCELED);
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new DataConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        }
        Event updEvent = eventRepository.save(event);
        return toEventFullDto(updEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("getEventsByAdmin, Параметры: " + users + ", " + states + ", " + ", " + categories + ", " + rangeStart + ", " + rangeEnd + ", from =" + from + ", size=" + size);
        if ((rangeStart != null && rangeEnd != null) && (rangeEnd.isBefore(rangeStart) || rangeStart.isAfter(rangeEnd))) {
            throw new ValidationException("Дата конца периода поиска не может быть раньше даты начала поиска и дата начала периода" +
                    "поиска не может быть позже даты конца периода поиска");
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        if (rangeStart == null) {
            rangeStart = DEFAULT_START_SEARCH_TIME;
        }
        if (rangeEnd == null) {
            rangeEnd = DEFAULT_END_SEARCH_TIME;
        }
        List<Event> events = eventRepository.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
        log.info("getEventsByAdmin: " + events);
        List<EventFullDto> fullEvents = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        log.info("getEventsByAdmin, EventsFullDto: " + fullEvents.stream());
        return fullEvents;
    }

//PUBLIC

    public EventFullDto getOneEventByUser(Long id, HttpServletRequest httpRequest) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Событие с id " + id + " не найдено в базе данных"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие не опубликовано");
        }
        addHitToStats(httpRequest);
        List<Event> events = List.of(event);
        Map<Long, Long> views = getViewsFromStats(events);
        log.info("Содержимое Map<Long, Long> views: " + views);
        event.setViews(views.getOrDefault(id, 0L));
        eventRepository.save(event);
        EventFullDto eventFullDto = toEventFullDto(event);
        log.info("EventFullDto =" + eventFullDto.getViews());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                                  HttpServletRequest httpRequest) {
        log.info("getAllEventsByUser Параметры: " + text + ", " + paid + ", " + ", " + categories + ", " + rangeStart + ", " + rangeEnd + ", from =" + from + ", size=" + size);
        if ((rangeStart != null && rangeEnd != null) && (rangeEnd.isBefore(rangeStart) || rangeStart.isAfter(rangeEnd))) {
            throw new ValidationException("Дата конца периода поиска не может быть раньше даты начала поиска и дата начала периода" +
                    "поиска не может быть позже даты конца периода поиска");
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        if (categories == null || categories.isEmpty()) {
            categories = categoryRepository.findAll().stream().map(Category::getId).collect(Collectors.toList());
        }
        if (rangeStart == null) {
            rangeStart = DEFAULT_START_SEARCH_TIME;
        }
        if (rangeEnd == null) {
            rangeEnd = DEFAULT_END_SEARCH_TIME;
        }
        log.info("pageRequest=" + pageRequest);
        List<Event> events;
        if (onlyAvailable) {
            events = eventRepository.getAvailableEventsWithoutSorting(text, categories, paid, rangeStart, rangeEnd, pageRequest);
            addHitToStats(httpRequest);
            Map<Long, Long> views = getViewsFromStats(events);
            log.info("VIEWS:" + views);
            if (sort == null) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
            if (sort.equals(EventSort.VIEWS)) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
            if (sort.equals(EventSort.EVENT_DATE)) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
        } else {
            events = eventRepository.getAllEvents(text, categories, paid, rangeStart, rangeEnd, pageRequest);
            addHitToStats(httpRequest);
            Map<Long, Long> views = getViewsFromStats(events);
            log.info("VIEWS:" + views);
            if (sort == null) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
            if (sort.equals(EventSort.VIEWS)) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
            if (sort.equals(EventSort.EVENT_DATE)) {
                List<EventShortDto> dtos = events.stream()
                        .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                        .peek(eventRepository::save)
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("dtos.size() =" + dtos.size() + ". First:" + dtos.get(0));
                return dtos;
            }
        }
        return new ArrayList<>();
    }

    private Event checkEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Событие с id " + eventId + " не найдено в базе данных");
        }
        return event;
    }

    private void checkTime(LocalDateTime time) {
        if (time.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала события не может быть раньше текущего времени");
        }
        if (time.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата начала события не может быть раньше, чем через 2 часа после текущего времени");
        }
    }

    private void checkEventState(EventState state) {
        if (!(state.equals(EventState.PENDING) || state.equals(EventState.CANCELED))) {
            throw new DataConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
    }

    private void updateEventStateByInitiator(Event event, EventStateAction eventStateAction) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        if (eventStateAction.equals(EventStateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        }
        if (eventStateAction.equals(EventStateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        }
    }

    private void addHitToStats(HttpServletRequest httpRequest) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("main");
        endpointHitDto.setUri(httpRequest.getRequestURI());
        endpointHitDto.setIp(httpRequest.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClient.create(endpointHitDto);
    }

    private Map<Long, Long> getViewsFromStats(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        String eventsUri = "/events/";
        String[] uris = ids.stream().map(id -> eventsUri + id).toArray(String[]::new);
        ResponseEntity<Object> objects = statsClient.getStats(DEFAULT_START_SEARCH_TIME, DEFAULT_END_SEARCH_TIME, uris, true);
        List<ViewStatsDto> viewStatsDtoList = objectMapper.convertValue(objects.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });
        Map<Long, Long> views = new HashMap<>();
        for (ViewStatsDto viewStatDto : viewStatsDtoList) {
            String uri = viewStatDto.getUri();
            String[] split = uri.split("/");
            String str = split[2];
            Long id = Long.parseLong(str);
            views.put(id, viewStatDto.getHits());
        }
        return views;
    }
}