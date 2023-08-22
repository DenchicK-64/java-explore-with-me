package ru.practicum.main.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Поле <название категории> не может быть пустым")
    @Size(min = 1, max = 50, message = "Название категории не может содержать более 50 символов")
    String name;
}