package ru.mtuci.siscatharsis.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("unused")
@Configuration
public class ApplicationConfig {

        private static final int BCRYPT_STRENGTH = 12;

        @Bean
        public ObjectMapper objectMapper() {
                return new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
        }

}
