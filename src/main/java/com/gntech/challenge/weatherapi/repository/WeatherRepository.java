package com.gntech.challenge.weatherapi.repository;

import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long>{
    List<WeatherEntity> findByCityIgnoreCase(String city);
    Optional<WeatherEntity> findFirstByCityIgnoreCaseOrderByTimestampDesc(String city);
}
