package ru.practicum.main.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    @NotBlank(message = "Поле <имя> не может быть пустым")
    private String name;
}