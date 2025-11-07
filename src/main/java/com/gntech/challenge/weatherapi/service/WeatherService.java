package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import com.gntech.challenge.weatherapi.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<WeatherDTO> getAllWeather(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return weatherRepository.findAll(pageable).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<WeatherDTO> getWeatherByCity(String city) {
        return weatherRepository.findByCityIgnoreCase(city.trim()).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public WeatherDTO  getLatestWeatherByCityOrThrow(String city) {
        return weatherRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city.trim())
                .map(this::mapToDTO)
                .orElseThrow(() -> new WeatherException(
                        "Nenhum registro encontrado para a cidade " + city,
                        HttpStatus.NOT_FOUND
                ));
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

    private WeatherDTO mapToDTO(WeatherEntity entity) {
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

    private String formatCityName(String city) {
        String trimmed = city.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}