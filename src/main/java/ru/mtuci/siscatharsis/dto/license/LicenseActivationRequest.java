package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record LicenseActivationRequest (

    UUID activationCode,

    @NotBlank(message = "Mac address is empty")
    String macAddress
) {}
