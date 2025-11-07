package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.entity.WeatherEntity;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import com.gntech.challenge.weatherapi.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {
    @Mock
    private WeatherClient weatherClient;

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getWeather_shouldReturnWeatherDTO_andPersistData() {
        String city = "Florianópolis";
        WeatherDTO mockDto = new WeatherDTO(city, "BR", 25.0, 80.0, 5.0, "clear sky", LocalDateTime.now());

        when(weatherClient.getWeather("Florianópolis")).thenReturn(mockDto);
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WeatherDTO result = weatherService.getWeather(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
        assertEquals(mockDto.getTemperature(), result.getTemperature());

        verify(weatherRepository, times(1)).save(any(WeatherEntity.class));
        verify(weatherClient, times(1)).getWeather("Florianópolis");
    }

    @Test
    void getAllWeather_shouldReturnListOfWeatherDTO() {
        WeatherEntity entity1 = new WeatherEntity("Florianópolis", "BR", 25.0, 80.0, 5.0, "clear sky", LocalDateTime.now());
        WeatherEntity entity2 = new WeatherEntity("São Paulo", "BR", 28.0, 70.0, 3.0, "sunny", LocalDateTime.now());

        Page<WeatherEntity> pageResult = new PageImpl<>(List.of(entity1, entity2));

        when(weatherRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        List<WeatherDTO> result = weatherService.getAllWeather(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Florianópolis", result.get(0).getCity());
        assertEquals("São Paulo", result.get(1).getCity());

        verify(weatherRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getWeatherByCity_shouldReturnListOfWeatherDTO() {
        WeatherEntity entity1 = new WeatherEntity("Florianópolis", "BR", 25.0, 80.0, 5.0, "clear sky", LocalDateTime.now());
        WeatherEntity entity2 = new WeatherEntity("Florianópolis", "BR", 26.0, 75.0, 4.0, "partly cloudy", LocalDateTime.now());

        when(weatherRepository.findByCityIgnoreCase("Florianópolis")).thenReturn(List.of(entity1, entity2));

        List<WeatherDTO> result = weatherService.getWeatherByCity("Florianópolis");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Florianópolis", result.get(0).getCity());
        assertEquals("clear sky", result.get(0).getDescription());
        assertEquals("partly cloudy", result.get(1).getDescription());

        verify(weatherRepository, times(1)).findByCityIgnoreCase("Florianópolis");
    }

    @Test
    void getLatestWeatherByCityOrThrow_shouldReturnWeatherDTO_whenCityExists() {
        WeatherEntity entity = new WeatherEntity(
                "Florianópolis", "BR", 25.0, 80.0, 5.0, "clear sky", LocalDateTime.now()
        );

        when(weatherRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc("Florianópolis"))
                .thenReturn(Optional.of(entity));

        WeatherDTO result = weatherService.getLatestWeatherByCityOrThrow("Florianópolis");

        assertNotNull(result);
        assertEquals("Florianópolis", result.getCity());
        assertEquals("clear sky", result.getDescription());

        verify(weatherRepository, times(1))
                .findFirstByCityIgnoreCaseOrderByTimestampDesc("Florianópolis");
    }

    @Test
    void getLatestWeatherByCityOrThrow_shouldThrowWeatherException_whenCityDoesNotExist() {
        when(weatherRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc("CidadeInexistente"))
                .thenReturn(Optional.empty());

        WeatherException exception = assertThrows(WeatherException.class,
                () -> weatherService.getLatestWeatherByCityOrThrow("CidadeInexistente"));

        assertEquals("Nenhum registro encontrado para a cidade CidadeInexistente", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(weatherRepository, times(1))
                .findFirstByCityIgnoreCaseOrderByTimestampDesc("CidadeInexistente");
    }
}

