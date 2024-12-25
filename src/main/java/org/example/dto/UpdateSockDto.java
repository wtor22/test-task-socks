package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос для обновления носков")
public class UpdateSockDto {

    @Schema(
            description = "Цвет носков в запросе",
            example = "red"
    )
    @NotBlank(message = "Поле color - обязательно для заполнения")
    private String color;

    @Schema(
            description = "Процентное содержание хлопка. Значение может быть в диапазоне от 0 до 100",
            example = "10"
    )
    @NotNull(message = "Поле cottonPercentage - обязательно для заполнения")
    @Min(value = 0, message = "Содержание хлопка в процентах должно быть не менее 0")
    @Max(value = 100, message = "Содержание хлопка в процентах не может превышать 100")
    private Integer cottonPercentage;

}
