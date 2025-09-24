package com.wcs.travel_blog.geocoding.service;

import com.wcs.travel_blog.exception.ExternalServiceException;
import com.wcs.travel_blog.geocoding.config.NominatimProperties;
import com.wcs.travel_blog.geocoding.dto.LocationSuggestionDTO;
import com.wcs.travel_blog.geocoding.dto.NominatimAddress;
import com.wcs.travel_blog.geocoding.dto.NominatimPlaceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class GeocodingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingService.class);

    private final RestTemplate restTemplate;

    private final NominatimProperties properties;

    public GeocodingService(@Qualifier("nominatimRestTemplate") RestTemplate restTemplate, NominatimProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public List<LocationSuggestionDTO> searchLocations(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        if (!StringUtils.hasText(properties.getUserAgent())) {
            throw new IllegalStateException("nominatim.user-agent doit être configuré");
        }

        URI uri = UriComponentsBuilder.fromPath("/search")
            .queryParam("q", query.trim())
            .queryParam("format", properties.getFormat())
            .queryParam("addressdetails", properties.isAddressDetails() ? 1 : 0)
            .queryParam("limit", properties.getLimit())
            .build(true)
            .toUri();

        RequestEntity<Void> request = RequestEntity.get(uri)
            .header(HttpHeaders.USER_AGENT, properties.getUserAgent())
            .headers(headers -> {
                if (StringUtils.hasText(properties.getAcceptLanguage())) {
                    headers.set(HttpHeaders.ACCEPT_LANGUAGE, properties.getAcceptLanguage());
                }
            })
            .build();

        try {
            ResponseEntity<NominatimPlaceResponse[]> response = restTemplate.exchange(request, NominatimPlaceResponse[].class);
            NominatimPlaceResponse[] body = response.getBody();
            if (body == null || body.length == 0) {
                return List.of();
            }

            return Arrays.stream(body)
                .filter(Objects::nonNull)
                .map(this::toSuggestion)
                .filter(Objects::nonNull)
                .toList();
        } catch (RestClientException exception) {
            LOGGER.error("Erreur lors de l'appel à Nominatim", exception);
            throw new ExternalServiceException("Le service de géolocalisation est indisponible pour le moment.", exception);
        }
    }

    private LocationSuggestionDTO toSuggestion(NominatimPlaceResponse place) {
        if (place == null) {
            return null;
        }

        Double latitude = parseCoordinate(place.getLatitude());
        Double longitude = parseCoordinate(place.getLongitude());

        if (latitude == null || longitude == null) {
            return null;
        }

        NominatimAddress address = place.getAddress();
        String city = extractCity(address);
        String countryCode = Optional.ofNullable(address)
            .map(NominatimAddress::getCountryCode)
            .map(code -> code.toUpperCase(Locale.ROOT))
            .orElse(null);

        return LocationSuggestionDTO.builder()
            .displayName(place.getDisplayName())
            .latitude(latitude)
            .longitude(longitude)
            .city(city)
            .state(Optional.ofNullable(address).map(NominatimAddress::getState).orElse(null))
            .country(Optional.ofNullable(address).map(NominatimAddress::getCountry).orElse(null))
            .countryCode(countryCode)
            .postalCode(Optional.ofNullable(address).map(NominatimAddress::getPostalCode).orElse(null))
            .build();
    }

    private Double parseCoordinate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            LOGGER.warn("Coordonnée invalide renvoyée par Nominatim: {}", value);
            return null;
        }
    }

    private String extractCity(NominatimAddress address) {
        if (address == null) {
            return null;
        }

        return Optional.ofNullable(address.getCity())
            .or(() -> Optional.ofNullable(address.getTown()))
            .or(() -> Optional.ofNullable(address.getVillage()))
            .or(() -> Optional.ofNullable(address.getMunicipality()))
            .or(() -> Optional.ofNullable(address.getCounty()))
            .orElse(null);
    }
}
