package com.wcs.travel_blog.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${cors.allowed-origin}")
    private String allowedOrigins;

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private List<String> resolveAllowedOrigins() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            return List.of("https://travel-blog.cloud");
        }
        return Arrays.stream(allowedOrigins.split("\\s*,\\s*"))
                .filter(origin -> !origin.isBlank())
                .toList();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)

                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(resolveAllowedOrigins());
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
                            config.setAllowCredentials(true);
                            config.setMaxAge(3600L);
                            return config;
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth path
                        .requestMatchers(HttpMethod.POST,"/auth/**").permitAll()

                        // Articles paths
                        .requestMatchers(HttpMethod.GET, "/articles/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/articles/**").permitAll()

                        // Travel Diaries paths
                        .requestMatchers(HttpMethod.GET, "/travel-diaries/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/travel-diaries/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, "/travel-diaries/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/travel-diaries/**").hasAnyRole("ADMIN","USER")

                        // Users paths
                        .requestMatchers(HttpMethod.GET, "/users").permitAll()
                        .requestMatchers(new RegexRequestMatcher("^/users/\\d+$", "GET")).permitAll() // Regex allowing only get by id
                        .requestMatchers(HttpMethod.GET, "/users/email").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/pseudo").authenticated()

                        // The rest of requests
                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("text/plain");
                            response.getWriter().write("Accès refusé : Vous devez être connecté pour accéder à cette ressource. ");
                        })
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration
                                                               authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
