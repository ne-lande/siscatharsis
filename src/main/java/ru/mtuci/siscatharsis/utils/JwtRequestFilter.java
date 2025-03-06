package ru.mtuci.siscatharsis.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mtuci.siscatharsis.services.JwtService;
import ru.mtuci.siscatharsis.services.UserService;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public JwtRequestFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

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
            if (!jwtService.validateToken(token) && !jwtService.isAccessToken(token)) return;

            String username = jwtService.extractLogin(token);

            if (username == null) return;

            if (SecurityContextHolder.getContext().getAuthentication() != null) return;

            UserDetails userDetails = userService.findByLogin(username);
            SecurityContextHolder.getContext().setAuthentication(jwtService.getAuthentication(token, userDetails));

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
