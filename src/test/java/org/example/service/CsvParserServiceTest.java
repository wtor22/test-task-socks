package org.example.service;

import org.example.dto.StockDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvParserServiceTest {

    private CsvParserService csvParserService;

    @BeforeEach
    void setUp() {
        csvParserService = new CsvParserService();
    }

    @Test
    void shouldParseCsvFileCorrectly() throws Exception {
        // Arrange
        String csvContent = "Red,50,10\nBlue,30,20\nGreen,40,30";
        MultipartFile file = new MockMultipartFile("file.csv", "file.csv", "text/csv", csvContent.getBytes());

        // Act
        List<StockDto> stockDtoList = csvParserService.parseCsvFile(file);

        // Assert
        assertNotNull(stockDtoList);
        assertEquals(3, stockDtoList.size());

        StockDto stockDto1 = stockDtoList.get(0);
        assertEquals("Red", stockDto1.getSockDto().getColor());
        assertEquals(50, stockDto1.getSockDto().getCottonPercentage());
        assertEquals(10, stockDto1.getQuantity());

        StockDto stockDto2 = stockDtoList.get(1);
        assertEquals("Blue", stockDto2.getSockDto().getColor());
        assertEquals(30, stockDto2.getSockDto().getCottonPercentage());
        assertEquals(20, stockDto2.getQuantity());

        StockDto stockDto3 = stockDtoList.get(2);
        assertEquals("Green", stockDto3.getSockDto().getColor());
        assertEquals(40, stockDto3.getSockDto().getCottonPercentage());
        assertEquals(30, stockDto3.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenCsvHasIncorrectColumnCount() {
        // Arrange
        String csvContent = "Red,50,Blue,30,20,Extra";
        MultipartFile file = new MockMultipartFile("file.csv", "file.csv", "text/csv", csvContent.getBytes());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parseCsvFile(file);
        });

        assertTrue(exception.getMessage().contains("Ожидалось 3 столбца, но получено"));
    }

    @Test
    void shouldThrowExceptionWhenCsvHasNonNumericData() {
        // Arrange
        String csvContent = "Red,abc,10";
        MultipartFile file = new MockMultipartFile("file.csv", "file.csv", "text/csv", csvContent.getBytes());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parseCsvFile(file);
        });

        assertTrue(exception.getMessage().contains("Позиция : Red,abc,10"));
    }

}