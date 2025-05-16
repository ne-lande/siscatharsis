package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LicenseActivationRequest (
    @NotNull(message = "activationCode cannot be null")
    UUID activationCode,

    @NotBlank(message = "Mac address is empty")
    String macAddress
) {}
