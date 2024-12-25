package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.SearchStockDto;
import org.example.dto.UpdateSockDto;
import org.example.service.SockService;
import org.example.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class SockControllerTest {

    @MockBean
    private SockService sockService;
    @MockBean
    StockService stockService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private SockController sockController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }



    @Test
    void testUpdateSocks_Success() throws Exception {
        Long sockId = 1L;
        UpdateSockDto sockDto = new UpdateSockDto("Red", 50);

        doNothing().when(sockService).updateSockEntity(sockId, sockDto);

        mockMvc.perform(put("/api/socks/" + sockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto)))
                .andExpect(status().isNoContent()); // Проверка, что вернулся статус 204 (No Content)

        verify(sockService, times(1)).updateSockEntity(sockId, sockDto);
    }

    // Тест на невалидные поля при обновлении товара
    @Test
    void testUpdateSocks_InvalidDto() throws Exception {
        long sockId = 1L;
        UpdateSockDto sockDto = new UpdateSockDto("", -10); // Невалидные данные

        mockMvc.perform(put("/api/socks/" + sockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.color").value("Поле color - обязательно для заполнения"))
                .andExpect(jsonPath("$.cottonPercentage").value("Содержание хлопка в процентах должно быть не менее 0"));

        verify(sockService, never()).updateSockEntity(anyLong(), any(UpdateSockDto.class));
    }

    // Тест успешного увеличения количества
    @Test
    void testIncomeSock_Success() throws Exception {
        SearchStockDto searchStockDto = new SearchStockDto("Red", 24, 150);

        doNothing().when(stockService).addedStock(anyList());

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchStockDto)))
                .andExpect(status().isNoContent());  // Проверка, что вернулся статус 204 (No Content)

        verify(stockService).addedStock(anyList());
    }

    // Тест на валидность полей при поступлении товара
    @Test
    void testIncomeSock_ErrorArguments() throws Exception {
        SearchStockDto searchStockDto = new SearchStockDto();
        searchStockDto.setCottonPercentage(120);
        searchStockDto.setQuantity(-1);
        doNothing().when(stockService).addedStock(anyList());

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchStockDto)))
                .andExpect(status().isBadRequest()) // Проверка, что вернулся статус 400 (No Content)
                .andExpect(jsonPath("$.color").value("Поле color - обязательно для заполнения"))
                .andExpect(jsonPath("$.cottonPercentage").value("Значение поля cottonPercentage не должно превышать 100"))
                .andExpect(jsonPath("$.quantity").value("Минимальное значение поля quantity не может быть меньше чем 1"));

        verify(sockService, never()).updateSockEntity(anyLong(), any(UpdateSockDto.class));
    }

    // Тест успешного уменьшения количества
    @Test
    void testOutcomeSock_Success() throws Exception {

        SearchStockDto searchStockDto = new SearchStockDto("Red", 24, 15000);

        doNothing().when(stockService).removeStock(anyList());

        mockMvc.perform(post("/api/socks/outcome")  // Путь к методу контроллера
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchStockDto)))
                .andExpect(status().isNoContent());  // Проверка, что вернулся статус 204 (No Content)

        // Проверка, что метод addStock был вызван один раз
        verify(stockService,times(1)).removeStock(anyList());
    }

    // Тест на валидность полей при списании товара
    @Test
    void testOutcomeSock__ErrorArguments() throws Exception {

        SearchStockDto searchStockDto = new SearchStockDto();
        searchStockDto.setCottonPercentage(-10);
        searchStockDto.setQuantity(0);

        doNothing().when(stockService).removeStock(anyList());

        mockMvc.perform(post("/api/socks/outcome")  // Путь к методу контроллера
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchStockDto)))
                .andExpect(status().isBadRequest())  // Проверка, что вернулся статус 404 (Not Found)
                .andExpect(jsonPath("$.color").value("Поле color - обязательно для заполнения"))
                .andExpect(jsonPath("$.cottonPercentage").value("Значение поля cottonPercentage не должно быть меньше чем 0"))
                .andExpect(jsonPath("$.quantity").value("Минимальное значение поля quantity не может быть меньше чем 1"));

        verify(sockService, never()).updateSockEntity(anyLong(), any(UpdateSockDto.class));
    }
}