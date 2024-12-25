package org.example.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SockDto {

    @Schema(hidden = true) // Скрыть поле id
    private Long id;

    @NotBlank(message = "Поле color - обязательно для заполнения")
    private String color;

    @NotNull(message = "Поле cottonPercentage - обязательно для заполнения")
    @Min(value = 0, message = "Значение поля cottonPercentage не должно быть меньше чем 0")
    @Max(value = 100, message = "Значение поля cottonPercentage не должно превышать 100")
    private Integer cottonPercentage;

    public String toString() {
        return "носки {" +
                "id=" + id +
                ", цвет='" + color + '\'' +
                ", содержание хлопка=" + cottonPercentage +
                '}';
    }

}
