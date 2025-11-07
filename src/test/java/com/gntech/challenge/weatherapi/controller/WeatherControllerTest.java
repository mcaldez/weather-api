package com.gntech.challenge.weatherapi.controller;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }
    WeatherDTO dto1 = new WeatherDTO("Florianópolis", "BR", 25.0, 80.0, 5.0, "clear sky", LocalDateTime.now());
    WeatherDTO dto2 = new WeatherDTO("São Paulo", "BR", 28.0, 70.0, 3.5, "partly cloudy", LocalDateTime.now());

    @Test
    void getCurrentWeather_shouldReturnWeatherDTO() throws Exception {

        when(weatherService.getWeather("Florianópolis")).thenReturn(dto1);

        mockMvc.perform(get("/internal/weather/current")
                        .param("city", "Florianópolis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Florianópolis"))
                .andExpect(jsonPath("$.country").value("BR"))
                .andExpect(jsonPath("$.temperature").value(25.0));

        verify(weatherService, times(1)).getWeather("Florianópolis");
    }

    @Test
    void getAllWeather_shouldReturnPagedWeatherDTOList() throws Exception {

        when(weatherService.getAllWeather(0, 10)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/internal/weather/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Florianópolis"))
                .andExpect(jsonPath("$[1].city").value("São Paulo"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(weatherService, times(1)).getAllWeather(0, 10);
    }

    @Test
    void getWeatherByCity_shouldReturnWeatherDTOList() throws Exception {
        WeatherDTO dto2 = new WeatherDTO("Florianópolis", "BR", 26.0, 78.0, 4.5, "partly cloudy", LocalDateTime.now());

        when(weatherService.getWeatherByCity("Florianópolis")).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/internal/weather/by-city")
                        .param("city", "Florianópolis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Florianópolis"))
                .andExpect(jsonPath("$[1].city").value("Florianópolis"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(weatherService, times(1)).getWeatherByCity("Florianópolis");
    }

    @Test
    void getLatestWeatherByCity_shouldReturnWeatherDTO() throws Exception {
        WeatherDTO latestDto = dto1;

        when(weatherService.getLatestWeatherByCityOrThrow("Florianópolis")).thenReturn(latestDto);

        mockMvc.perform(get("/internal/weather/latest")
                        .param("city", "Florianópolis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Florianópolis"))
                .andExpect(jsonPath("$.temperature").value(25.0));

        verify(weatherService, times(1)).getLatestWeatherByCityOrThrow("Florianópolis");
    }

}
