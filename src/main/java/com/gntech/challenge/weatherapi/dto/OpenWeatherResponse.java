package com.gntech.challenge.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherResponse {

    public Main main;
    public Wind wind;
    public Sys sys;
    public Weather[] weather;
    public String name;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        public Double temp;
        public Double humidity;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        public Double speed;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        public String country;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        public String description;
    }
}
