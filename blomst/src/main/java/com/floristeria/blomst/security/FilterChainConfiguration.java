package com.floristeria.blomst.security;

import com.floristeria.blomst.handler.ApiAccessDeniedHandler;
import com.floristeria.blomst.handler.ApiAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.floristeria.blomst.constant.Constants.BASE_PATH;
import static com.floristeria.blomst.constant.Constants.PUBLIC_URLS;
import static com.google.common.net.HttpHeaders.X_REQUESTED_WITH;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfiguration {

    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiHttpConfigurer apiHttpConfigurer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception ->
                                exception.accessDeniedHandler(apiAccessDeniedHandler)
                                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_URLS).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/user/delete/**")
                                .hasAnyAuthority("user:delete")
                                .anyRequest().authenticated())
                .with(apiHttpConfigurer, Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", ""));
        corsConfiguration.setAllowedHeaders(Arrays.asList(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH, ACCESS_CONTROL_ALLOW_CREDENTIALS));
        corsConfiguration.setExposedHeaders(Arrays.asList(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH, ACCESS_CONTROL_ALLOW_CREDENTIALS));
        corsConfiguration.setAllowedMethods(Arrays.asList(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()));
        corsConfiguration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(BASE_PATH, corsConfiguration);
        return source;
    }

}
