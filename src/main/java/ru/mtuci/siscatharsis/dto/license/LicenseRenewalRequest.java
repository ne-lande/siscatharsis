package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LicenseRenewalRequest(
    @NotBlank(message = "Login is empty")
    String login,

    @NotNull(message = "License code is empty")
    UUID licenseCode,

    @NotBlank(message = "MAC address is empty")
    String macAddress
) {}
