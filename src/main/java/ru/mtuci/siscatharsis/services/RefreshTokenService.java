package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.RefreshToken;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.RefreshTokenRepository;
import ru.mtuci.siscatharsis.repositories.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

        @Autowired
        RefreshTokenRepository refreshTokenRepository;

        @Autowired
        UserRepository userRepository;

        public RefreshToken createRefreshToken(String username) {
                User user = userRepository.findByLogin(username).orElseThrow(
                        () -> new UsernameNotFoundException("User not found by login"));

                refreshTokenRepository.findByUser(user).ifPresent(token -> {
                        token.setUser(null);
                        refreshTokenRepository.save(token);
                });

                RefreshToken refreshToken = RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusMillis(600000)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                        .build();

                return refreshTokenRepository.save(refreshToken);
        }



        public Optional<RefreshToken> findByToken(String token){
                return refreshTokenRepository.findByToken(token);
        }

        public RefreshToken verifyExpiration(RefreshToken token){
                if(token.getExpiryDate().compareTo(Instant.now())<0){
                        refreshTokenRepository.delete(token);
                        throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
                }
                return token;
        }

}