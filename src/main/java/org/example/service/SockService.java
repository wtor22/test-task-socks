package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.SockDto;
import org.example.dto.UpdateSockDto;
import org.example.entity.SockEntity;
import org.example.exception.EntityAlreadyExistsException;
import org.example.exception.NotFoundProductException;
import org.example.repositories.SockRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SockService {

    private final SockRepository sockRepositories;

    // Обновляю товар
    public void updateSockEntity(Long id, UpdateSockDto sock) {

        // Ищу возможное совпадение по полям с другими носками
        Optional<SockEntity> doubleSock = sockRepositories.findByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage());
        if(doubleSock.isPresent()) {
            throw new EntityAlreadyExistsException(doubleSock.get().toString());
        }

        Optional<SockEntity> optionalSockEntity =
                sockRepositories.findById(id);

        if (optionalSockEntity.isEmpty()) {
            throw  new NotFoundProductException(" с id: " + id );
        }

        SockEntity existingdSockEntity = optionalSockEntity.get();
        log.info("Товар найден в БД: {}", existingdSockEntity);

        existingdSockEntity.setColor(sock.getColor());
        existingdSockEntity.setCottonPercentage(sock.getCottonPercentage());

        SockEntity updatedSockEntity = sockRepositories.save(existingdSockEntity);
        log.info("Товар c id {} с новыми параметрами {} сохранен успешно",id, updatedSockEntity);
    }

    // Проверяю есть ли товар
    public SockEntity getSockEntity(SockDto sockDto) {

        Optional<SockEntity> optionalSockEntity =
                sockRepositories.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());
        if (optionalSockEntity.isEmpty()) {
            log.info("Товар: {} НЕ найден в БД. ", sockDto);
            throw new NotFoundProductException("цвет - " + sockDto.getColor() + ", содержание хлопка - " + sockDto.getCottonPercentage() + "%");
        }
        log.info("Товар: {} найден в БД", sockDto.getColor());
        return optionalSockEntity.get();
    }


    // Получаю товар, если отсутствует то создаю
    public SockEntity getSockEntityAndCreate(SockDto sockDto) {

        Optional<SockEntity> optionalSockEntity =
                sockRepositories.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());
        if (optionalSockEntity.isEmpty()) {
            log.info("Товар: {} НЕ найден в БД. ", sockDto);
            SockEntity sockEntity = sockRepositories.save(SockService.mapToEntity(sockDto));
            log.info("Товар: {} создан в БД. ", sockEntity);
            return sockEntity;
        }
        log.info("Товар: {} найден в БД", sockDto);

        return optionalSockEntity.get();
    }

    public int getSocksCount(String color, String operation, int cottonPercentage) {
        return switch (operation) {
            case "moreThan" -> sockRepositories.countByColorAndCottonPercentageGreaterThan(color, cottonPercentage);
            case "lessThan" -> sockRepositories.countByColorAndCottonPercentageLessThan(color, cottonPercentage);
            case "equal" -> sockRepositories.countByColorAndCottonPercentage(color, cottonPercentage);
            default ->
                    throw new IllegalArgumentException("Некорректный оператор сравнения. Допустимые значения: moreThan, lessThan, equal.");
        };
    }

    public static SockEntity mapToEntity(SockDto sockDto) {

        SockEntity sock = new SockEntity();
        sock.setId(sockDto.getId());
        sock.setColor(sockDto.getColor());
        sock.setCottonPercentage(sockDto.getCottonPercentage());

        return sock;
    }
}
