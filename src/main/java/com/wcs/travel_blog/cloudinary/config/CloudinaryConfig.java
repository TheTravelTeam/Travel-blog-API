package com.wcs.travel_blog.cloudinary.config;

import com.cloudinary.Cloudinary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(CloudinaryProperties.class)
public class CloudinaryConfig {

    @Bean
    @ConditionalOnProperty(prefix = "cloudinary", name = "enabled", havingValue = "true")
    public Cloudinary cloudinary(CloudinaryProperties properties) {
        if (!StringUtils.hasText(properties.getCloudName())
            || !StringUtils.hasText(properties.getApiKey())
            || !StringUtils.hasText(properties.getApiSecret())) {
            throw new IllegalStateException("Cloudinary est activé mais les identifiants sont manquants. Définissez cloudinary.cloud-name/api-key/api-secret ou désactivez cloudinary.enabled.");
        }
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", properties.getCloudName());
        config.put("api_key", properties.getApiKey());
        config.put("api_secret", properties.getApiSecret());
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
