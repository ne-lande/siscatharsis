package ru.mtuci.siscatharsis.dto.user;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @NotBlank
        String password
) {}
