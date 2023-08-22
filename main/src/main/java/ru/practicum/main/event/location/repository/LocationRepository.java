package ru.practicum.main.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.event.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}