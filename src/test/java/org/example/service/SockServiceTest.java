package org.example.service;

import org.example.dto.SockDto;
import org.example.dto.UpdateSockDto;
import org.example.entity.SockEntity;
import org.example.exception.EntityAlreadyExistsException;
import org.example.exception.NotFoundProductException;
import org.example.repositories.SockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @Mock
    private SockRepository sockRepository;

    @InjectMocks
    private SockService sockService;


    @Test
    void updateSockEntity_ShouldThrowEntityAlreadyExistsException_WhenDuplicateFound() {
        Long sockId = 1L;
        UpdateSockDto updateSockDto = new UpdateSockDto("Red", 50);
        SockEntity duplicateSockEntity = new SockEntity();
        duplicateSockEntity.setId(2L); // другой ID, но тот же цвет и процент хлопка
        duplicateSockEntity.setColor("Red");
        duplicateSockEntity.setCottonPercentage(50);

        when(sockRepository.findByColorAndCottonPercentage("Red", 50))
                .thenReturn(Optional.of(duplicateSockEntity));

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () ->
                sockService.updateSockEntity(sockId, updateSockDto));
        assertEquals(duplicateSockEntity.toString(), exception.getMessage());

    }

    // Id товара при обновлении не найден
    @Test
    void updateSockEntity_ShouldThrowNotFoundProductException_WhenSockNotFound() {
        Long sockId = 1L;
        UpdateSockDto updateSockDto = new UpdateSockDto("Blue", 30);

        when(sockRepository.findById(sockId)).thenReturn(Optional.empty());

        NotFoundProductException exception = assertThrows(NotFoundProductException.class, () ->
                sockService.updateSockEntity(sockId, updateSockDto));
        assertTrue(exception.getMessage().contains("с id: " + sockId));
    }

    // Тест обновление товара успех
    @Test
    void updateSockEntity_ShouldUpdateSock_WhenSockExistsAndNoDuplicate() {
        // Arrange
        Long sockId = 1L;
        UpdateSockDto updateSockDto = new UpdateSockDto("Blue", 30);
        SockEntity existingSockEntity = new SockEntity();
        existingSockEntity.setId(sockId);
        existingSockEntity.setColor("Red");
        existingSockEntity.setCottonPercentage(50);

        when(sockRepository.findById(sockId)).thenReturn(Optional.of(existingSockEntity));
        when(sockRepository.findByColorAndCottonPercentage("Blue", 30))
                .thenReturn(Optional.empty());
        when(sockRepository.save(any(SockEntity.class))).thenAnswer(in -> in.getArgument(0));

        sockService.updateSockEntity(sockId, updateSockDto);

        verify(sockRepository).save(argThat(savedSock ->
                "Blue".equals(savedSock.getColor()) &&
                        savedSock.getCottonPercentage() == 30));
    }

    @Test
    void testGetSockEntity_Success() {

        SockDto sockDto = new SockDto();
        sockDto.setColor("Red");
        sockDto.setCottonPercentage(50);
        SockEntity sockEntity = new SockEntity();
        sockEntity.setColor("Red");
        sockEntity.setCottonPercentage(50);

        // Мокаем вызов метода репозитория
        when(sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                .thenReturn(Optional.of(sockEntity)); // Если товар найден, подставляем сущность


        SockEntity result = sockService.getSockEntity(sockDto);

        assertNotNull(result, "Сущность не должна быть null");
        assertEquals("Red", result.getColor(), "Цвет должен быть 'Red'");
        assertEquals(50, result.getCottonPercentage(), "Процент хлопка должен быть 50");

        // Проверяем, что метод репозитория был вызван с правильными параметрами
        verify(sockRepository).findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());
    }

    @Test
    void testGetSockEntity_NotFound() {
        // Создаем DTO для поиска
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(100);

        // Возвращаем пустой Optional
        when(sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                .thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение, если товар не найден
        NotFoundProductException exception = assertThrows(NotFoundProductException.class, () -> {
            sockService.getSockEntity(sockDto);
        });

        // Проверяем сообщение исключения
        assertEquals("цвет - Blue, содержание хлопка - 100%", exception.getMessage());
    }

    @Test
    void testGetSockEntityAndCreate_whenSockFound_shouldReturnExistingSock() {

        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(50);

        SockEntity existingSockEntity = new SockEntity();
        existingSockEntity.setColor("Blue");
        existingSockEntity.setCottonPercentage(50);

        when(sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                .thenReturn(Optional.of(existingSockEntity));

        SockEntity result = sockService.getSockEntityAndCreate(sockDto);

        // Assert
        assertNotNull(result); // Проверяем, что результат не null
        assertEquals(existingSockEntity.getId(), result.getId()); // Проверяем, что возвращен тот же объект
        assertEquals(sockDto.getColor(), result.getColor()); // Проверяем цвет
        assertEquals(sockDto.getCottonPercentage(), result.getCottonPercentage()); // Проверяем содержание хлопка

        // Проверяем, что метод save НЕ был вызван, так как товар был найден
        verify(sockRepository, never()).save(any(SockEntity.class));
    }

    @Test
    void testGetSockEntityAndCreate_whenSockNotFound_shouldCreateNewSock() {
        SockDto sockDto = new SockDto();
        sockDto.setColor("Blue");
        sockDto.setCottonPercentage(50);

        // Мокируем поведение репозитория: товар не найден
        when(sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                .thenReturn(Optional.empty());

        // Мокируем поведение репозитория на создание и сохранение нового товара
        SockEntity newSockEntity = new SockEntity(1L, sockDto.getColor(), sockDto.getCottonPercentage());
        when(sockRepository.save(any(SockEntity.class))).thenReturn(newSockEntity);

        SockEntity result = sockService.getSockEntityAndCreate(sockDto);

        assertNotNull(result); // Проверяем, что товар был создан
        assertEquals(sockDto.getColor(), result.getColor()); // Проверяем цвет
        assertEquals(sockDto.getCottonPercentage(), result.getCottonPercentage()); // Проверяем содержание хлопка

        // Проверяем, что репозиторий был вызван с нужными параметрами
        verify(sockRepository).findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());
        verify(sockRepository).save(any(SockEntity.class)); // Проверяем, что метод save был вызван
    }

}