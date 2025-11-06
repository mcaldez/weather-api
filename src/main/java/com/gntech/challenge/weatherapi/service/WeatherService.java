package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherDTO getWeather(String city) {

        String formattedCity = formatCityName(city);
        log.info("Cidade formatada para requisição: '{}'", formattedCity);

        WeatherDTO weather = weatherClient.getWeather(formattedCity);
        log.info("Dados recebidos do WeatherClient: {}", weather);
        return weather;
    }

    private String formatCityName(String city) {
        String trimmed = city.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}