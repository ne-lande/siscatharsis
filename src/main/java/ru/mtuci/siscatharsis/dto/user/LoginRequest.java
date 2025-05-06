package ru.mtuci.siscatharsis.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login is empty")
        String login,

        @NotBlank(message = "Password is empty")
        String password,

        @NotBlank(message = "Mac is empty")
        String macAddress
) {}