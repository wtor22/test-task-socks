package org.example.repositories;

import org.example.entity.SockEntity;
import org.example.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findBySock(SockEntity sock);

    // Сумма количества носок по цвету и проценту хлопка равно (equal)
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.sock.color = :color AND s.sock.cottonPercentage = :cottonPercentage")
    Integer sumQuantityByColorAndCottonPercentageEqual(@Param("color") String color, @Param("cottonPercentage") Integer cottonPercentage);

    // Сумма количества носок по цвету и проценту хлопка больше чем (moreThan)
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.sock.color = :color AND s.sock.cottonPercentage > :cottonPercentage")
    Integer sumQuantityByColorAndCottonPercentageGreaterThan(@Param("color") String color, @Param("cottonPercentage") Integer cottonPercentage);

    // Сумма количества носок по цвету и проценту хлопка меньше чем (lessThan)
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.sock.color = :color AND s.sock.cottonPercentage < :cottonPercentage")
    Integer sumQuantityByColorAndCottonPercentageLessThan(@Param("color") String color, @Param("cottonPercentage") Integer cottonPercentage);

    // Сумма количества носок по цвету и проценту хлопка в диапазоне (moreThan - lessThan)
    @Query("SELECT SUM(s.quantity) FROM StockEntity s WHERE s.sock.color = :color AND s.sock.cottonPercentage > :cottonPercentageMin AND s.sock.cottonPercentage < :cottonPercentageMax")
    Integer sumQuantityByColorAndCottonPercentageInRange(
            @Param("color") String color,
            @Param("cottonPercentageMin") Integer cottonPercentageMin,
            @Param("cottonPercentageMax") Integer cottonPercentageMax);



}
