package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.service.CsvParserService;
import org.example.service.SockService;
import org.example.service.StockService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("/api/socks")
@Tag(name = "Управление товарами", description = "Эндпоинты для работы с товарами: списание, оприходование, изменение.")
public class SockController {

    private final CsvParserService csvParserService;

    private final StockService stockService;
    private final SockService sockService;

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление полей товара",
            description = "Контроллер принимает id товара, цвет и процентное содержание хлопка осуществляет поиск товара" +
                    " по id в БД. Обновляет поля цвет и процентное содержание хлопка. При обращении к несуществующему " +
                    "товару в БД возвращается ошибка."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Параметры носков успешно обновлены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации: указаны некорректные параметры",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"color\": \"Поле color - обязательно для заполнения\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар с указанным ID не найден",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Товар с id: 5 не найден"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Товар с указанным параметрами уже существует",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Товар  носки {id=12, цвет='black', содержание хлопка=80} уже существует"
                            )
                    )
            )
    })
    public ResponseEntity<Void> updateSocks(@PathVariable Long id, @Valid @RequestBody UpdateSockDto sock) {
        log.info("Получен запрос на обновление товара с id: {}, новый цвет: {}, новое содержание хлопка: {}", id, sock.getColor(), sock.getCottonPercentage());
        sockService.updateSockEntity(id, sock);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/income")
    @Operation(
            summary = "Поступление товаров",
            description = "Контроллер принимает товар с полями: цвет, процентное содержание хлопка и количество, " +
                    "и обновляет остатки товара увеличивая их. Несуществующие товары в БД создаются."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Запрос успешно обработан, остатки обновлены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации: указаны некорректные параметры",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"color\": \"Поле color - обязательно для заполнения\" }"
                            )
                    )
            )
    })
    public ResponseEntity<Void> incomeSock(@RequestBody @Valid SearchStockDto searchStockDto) {

        log.info("Получен запрос на добавление товара  цвет: {}, содержание хлопка: {}", searchStockDto.getColor(), searchStockDto.getCottonPercentage());
        // Пока так, что бы переиспользовать метод и не создавать новый - запихиваю dto в List
        stockService.addedStock(getStockDtoList(new ArrayList<>(List.of(searchStockDto))));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outcome")
    @Operation(
            summary = "Отгрузка товаров",
            description = "Контроллер принимает товар с полями: цвет, процентное содержание хлопка и количество, " +
                    "и обновляет остатки товара уменьшая их. При обращении к несуществующему товару в БД возвращается ошибка."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Запрос успешно обработан, остатки обновлены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации: указаны некорректные параметры",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"color\": \"Поле color - обязательно для заполнения\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар с указанным параметрами не найден",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Товар  с полями черный/10 не найден"
                            )
                    )
            )
    })
    public ResponseEntity<String> outcomeSock(@RequestBody @Valid SearchStockDto searchStockDto) {
        log.info("Получен запрос на уменьшение количества товара  цвет: {}, содержание хлопка: {}", searchStockDto.getColor(), searchStockDto.getCottonPercentage());
        // Пока так, что бы переиспользовать существующий метод и не создавать новый - запихиваю dto в List
        stockService.removeStock(getStockDtoList(new ArrayList<>(List.of(searchStockDto))));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Поступление партии товаров",
            description = "Контроллер принимает файл CSV, парсит его и обновляет остатки товаров на складе, увеличивая их."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Файл успешно обработан, остатки обновлены",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка при обработке файла. Возможные причины: неверный формат файла, повреждённый файл" +
                            " или некорректный формат данных",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Ошибки при обработке файла. Позиция : green,4,Зеленый"
                            )
                    )
            )
    })
    public ResponseEntity<String> incomeSockBatch(
            @Parameter(description = "Файл для загрузки", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {

            // Обрабатываем файл и создаем список DTO для обновления остатков
            List<StockDto> stockDtoList = csvParserService.parseCsvFile(file);

            // Обновляем остатки на складе
            stockService.addedStock(stockDtoList);

        return ResponseEntity.noContent().build();

    }

    @GetMapping
    @Operation(
            summary = "Получение общего количества носков с фильтрацией",
            description = "Возвращает количество носков, соответствующих заданным критериям фильтрации. " +
                    "параметры для процентного содержания хлопка: moreThan - более чем, lessThan - менее чем, equal равно " +
                    "если заполнены все поля фильтрация происходит по диапазону"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Количество носков успешно получено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "123")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка в параметрах фильтрации",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Некорректный тип данных для поля: 'moreThan'")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Логическая ошибка в параметрах фильтрации",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "Ошибка бизнес-логики: 'lessThan' должно быть больше 'moreThan'.")
                    )

            )

    })
    public ResponseEntity<Integer> getSocksQuantity(
            @RequestParam @NotNull(message = "Поле color - обязательно для заполнения") String color,
            @RequestParam(required = false) @Min(value = 0, message = "Значение поля moreThan не должно быть меньше чем 0") @Max(value = 100, message = "Значение поля moreThan не должно превышать 100") Integer moreThan,
            @RequestParam(required = false)  @Min(value = 0, message = "Значение поля moreThan не должно быть меньше чем 0") @Max(value = 100, message = "Значение поля moreThan не должно превышать 100") Integer lessThan,
            @RequestParam(required = false) @Min(value = 0, message = "Значение поля moreThan не должно быть меньше чем 0") @Max(value = 100, message = "Значение поля moreThan не должно превышать 100") Integer equal) {

        Integer quantity = stockService.getSocksQuantity(color,moreThan,lessThan,equal);

        if(quantity == null)
            return ResponseEntity.badRequest().body(0);

        return ResponseEntity.ok(quantity);
    }


    private List<StockDto> getStockDtoList(List<SearchStockDto> searchStockDtoList) {

        List<StockDto> stockDtoList = new ArrayList<>();

        for(SearchStockDto searchStockDto: searchStockDtoList ) {
            SockDto sockDto = new SockDto();
            sockDto.setColor(searchStockDto.getColor());
            sockDto.setCottonPercentage(searchStockDto.getCottonPercentage());

            StockDto stockDto = new StockDto();
            stockDto.setSockDto(sockDto);
            stockDto.setQuantity(searchStockDto.getQuantity());
            stockDtoList.add(stockDto);
        }
        log.info("START GET STOCK DTO LIST " + stockDtoList.size());
        return stockDtoList;
    }



}
