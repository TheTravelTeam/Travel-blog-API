package com.wcs.travel_blog.geocoding.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeocodingConfig {

    @Bean(name = "nominatimRestTemplate")
    public RestTemplate nominatimRestTemplate(RestTemplateBuilder builder, NominatimProperties properties) {
        return builder
            .rootUri(properties.getBaseUrl())
            .setConnectTimeout(properties.getConnectTimeout())
            .setReadTimeout(properties.getReadTimeout())
            .build();
    }
}
