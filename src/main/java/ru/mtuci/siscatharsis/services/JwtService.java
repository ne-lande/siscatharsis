package ru.mtuci.siscatharsis.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.external.auth.response.UserTokenResponse;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.SessionId;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.model.UserSession;
import ru.mtuci.siscatharsis.repositories.UserSessionRepository;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secretKey;

        @Value("${jwt.access.expiration}")
        private Long accessExpiration;

        @Value("${jwt.refresh.expiration}")
        private Long refreshExpiration;

        private final UserSessionRepository userSessionRepository;
        private final UserService userService;
        private final DeviceService deviceService;

        @Autowired
        public JwtService(UserSessionRepository userSessionRepository, UserService userService, DeviceService deviceService) {
                this.userSessionRepository = userSessionRepository;
                this.userService = userService;
                this.deviceService = deviceService;
        }

        private Key getSigningKey() {
                return Keys.hmacShaKeyFor(secretKey.getBytes());
        }

        public String createToken(Map<String, Object> claims, String subject, Long expiration) {
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .signWith(getSigningKey())
                        .compact();
        }

        public UserTokenResponse generateTokenPair(UserDetails userDetails, Long deviceId) {
                Map<String, Object> accessClaims = new HashMap<>();
                accessClaims.put("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                );
                accessClaims.put("token_type", "access");
                accessClaims.put("device_id", deviceId);

                Map<String, Object> refreshClaims = new HashMap<>();
                refreshClaims.put("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                );

                UUID token_id = UUID.randomUUID();
                refreshClaims.put("token_type", "refresh");
                refreshClaims.put("token_id", token_id.toString());
                refreshClaims.put("device_id", deviceId);

                String refreshToken = createToken(refreshClaims, userDetails.getUsername(), refreshExpiration);
                String accessToken = createToken(accessClaims, userDetails.getUsername(), accessExpiration);

                User user = userService.findByLogin(userDetails.getUsername());
                Device device = deviceService.findById(deviceId);

                //try to find then save
                userSessionRepository.findByUserAndDevice(user, device).ifPresentOrElse(
                        userSession -> {
                                userSession.setRefreshTokenId(token_id);

                                userSessionRepository.save(userSession);
                        },
                        () -> {
                                UserSession userSession = UserSession.builder()
                                        .device(device)
                                        .user(user)
                                        .refreshTokenId(token_id)
                                        .build();

                                userSessionRepository.save(userSession);
                        }
                );

                return UserTokenResponse.builder()
                        .token(refreshToken)
                        .accessToken(accessToken)
                        .build();
        }

        private void blockSessions(User user) {
                userSessionRepository.getByUser(user).stream().forEach(c -> {
                        c.setRefreshTokenId(null);
                        userSessionRepository.save(c);
                });
        }

        public UserTokenResponse rotateToken(UUID tokenId, String username, Long deviceId) {
                // Проверяет что ид сессии совподает если нет блокировать все сессии если да обновлять сессию и выдавать токены
                User user = userService.findByLogin(username);

                // здесь сессия может быть невалидна
                UserSession userSession = userSessionRepository.findByRefreshTokenId(tokenId).orElseThrow(() -> {
                        blockSessions(user);
                        throw new RuntimeException("Cant find valid session");
                });

                Device device = deviceService.findById(deviceId);

                //смотрим что сессия действительно принадлежит юзеру
                if (!device.getUser().getId().equals(user.getId())) {
                        throw new RuntimeException("Cant find valid session");
                }

                return generateTokenPair(userService.loadUserByUsername(username), deviceId);
        }

        private boolean isTokenExpired(String token) {
                return extractExpiration(token).before(new Date());
        }

        public boolean validateToken(String token) {
                try {
                        Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token);
                        return true;
                } catch (Exception e) {
                        return false;
                }
        }

        public Boolean isAccessToken(String token) {
                String tokenType = extractClaim(token, c -> (String) c.get("token_type"));
                return tokenType.equals("access");
        }

        private Claims extractAllClaims(String token) {
                return Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
                final Claims claims = extractAllClaims(token);
                return claimsResolver.apply(claims);
        }

        public String extractLogin(String token) {
                return extractClaim(token, Claims::getSubject);
        }

        public Date extractExpiration(String token) {
                return extractClaim(token, Claims::getExpiration);
        }

        public Set<GrantedAuthority> extractAuthorities(String token) {
                List<?> roles = extractClaim(token, claims -> claims.get("role", List.class));
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority((String) role))
                        .collect(Collectors.toSet());
        }

        public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
                Set<GrantedAuthority> authorities = extractAuthorities(token);
                return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        }

        public Long extractDeviceId(String token) {
                return extractClaim(token, c -> c.get("device_id", Long.class));
                //return Long.parseLong((String) claim);
        }

        public UUID extractRefreshTokenId(String token) {
                String claim = extractClaim(token, c -> c.get("token_id", String.class));
                return UUID.fromString(claim);
        }
}
