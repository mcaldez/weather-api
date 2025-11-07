package com.gntech.challenge.weatherapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "weather")
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String country;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String description;

    private LocalDateTime timestamp;

    public WeatherEntity() {}

    public WeatherEntity(String city, String country, Double temperature, Double humidity,
                         Double windSpeed, String description, LocalDateTime timestamp) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.timestamp = timestamp;
    }
}
