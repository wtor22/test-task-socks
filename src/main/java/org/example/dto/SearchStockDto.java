package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос контроллеров увеличения или уменьшения остатков товаров")
public class SearchStockDto {

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
    @Min(value = 0, message = "Значение поля cottonPercentage не должно быть меньше чем 0")
    @Max(value = 100, message = "Значение поля cottonPercentage не должно превышать 100")
    private Integer cottonPercentage;
    @Schema(
            description = "Количество носков в запросе. Значение не может быть меньше чем 1",
            example = "10"
    )
    @Min(value = 1, message = "Минимальное значение поля quantity не может быть меньше чем 1")
    private int quantity;

}
