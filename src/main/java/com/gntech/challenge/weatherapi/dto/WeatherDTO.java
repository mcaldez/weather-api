package com.gntech.challenge.weatherapi.dto;

import java.time.LocalDateTime;

public class WeatherDTO {
    private String city;
    private String country;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String description;
    private LocalDateTime dateTime;

    public WeatherDTO(String city, String country, Double temperature, Double humidity,
                      Double windSpeed, String description, LocalDateTime dateTime) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.dateTime = dateTime;
    }

    public String getCity() { return city; }
    public String getCountry() { return country; }
    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
    public Double getWindSpeed() { return windSpeed; }
    public String getDescription() { return description; }
    public LocalDateTime getDateTime() { return dateTime; }
}
