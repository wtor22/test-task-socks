package org.example.service;

import org.example.dto.SockDto;
import org.example.dto.StockDto;
import org.example.entity.SockEntity;
import org.example.entity.StockEntity;
import org.example.exception.NotFoundProductException;
import org.example.repositories.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SockService sockService;

    @InjectMocks
    private StockService stockService;

    @Test
    void shouldAddNewStockWhenNotExists() {
        // Arrange
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(50);
        StockDto stockDto = new StockDto();
        stockDto.setSockDto(sockDto);
        stockDto.setQuantity(10);

        SockEntity sockEntity = new SockEntity();
        sockEntity.setId(1L);
        sockEntity.setColor("Blue");
        sockEntity.setCottonPercentage(50);
        StockEntity newStockEntity = new StockEntity();
        newStockEntity.setSock(sockEntity);
        newStockEntity.setQuantity(10);

        when(sockService.getSockEntityAndCreate(sockDto)).thenReturn(sockEntity);
        when(stockRepository.findBySock(sockEntity)).thenReturn(Optional.empty());

        stockService.addedStock(List.of(stockDto));

        verify(stockRepository).save(argThat(stockEntity ->
                stockEntity.getSock().equals(sockEntity) &&
                        stockEntity.getQuantity() == 10
        ));
    }


    @Test
    void shouldCallSockServiceAndRepository() {
        // Arrange
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(50);
        StockDto stockDto = new StockDto();
        stockDto.setSockDto(sockDto);
        stockDto.setQuantity(300);

        SockEntity sockEntity = new SockEntity();
        sockEntity.setId(1L);
        sockEntity.setColor("Blue");
        sockEntity.setCottonPercentage(50);

        when(sockService.getSockEntityAndCreate(sockDto)).thenReturn(sockEntity);
        when(stockRepository.findBySock(sockEntity)).thenReturn(Optional.empty());

        stockService.addedStock(List.of(stockDto));

        verify(sockService).getSockEntityAndCreate(sockDto);
        verify(stockRepository).findBySock(sockEntity);
    }

    @Test
    void shouldThrowNotFoundProductExceptionIfStockDoesNotExist() {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(50);

        StockDto stockDto = new StockDto();
        stockDto.setSockDto(sockDto);
        stockDto.setQuantity(50);

        SockEntity sockEntity = new SockEntity();
        sockEntity.setId(1L);
        sockEntity.setColor("Blue");
        sockEntity.setCottonPercentage(50);

        when(sockService.getSockEntity(sockDto)).thenReturn(sockEntity);
        when(stockRepository.findBySock(sockEntity)).thenReturn(Optional.empty());

        NotFoundProductException exception = assertThrows(
                NotFoundProductException.class,
                () -> stockService.removeStock(List.of(stockDto))
        );
        assertTrue(exception.getMessage().contains("с полями Blue/50"));
    }



}