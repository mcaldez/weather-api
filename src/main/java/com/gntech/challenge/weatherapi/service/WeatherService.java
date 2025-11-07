package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import com.gntech.challenge.weatherapi.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@Slf4j
public class WeatherService {

    private final WeatherClient weatherClient;
    private final WeatherRepository weatherRepository;


    public WeatherService(WeatherClient weatherClient, WeatherRepository weatherRepository) {
        this.weatherClient = weatherClient;
        this.weatherRepository = weatherRepository;
    }

    @Transactional
    public WeatherDTO getWeather(String city) {
        String formattedCity = formatCityName(city);
        log.info("Buscando dados de clima para '{}'", formattedCity);

        WeatherDTO weatherDTO = weatherClient.getWeather(formattedCity);
        log.info("Dados recebidos do WeatherClient para '{}': {}", formattedCity, weatherDTO);

        persistWeatherData(weatherDTO);

        return weatherDTO;
    }

    private void persistWeatherData(WeatherDTO weatherDTO) {
        try {
            WeatherEntity entity = mapToEntity(weatherDTO);
            weatherRepository.save(entity);
            log.info("Dados de clima salvos no banco para '{}'", entity.getCity());
        } catch (Exception e) {
            log.error("Falha ao salvar dados de clima no banco para '{}': {}", weatherDTO.getCity(), e.getMessage(), e);
            throw new WeatherException(
                    "Erro ao persistir dados de clima para " + weatherDTO.getCity(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private WeatherEntity mapToEntity(WeatherDTO weatherDTO) {
        return new WeatherEntity(
                weatherDTO.getCity(),
                weatherDTO.getCountry(),
                weatherDTO.getTemperature(),
                weatherDTO.getHumidity(),
                weatherDTO.getWindSpeed(),
                weatherDTO.getDescription(),
                LocalDateTime.now()
        );
    }

    private String formatCityName(String city) {
        String trimmed = city.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}