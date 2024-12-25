package org.example.service;

import org.example.dto.StockDto;
import org.example.dto.SockDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CsvParserService {

    public List<StockDto> parseCsvFile(MultipartFile file) throws Exception {

        if (!isCsvFile(file)) {
            throw new MultipartException("Файл: " + file.getOriginalFilename() + " имеет неподдерживаемый формат ");
        }

        List<StockDto> stockDtoList = new ArrayList<>();

        // Делаю парсинг CSV файла
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                StockDto stockDto = getSockDto(line);
                stockDtoList.add(stockDto);
            }
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (IOException e) {
            throw new IOException("Ошибка чтения файла: " + file.getOriginalFilename(), e);
        }
        return stockDtoList;
    }

    private static StockDto getSockDto(String line) {
        String[] columns = line.split(",");
        if (columns.length != 3) {
            throw new IllegalArgumentException(" Ожидалось 3 столбца, но получено " + columns.length);
        }
        try {
            SockDto sockDto = new SockDto();
            sockDto.setColor(columns[0]);
            sockDto.setCottonPercentage(Integer.parseInt(columns[1])); // Процент хлопка
            int quantity = Integer.parseInt(columns[2]); // Количество носок

            StockDto stockDto = new StockDto();
            stockDto.setSockDto(sockDto);
            stockDto.setQuantity(quantity);
            return stockDto;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Позиция : " + line);
        }
    }

    public boolean isCsvFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (!contentType.equals("text/csv") && !contentType.equals("application/vnd.ms-excel")) {
            return  false;
        }
        String filename = file.getOriginalFilename();
        return filename != null && filename.endsWith(".csv");
    }
}
