package com.gntech.challenge.weatherapi.mapper;


import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WeatherMapper {

    public WeatherDTO fromResponse(OpenWeatherResponse response) {
        return new WeatherDTO(
                response.name,
                response.sys != null ? response.sys.country : null,
                response.main != null ? response.main.temp : null,
                response.main != null ? response.main.humidity : null,
                response.wind != null ? response.wind.speed : null,
                (response.weather != null && response.weather.length > 0)
                        ? response.weather[0].description : null,
                LocalDateTime.now()
        );
    }

    public  WeatherEntity toEntity(WeatherDTO dto) {
        return new WeatherEntity(
                null,
                dto.getCity(),
                dto.getCountry(),
                dto.getTemperature(),
                dto.getHumidity(),
                dto.getWindSpeed(),
                dto.getDescription(),
                dto.getDateTime() != null ? dto.getDateTime() : LocalDateTime.now()
        );
    }

    public WeatherDTO toDTO(WeatherEntity entity) {
        return new WeatherDTO(
                entity.getCity(),
                entity.getCountry(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getWindSpeed(),
                entity.getDescription(),
                entity.getTimestamp()
        );
    }
}