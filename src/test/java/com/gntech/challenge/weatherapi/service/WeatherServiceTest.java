package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import com.gntech.challenge.weatherapi.mapper.WeatherMapper;
import com.gntech.challenge.weatherapi.repository.WeatherRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {
    @Mock private WeatherClient weatherClient;
    @Mock private WeatherRepository weatherRepository;
    @Mock private WeatherMapper mapper;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService(weatherClient, weatherRepository, mapper, "dummy-api-key");
    }

    @Test
    void getWeather_ShouldReturnWeatherDTO_WhenApiResponds() {
        String city = "Florianopolis";
        OpenWeatherResponse response = new OpenWeatherResponse();
        WeatherDTO dto = new WeatherDTO(city, "BR", 25.0, 70.0, 5.0, "Sunny", null);

        when(weatherClient.getWeather(city, "dummy-api-key", "metric")).thenReturn(response);
        when(mapper.fromResponse(response)).thenReturn(dto);

        WeatherDTO result = weatherService.fetchWeatherFromApi(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
    }

    @Test
    void fetchWeatherFromApi_ShouldThrowWeatherException_OnFeignException() {
        String city = "Florianopolis";
        when(weatherClient.getWeather(anyString(), anyString(), anyString()))
                .thenThrow(FeignException.FeignClientException.class);

        assertThrows(WeatherException.class, () -> weatherService.fetchWeatherFromApi(city));
    }


    @Test
    void getLatestWeatherByCityOrThrow_ShouldThrow_WhenNoData() {
        String city = "CidadeInexistente";
        when(weatherRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city.trim())).thenReturn(Optional.empty());

        assertThrows(WeatherException.class, () -> weatherService.getLatestWeatherByCityOrThrow(city));
    }

    @Test
    void getWeather_ShouldFetchAndPersistWeather() {
        String city = "Florianopolis";
        WeatherDTO dto = new WeatherDTO(city, "BR", 25.0, 70.0, 5.0, "Sunny", null);
        OpenWeatherResponse response = new OpenWeatherResponse();

        when(weatherClient.getWeather(anyString(), anyString(), anyString())).thenReturn(response);
        when(mapper.fromResponse(response)).thenReturn(dto);
        when(mapper.toEntity(dto)).thenReturn(new WeatherEntity(city, "BR", 25.0, 70.0, 5.0, "Sunny", null));

        WeatherDTO result = weatherService.getWeather(city);

        assertNotNull(result);
        assertEquals("Florianopolis", result.getCity());
        verify(weatherRepository, times(1)).save(any(WeatherEntity.class));
    }

    @Test
    void persistWeatherData_ShouldThrowWeatherException_OnSaveFailure() {
        WeatherDTO dto = new WeatherDTO("Florianopolis", "BR", 25.0, 70.0, 5.0, "Sunny", null);
        when(mapper.toEntity(dto)).thenReturn(new WeatherEntity(dto.getCity(), dto.getCountry(), dto.getTemperature(),
                dto.getHumidity(), dto.getWindSpeed(), dto.getDescription(), null));
        doThrow(new RuntimeException("DB error")).when(weatherRepository).save(any());

        WeatherException ex = assertThrows(WeatherException.class, () -> {
            ReflectionTestUtils.invokeMethod(weatherService, "persistWeatherData", dto);
        });

        assertTrue(ex.getMessage().contains("Erro ao persistir dados de clima"));
    }

    @Test
    void getAllWeather_ShouldReturnPagedWeatherDTOs() {
        WeatherEntity entity1 = new WeatherEntity("City1", "BR", 20.0, 50.0, 3.0, "Cloudy", null);
        WeatherEntity entity2 = new WeatherEntity("City2", "BR", 22.0, 55.0, 4.0, "Sunny", null);
        List<WeatherEntity> entities = List.of(entity1, entity2);
        Page<WeatherEntity> page = new PageImpl<>(entities);

        when(weatherRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDTO(entity1)).thenReturn(new WeatherDTO("City1", "BR", 20.0, 50.0, 3.0, "Cloudy", null));
        when(mapper.toDTO(entity2)).thenReturn(new WeatherDTO("City2", "BR", 22.0, 55.0, 4.0, "Sunny", null));

        List<WeatherDTO> result = weatherService.getAllWeather(0, 10);

        assertEquals(2, result.size());
        assertEquals("City1", result.get(0).getCity());
        assertEquals("City2", result.get(1).getCity());
    }

    @Test
    void getWeatherByCity_ShouldReturnWeatherDTOs() {
        String city = "Florianopolis";
        WeatherEntity entity = new WeatherEntity(city, "BR", 25.0, 70.0, 5.0, "Sunny", null);
        WeatherDTO dto = new WeatherDTO(city, "BR", 25.0, 70.0, 5.0, "Sunny", null);

        when(weatherRepository.findByCityIgnoreCase(city.trim())).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        List<WeatherDTO> result = weatherService.getWeatherByCity(city);

        assertEquals(1, result.size());
        assertEquals(city, result.get(0).getCity());
    }

    @Test
    void formatCityName_ShouldThrow_WhenNullOrEmpty() {
        assertThrows(WeatherException.class, () -> {
            ReflectionTestUtils.invokeMethod(weatherService, "formatCityName", (String) null);
        });

        assertThrows(WeatherException.class, () -> {
            ReflectionTestUtils.invokeMethod(weatherService, "formatCityName", "   ");
        });
    }
}