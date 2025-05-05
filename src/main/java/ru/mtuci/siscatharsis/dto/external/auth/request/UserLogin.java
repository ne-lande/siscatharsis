package ru.mtuci.siscatharsis.dto.external.auth.request;

import jakarta.validation.constraints.NotBlank;

public record UserLogin(
        @NotBlank(message = "Login is empty")
        String login,

        @NotBlank(message = "Password is empty")
        String password,

        @NotBlank(message = "Mac is empty")
        String macAddress
) {}