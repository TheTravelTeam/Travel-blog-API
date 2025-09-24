package com.wcs.travel_blog.geocoding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nominatim")
public class NominatimProperties {

    private String baseUrl = "https://nominatim.openstreetmap.org";

    private String format = "jsonv2";

    private boolean addressDetails = true;

    private int limit = 5;

    private Duration connectTimeout = Duration.ofSeconds(2);

    private Duration readTimeout = Duration.ofSeconds(5);

    private String userAgent;

    private String acceptLanguage = "fr";
}
