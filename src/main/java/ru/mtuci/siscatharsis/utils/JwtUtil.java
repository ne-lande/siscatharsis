package ru.mtuci.siscatharsis.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/* Этот класс состоит из двух частей
        динамичной: проверкой подписи, подписью jwt и преметодов для создания jwt
        статичной: достать
 */
@Component
public class JwtUtil {
        @Value("${jwt.secret}")
        private String secretKey;

        @Value("${jwt.access.expiration}")
        private Long accessExpiration;

        @Value("${jwt.refresh.expiration}")
        private Long refreshExpiration;

        public String createAccessToken(UserDetails userDetails) {
                Map<String, Object> accessClaims = new HashMap<>();
                accessClaims.put("token_type", "access");

                accessClaims.put("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                );

                return createToken(accessClaims, userDetails.getUsername(), accessExpiration);
        }

        public String createRefreshToken(UserDetails userDetails, UUID tokenId, Long deviceId) {
                Map<String, Object> refreshClaims = new HashMap<>();

                refreshClaims.put("token_type", "refresh");
                refreshClaims.put("token_id", tokenId.toString());
                refreshClaims.put("device_id", deviceId);

                return createToken(refreshClaims, userDetails.getUsername(), refreshExpiration);
        }

        public Boolean isAccessToken(String token) {
                String tokenType = extractClaim(token, c -> (String) c.get("token_type"));
                return tokenType.equals("access");
        }

        private Key getSigningKey() {
                return Keys.hmacShaKeyFor(secretKey.getBytes());
        }

        private String createToken(Map<String, Object> claims, String subject, Long expiration) {
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .signWith(getSigningKey())
                        .compact();
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
        }

        public UUID extractRefreshTokenId(String token) {
                String claim = extractClaim(token, c -> c.get("token_id", String.class));
                return UUID.fromString(claim);
        }
}
