package ru.practicum.main.compilation.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "compilations")
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    @Length(min = 1, max = 50)
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;
    @ManyToMany
    @JoinTable(name = "compilations_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;

    @PrePersist
    public void prePersist() {
        if (pinned == null) {
            pinned = false;
        }
    }
}