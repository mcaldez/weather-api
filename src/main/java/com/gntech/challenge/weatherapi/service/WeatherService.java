package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import com.gntech.challenge.weatherapi.mapper.WeatherMapper;
import com.gntech.challenge.weatherapi.repository.WeatherRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
@Slf4j
public class WeatherService {

    private final WeatherClient weatherClient;
    private final WeatherRepository weatherRepository;
    private final WeatherMapper mapper;
    private final String apiKey;


    public WeatherService(WeatherClient weatherClient, WeatherRepository weatherRepository, WeatherMapper mapper, @Value("${openweather.api.key}") String apiKey) {
        this.weatherClient = weatherClient;
        this.weatherRepository = weatherRepository;
        this.mapper = mapper;
        this.apiKey = apiKey;
    }

    public WeatherDTO getWeather(String city) {
        String formattedCity = formatCityName(city);
        log.info("[WeatherService:getWeather] Buscando dados de clima para '{}'", formattedCity);

        WeatherDTO weatherDTO = fetchWeatherFromApi(formattedCity);
        persistWeatherData(weatherDTO);

        return weatherDTO;
    }

    public WeatherDTO fetchWeatherFromApi(String city) {
        try {
            OpenWeatherResponse response = weatherClient.getWeather(city, apiKey, "metric");
            if (response == null) {
                throw new WeatherException("Resposta vazia do OpenWeather", HttpStatus.BAD_GATEWAY);
            }
            return mapper.fromResponse(response);
        } catch (FeignException e) {
            throw new WeatherException("Falha ao consultar a API OpenWeather", HttpStatus.BAD_GATEWAY);
        }
    }

    private void persistWeatherData(WeatherDTO dto) {
        try {
            WeatherEntity entity = mapper.toEntity(dto);
            weatherRepository.save(entity);
            log.info("[WeatherService:persistWeatherData] Dados de clima salvos no banco para '{}'", entity.getCity());
        } catch (Exception e) {
            log.error("[WeatherService:persistWeatherData] Falha ao salvar dados de clima para '{}': {}", dto.getCity(), e.getMessage(), e);
            throw new WeatherException(
                    "Erro ao persistir dados de clima para " + dto.getCity(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public List<WeatherDTO> getAllWeather(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return weatherRepository.findAll(pageable).stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<WeatherDTO> getWeatherByCity(String city) {
        String formattedCity = formatCityName(city);
        return weatherRepository.findByCityIgnoreCase(city.trim()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    public WeatherDTO getLatestWeatherByCityOrThrow(String city) {
        return weatherRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city.trim())
                .map(mapper::toDTO)
                .orElseThrow(() -> new WeatherException(
                        "Nenhum registro encontrado para a cidade " + city,
                        HttpStatus.NOT_FOUND
                ));
    }

    private String formatCityName(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new WeatherException("O nome da cidade não pode ser nulo ou vazio", HttpStatus.BAD_REQUEST);
        }
        String trimmed = city.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}