package ru.mtuci.siscatharsis.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mtuci.siscatharsis.services.user.UserService;

import java.io.IOException;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        filterLogic(token);

        filterChain.doFilter(request, response);
    }

    private void filterLogic(String token) {
        if (token == null) return;

        try {
            if (!jwtUtil.validateToken(token) && !jwtUtil.isAccessToken(token)) return;

            String username = jwtUtil.extractLogin(token);

            if (username == null) return;

            if (SecurityContextHolder.getContext().getAuthentication() != null) return;

            UserDetails userDetails = userService.loadUserByUsername(username);
            SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(token, userDetails));
        } catch (Exception e){
            System.err.println("JWT Filter error: " + e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println(bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
