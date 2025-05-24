package com.example.telegram.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;

    public WeatherService(RestTemplateBuilder builder,
                          @Value("${openweather.api-key}") String apiKey) {
        this.restTemplate = builder.build();
        this.apiKey = apiKey;
    }

    public String getTwoDayForecast(String city) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q={city}&cnt=16&appid={key}&units=metric&lang=ru";
        String json = restTemplate.getForObject(url, String.class, city, apiKey);
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode list = root.get("list");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size() && i < 16; i += 8) {
                JsonNode item = list.get(i);
                String date = item.get("dt_txt").asText();
                String desc = item.get("weather").get(0).get("description").asText();
                double temp = item.get("main").get("temp").asDouble();
                sb.append(date).append(": ")
                  .append(desc).append(", ")
                  .append(String.format("%.1f", temp)).append("°C\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Не удалось получить данные о погоде";
        }
    }
}
