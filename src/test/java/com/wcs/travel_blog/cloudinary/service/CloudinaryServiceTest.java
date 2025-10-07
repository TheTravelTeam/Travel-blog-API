package com.wcs.travel_blog.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wcs.travel_blog.cloudinary.config.CloudinaryProperties;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CloudinaryServiceTest {

    private CloudinaryService cloudinaryService;

    private Cloudinary cloudinary;

    private CloudinaryProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CloudinaryProperties();
        properties.setCloudName("demo");
        properties.setApiKey("123456");
        properties.setApiSecret("abc123");

        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", properties.getCloudName(),
            "api_key", properties.getApiKey(),
            "api_secret", properties.getApiSecret(),
            "secure", true
        ));

        cloudinaryService = new CloudinaryService(cloudinary, properties);
    }

    @Test
    void shouldGenerateSignatureWithOptionalPublicId() {
        CloudinarySignatureRequest request = new CloudinarySignatureRequest();
        request.setPublicId("travel-diaries/covers/sample");

        CloudinarySignatureResponse response = cloudinaryService.generateSignature(request);

        assertThat(response.getApiKey()).isEqualTo(properties.getApiKey());
        assertThat(response.getCloudName()).isEqualTo(properties.getCloudName());
        assertThat(response.getSignature()).isNotBlank();

        Map<String, Object> expectedParams = Map.of(
            "public_id", request.getPublicId(),
            "timestamp", response.getTimestamp()
        );

        String expected = cloudinary.apiSignRequest(expectedParams, properties.getApiSecret());

        assertThat(response.getSignature()).isEqualTo(expected);
    }

    @Test
    void shouldGenerateSecureUrlWithTransformation() {
        CloudinaryUrlRequest urlRequest = new CloudinaryUrlRequest();
        urlRequest.setWidth(400);
        urlRequest.setHeight(300);
        urlRequest.setCrop("fill");

        CloudinaryUrlResponse response = cloudinaryService.buildDeliveryUrl("sample_public_id", urlRequest);

        assertThat(response.getUrl()).contains("sample_public_id");
        assertThat(response.getUrl()).contains("w_400");
        assertThat(response.getUrl()).contains("h_300");
        assertThat(response.getUrl()).contains("c_fill");
    }
}
