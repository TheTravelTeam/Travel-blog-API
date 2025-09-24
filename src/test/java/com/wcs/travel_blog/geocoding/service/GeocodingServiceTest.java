package com.wcs.travel_blog.geocoding.service;

import com.wcs.travel_blog.exception.ExternalServiceException;
import com.wcs.travel_blog.geocoding.config.NominatimProperties;
import com.wcs.travel_blog.geocoding.dto.LocationSuggestionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeocodingServiceTest {

    private GeocodingService geocodingService;

    private MockRestServiceServer mockServer;

    private NominatimProperties properties;

    @BeforeEach
    void setUp() {
        properties = new NominatimProperties();
        properties.setBaseUrl("https://nominatim.test");
        properties.setUserAgent("travel-blog-test/1.0 (test@example.com)");
        properties.setLimit(5);

        RestTemplate restTemplate = new RestTemplateBuilder()
            .rootUri(properties.getBaseUrl())
            .build();

        mockServer = MockRestServiceServer.bindTo(restTemplate)
            .ignoreExpectOrder(true)
            .build();

        geocodingService = new GeocodingService(restTemplate, properties);
    }

    @Test
    void shouldReturnSuggestionsWhenNominatimResponds() {
        String responseBody = """
            [
              {
                \"display_name\": \"Paris, Île-de-France, France\",
                \"lat\": \"48.8566\",
                \"lon\": \"2.3522\",
                \"address\": {
                  \"city\": \"Paris\",
                  \"state\": \"Île-de-France\",
                  \"country\": \"France\",
                  \"country_code\": \"fr\",
                  \"postcode\": \"75000\"
                }
              }
            ]
            """;

        mockServer.expect(MockRestRequestMatchers.requestTo("https://nominatim.test/search?q=Paris&format=jsonv2&addressdetails=1&limit=5"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.USER_AGENT, properties.getUserAgent()))
            .andRespond(MockRestResponseCreators.withSuccess(responseBody, MediaType.APPLICATION_JSON));

        List<LocationSuggestionDTO> results = geocodingService.searchLocations("Paris");

        mockServer.verify();

        assertThat(results).hasSize(1);
        LocationSuggestionDTO suggestion = results.get(0);
        assertThat(suggestion.getDisplayName()).isEqualTo("Paris, Île-de-France, France");
        assertThat(suggestion.getLatitude()).isEqualTo(48.8566);
        assertThat(suggestion.getLongitude()).isEqualTo(2.3522);
        assertThat(suggestion.getCity()).isEqualTo("Paris");
        assertThat(suggestion.getCountry()).isEqualTo("France");
        assertThat(suggestion.getCountryCode()).isEqualTo("FR");
        assertThat(suggestion.getPostalCode()).isEqualTo("75000");
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenNominatimFails() {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://nominatim.test/search?q=Lyon&format=jsonv2&addressdetails=1&limit=5"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.USER_AGENT, properties.getUserAgent()))
            .andRespond(MockRestResponseCreators.withServerError());

        assertThatThrownBy(() -> geocodingService.searchLocations("Lyon"))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessage("Le service de géolocalisation est indisponible pour le moment.");

        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyListWhenQueryIsBlank() {
        List<LocationSuggestionDTO> results = geocodingService.searchLocations("   ");
        assertThat(results).isEmpty();
        mockServer.verify();
    }
}
