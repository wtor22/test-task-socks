package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.StockDto;
import org.example.entity.SockEntity;
import org.example.entity.StockEntity;
import org.example.exception.BusinessLogicException;
import org.example.exception.InsufficientStockException;
import org.example.exception.NotFoundProductException;
import org.example.repositories.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final SockService sockService;

    @Transactional
    public void addedStock(List<StockDto> stockDtoList) {
        for (StockDto stockDto : stockDtoList) {
            SockEntity sockEntity = sockService.getSockEntityAndCreate(stockDto.getSockDto());
            Optional<StockEntity> optionalStockEntity = stockRepository.findBySock(sockEntity);

            if(optionalStockEntity.isEmpty()) {
                StockEntity stockEntity = new StockEntity();
                stockEntity.setSock(sockEntity);
                stockEntity.setQuantity(stockDto.getQuantity());
                stockRepository.save(stockEntity);
                continue;
            }
            StockEntity stockEntity = optionalStockEntity.get();
            int quantity = stockEntity.getQuantity();
            int updatedQuantity = quantity + stockDto.getQuantity();
            stockEntity.setQuantity(updatedQuantity);
            stockRepository.save(stockEntity);
        }
    }

    @Transactional
    public void removeStock(List<StockDto> stockDtoList) {
        log.info("START SERVICE");
        List<StockEntity> updatedStockEntityList = new ArrayList<>();
        for (StockDto stockDto : stockDtoList) {

            SockEntity sockEntity = sockService.getSockEntity(stockDto.getSockDto());

            log.info("GET SOCK ENTITY " + sockEntity);

            Optional<StockEntity> optionalStockEntity = stockRepository.findBySock(sockEntity);
            if(optionalStockEntity.isEmpty()) {
                throw new NotFoundProductException(" с полями " + stockDto.getSockDto().getColor() + "/"
                            + stockDto.getSockDto().getCottonPercentage());
            }
            StockEntity stockEntity = optionalStockEntity.get();
            log.info("Текущее количество товара {} на складе: {}", stockEntity.getSock(), stockEntity.getQuantity() );

            if(stockEntity.getQuantity() < stockDto.getQuantity() ) {
                log.info("stockEntity.getQuantity(): {}, stockDto.getQuantity(): {}", stockEntity.getQuantity(), stockDto.getQuantity());
                throw new InsufficientStockException("товара носки: " + sockEntity.getColor() + "/" +
                        sockEntity.getCottonPercentage() + " недостаточно на складе");
            }

            int updatedQuantity = stockEntity.getQuantity() - stockDto.getQuantity();
            stockEntity.setQuantity(updatedQuantity);
            updatedStockEntityList.add(stockEntity);
            log.info("Новое количество товара {} на складе: {}",stockEntity.getSock(), stockEntity.getQuantity());
        }
        stockRepository.saveAll(updatedStockEntityList);
        log.info("Количество товара успешно обновлено");
    }

    // Фильтрация по цвету и проценту хлопка с диапазоном (больше чем - меньше чем ну и равно)
    // Если все параметры указаны включаю equal то фильтрация по диапазону
    public Integer getSocksQuantity(String color, Integer moreThan, Integer lessThan, Integer equal) {

        // Проверяем условие, что lessThan >= moreThan
        if (lessThan != null && moreThan != null && lessThan <= moreThan) {
            throw new BusinessLogicException("'lessThan' должно быть больше 'moreThan'.");
        }
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("Поле 'color' не должно быть пустым.");
        }

        if (moreThan != null && lessThan != null) {
            // Диапазон: больше чем moreThan и меньше чем lessThan
            return stockRepository.sumQuantityByColorAndCottonPercentageInRange(color, moreThan, lessThan);
        } else if (moreThan != null) {
            // Только больше чем
            return stockRepository.sumQuantityByColorAndCottonPercentageGreaterThan(color, moreThan);
        } else if (lessThan != null) {
            // Только меньше чем
            return stockRepository.sumQuantityByColorAndCottonPercentageLessThan(color, lessThan);
        } else if ( equal != null) {
            // Только равно
            return stockRepository.sumQuantityByColorAndCottonPercentageEqual(color,equal);
        }
        return 0; // Если нет фильтра, возвращаем 0 (Такого быть не должно)
    }


}
