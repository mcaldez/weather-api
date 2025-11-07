package com.gntech.challenge.weatherapi.repository;

import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long>{
}
