package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse.Main;
import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse.Sys;
import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse.Wind;
import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse.Weather;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherClientImplTest {

    private WebClient.ResponseSpec responseSpec;

    private WeatherClientImpl weatherClient;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestUriSpec);
        when(requestUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        weatherClient = new WeatherClientImpl(webClientBuilder, "fakeApiKey", "http://fake.url");
    }

    @Test
    void getWeather_shouldReturnWeatherDTO_whenResponseIsValid() {
        OpenWeatherResponse openWeather = new OpenWeatherResponse();
        openWeather.name = "Florianópolis";
        openWeather.sys = new Sys();
        openWeather.sys.country = "BR";
        openWeather.main = new Main();
        openWeather.main.temp = 25.0;
        openWeather.main.humidity = 80.0;
        openWeather.wind = new Wind();
        openWeather.wind.speed = 5.0;
        openWeather.weather = new Weather[]{ new Weather() };
        openWeather.weather[0].description = "clear sky";

        when(responseSpec.bodyToMono(OpenWeatherResponse.class)).thenReturn(Mono.just(openWeather));

        WeatherDTO dto = weatherClient.getWeather("Florianópolis");

        assertNotNull(dto);
        assertEquals("Florianópolis", dto.getCity());
        assertEquals("BR", dto.getCountry());
        assertEquals(25.0, dto.getTemperature());
        assertEquals(80.0, dto.getHumidity());
        assertEquals(5.0, dto.getWindSpeed());
        assertEquals("clear sky", dto.getDescription());
    }

    @Test
    void toWeatherDTO_shouldMapAllFieldsCorrectly() throws Exception {
        WeatherClientImpl client = new WeatherClientImpl(mock(WebClient.Builder.class), "key", "url");

        OpenWeatherResponse response = new OpenWeatherResponse();
        response.name = "Florianópolis";
        response.sys = new OpenWeatherResponse.Sys();
        response.sys.country = "BR";
        response.main = new OpenWeatherResponse.Main();
        response.main.temp = 25.0;
        response.main.humidity = 80.0;
        response.wind = new OpenWeatherResponse.Wind();
        response.wind.speed = 5.0;
        response.weather = new OpenWeatherResponse.Weather[]{ new OpenWeatherResponse.Weather() };
        response.weather[0].description = "clear sky";

        Method method = WeatherClientImpl.class.getDeclaredMethod("toWeatherDTO", OpenWeatherResponse.class);
        method.setAccessible(true);

        WeatherDTO dto = (WeatherDTO) method.invoke(client, response);

        assertNotNull(dto);
        assertEquals("Florianópolis", dto.getCity());
        assertEquals("BR", dto.getCountry());
        assertEquals(25.0, dto.getTemperature());
        assertEquals(80.0, dto.getHumidity());
        assertEquals(5.0, dto.getWindSpeed());
        assertEquals("clear sky", dto.getDescription());
    }

    @Test
    void getWeather_shouldThrowWeatherException_whenResponseIsNull() {
        Mono emptyMono = mock(Mono.class);
        when(emptyMono.block()).thenReturn(null);

        when(responseSpec.bodyToMono(OpenWeatherResponse.class)).thenReturn(emptyMono);

        WeatherException ex = assertThrows(WeatherException.class, () -> weatherClient.getWeather("Florianópolis"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Resposta vazia"));

        verify(responseSpec).bodyToMono(OpenWeatherResponse.class);
    }

    @Test
    void getWeather_shouldThrowWeatherException_whenCityNotFound() {
        when(responseSpec.bodyToMono(OpenWeatherResponse.class)).thenThrow(new RuntimeException("404"));

        WeatherException ex = assertThrows(WeatherException.class, () -> weatherClient.getWeather("CidadeInexistente"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Cidade não encontrada"));
    }

    @Test
    void getWeather_shouldThrowWeatherException_whenConnectionError() {
        WebClientRequestException wcre = new WebClientRequestException(
                new RuntimeException("connection failed"),
                org.springframework.http.HttpMethod.GET,
                java.net.URI.create("http://localhost"),
                org.springframework.http.HttpHeaders.EMPTY
        );

        when(responseSpec.bodyToMono(OpenWeatherResponse.class)).thenThrow(wcre);

        WeatherException ex = assertThrows(WeatherException.class, () -> weatherClient.getWeather("Florianópolis"));
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Erro de conexão"));
    }

    @Test
    void getWeather_shouldThrowIllegalStateException_whenApiKeyIsMissing() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WeatherClientImpl client = new WeatherClientImpl(builder, "", "http://fake.url");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> client.getWeather("Florianópolis"));
        assertEquals("OPENWEATHER_API_KEY não configurada", ex.getMessage());
    }

    @Test
    void getWeather_shouldRethrowWeatherException_ifAlreadyWeatherException() {
        when(responseSpec.bodyToMono(OpenWeatherResponse.class))
                .thenThrow(new WeatherException("teste", HttpStatus.BAD_GATEWAY));

        WeatherException ex = assertThrows(WeatherException.class, () -> weatherClient.getWeather("Florianópolis"));
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
        assertEquals("teste", ex.getMessage());
    }

    @Test
    void getWeather_shouldThrowWeatherException_withBadGateway_whenOtherException() {
        when(responseSpec.bodyToMono(OpenWeatherResponse.class))
                .thenThrow(new RuntimeException("Some server error"));

        WeatherException ex = assertThrows(WeatherException.class, () -> weatherClient.getWeather("Florianópolis"));
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Erro ao consultar OpenWeather"));
    }

}