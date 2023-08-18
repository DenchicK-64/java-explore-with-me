package ru.practicum.main.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Поле <имя> не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя не может содержать более 250 символов")
    private String name;
    @Email(message = "Некорректно указан email")
    @NotBlank(message = "Поле <email> не может быть пустым")
    @Size(min = 6, max = 254, message = "email не может содержать более 254 символов")
    private String email;
}