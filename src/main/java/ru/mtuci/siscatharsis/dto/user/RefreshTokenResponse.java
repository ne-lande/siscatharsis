package ru.mtuci.siscatharsis.dto.user;

// кто то забыл
public record RefreshTokenResponse (
    String refreshToken,
    String accessToken
) {}
