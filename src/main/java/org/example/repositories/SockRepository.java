package org.example.repositories;

import org.example.entity.SockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SockRepository extends JpaRepository<SockEntity, Long> {

    Optional<SockEntity> findByColorAndCottonPercentage(String color, int cottonPercentage);


    int countByColorAndCottonPercentageGreaterThan(String color, int cottonPercentage);

    int countByColorAndCottonPercentageLessThan(String color, int cottonPercentage);

    int countByColorAndCottonPercentage(String color, int cottonPercentage);
}
