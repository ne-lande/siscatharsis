package ru.mtuci.siscatharsis.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.mtuci.siscatharsis.utils.JwtRequestFilter;

@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/auth/**", "/info/**", "/public-key/get", "/actuator/**", "/v3/**", "/swagger-ui/**"
    };

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(a -> a
                .requestMatchers(PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/signatures/**")
                .authenticated()
                .anyRequest()
                .authenticated()
        );

        httpSecurity.sessionManagement(s -> s.
                sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
