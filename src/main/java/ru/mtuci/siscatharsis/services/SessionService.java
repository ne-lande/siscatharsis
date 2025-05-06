package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.user.RefreshTokenResponse;
import ru.mtuci.siscatharsis.model.UserSession;
import ru.mtuci.siscatharsis.repositories.UserSessionRepository;
import ru.mtuci.siscatharsis.utils.JwtUtil;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService {
        private final UserSessionRepository userSessionRepository;
        private final JwtUtil jwtUtil;

        public RefreshTokenResponse generateTokenPair(UserDetails userDetails, Long userId, Long deviceId) {
                // просрочить все прошлые сессии
                userSessionRepository.getByUserIdAndDeviceId(userId, deviceId).stream()
                        .filter(c -> c.getStatus() == UserSession.SessionStatus.ACTIVE)
                        .forEach(c -> {
                                c.setStatus(UserSession.SessionStatus.EXPIRED);
                                userSessionRepository.save(c);
                        });

                UUID tokenId = UUID.randomUUID();

                // save session
                userSessionRepository.save(
                        UserSession.builder()
                                .deviceId(deviceId)
                                .userId(userId)
                                .refreshTokenId(tokenId)
                                .status(UserSession.SessionStatus.ACTIVE)
                                .build()
                );

                String refreshToken = jwtUtil.createRefreshToken(userDetails, tokenId, deviceId);
                String accessToken = jwtUtil.createAccessToken(userDetails);

                return RefreshTokenResponse.builder()
                        .token(refreshToken)
                        .accessToken(accessToken)
                        .build();
        }

        private void blockActiveSessions(Long userId) {
                userSessionRepository.getByUserId(userId).stream().filter(
                        c -> c.getStatus() == UserSession.SessionStatus.ACTIVE
                ).forEach(
                        c -> {
                                c.setStatus(UserSession.SessionStatus.BLOCKED);
                                userSessionRepository.save(c);
                        }
                );
        }

        /* Достаем сессию по токену
         * если сессии нет выкидываем ошибку
         * если сессия есть проверяем что сессия не просрочена
         * если сессия просрочена блокируем все активные сессии пользователя
         * если сессия активна просрачиваем ее и выдаем новую пару токенов
         */
        public void rotateToken(UUID tokenId, Long userId) {
                UserSession userSession = userSessionRepository.findByRefreshTokenId(tokenId).orElseThrow(() -> new RuntimeException("Cant find valid session"));

                switch (userSession.getStatus()) {
                        case EXPIRED -> {
                                blockActiveSessions(userId);
                                throw new RuntimeException("Session intercept detected.");
                        }
                        case BLOCKED -> {
                                throw new RuntimeException("Session was blocked by security measures");
                        }
                };
        }
}
