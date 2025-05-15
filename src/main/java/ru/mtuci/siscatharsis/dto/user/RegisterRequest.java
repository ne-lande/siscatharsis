package ru.mtuci.siscatharsis.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "login cannot be empty")
    String login,

    @NotBlank(message = "password cannot be empty")
    String password,

    @NotBlank(message = "email cannot be empty")
    @Email(message = "email should be valid")
    String email,

    @NotBlank(message = "mac cannot be empty")
    String macAddress
) {}
