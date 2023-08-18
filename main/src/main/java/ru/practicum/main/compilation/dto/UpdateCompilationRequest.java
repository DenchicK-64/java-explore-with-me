package ru.practicum.main.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Название подборки не может содержать более 50 символов")
    private String title;
}