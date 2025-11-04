package com.wcs.travel_blog.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ReverseGeocodingService {

    private static final String DEFAULT_USER_AGENT = "TravelBlog/1.0 (reverse-geocoding)";

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String acceptLanguage;
    private final String contactEmail;
    private final String userAgent;

    public ReverseGeocodingService(
            @Value("${nominatim.base-url:https://nominatim.openstreetmap.org}") String baseUrl,
            @Value("${nominatim.accept-language:fr}") String acceptLanguage,
            @Value("${nominatim.contact-email:}") String contactEmail,
            @Value("${nominatim.user-agent:}") String userAgent
    ) {
        this.baseUrl = baseUrl;
        this.acceptLanguage = acceptLanguage;
        this.contactEmail = contactEmail;
        this.userAgent = StringUtils.hasText(userAgent) ? userAgent : DEFAULT_USER_AGENT;
    }

    public ReverseGeocodingResult reverseGeocode(double latitude, double longitude) {
        URI requestUri = buildUri(latitude, longitude);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, userAgent);
        if (StringUtils.hasText(contactEmail)) {
            headers.set(HttpHeaders.FROM, contactEmail);
        }
        headers.set(HttpHeaders.ACCEPT, "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<NominatimResponse> response = restTemplate.exchange(
                requestUri,
                HttpMethod.GET,
                entity,
                NominatimResponse.class
        );

        NominatimResponse payload = response.getBody();
        if (payload == null || payload.getAddress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Impossible de lire la réponse Nominatim");
        }

        NominatimResponse.Address address = payload.getAddress();
        String city = pickFirstValue(
                address.getCity(),
                address.getTown(),
                address.getVillage(),
                address.getMunicipality(),
                address.getCityDistrict(),
                address.getCounty()
        );

        String country = emptyToNull(address.getCountry());
        String continent = emptyToNull(address.getContinent());
        if (!StringUtils.hasText(continent) && StringUtils.hasText(address.getCountryCode())) {
            continent = ContinentLookup.lookup(address.getCountryCode());
        }

        ReverseGeocodingResult result = new ReverseGeocodingResult();
        result.setLatitude(latitude);
        result.setLongitude(longitude);
        result.setCity(city);
        result.setCountry(country);
        result.setContinent(continent);
        return result;
    }

    private URI buildUri(double latitude, double longitude) {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(normalizedBase);
        if (!normalizedBase.endsWith("/reverse")) {
            builder.path("/reverse");
        }

        builder.queryParam("format", "jsonv2");
        builder.queryParam("lat", latitude);
        builder.queryParam("lon", longitude);
        builder.queryParam("addressdetails", 1);
        builder.queryParam("zoom", 10);

        if (StringUtils.hasText(acceptLanguage)) {
            builder.queryParam("accept-language", acceptLanguage);
        }

        return builder.build(true).toUri();
    }

    private String pickFirstValue(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String emptyToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
