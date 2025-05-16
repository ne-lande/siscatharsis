package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LicenseInfoRequest (
    @NotBlank(message = "MAC address cannot be empty")
    String macAddress,

    @NotNull(message = "LicenseCode cannot be empty")
    UUID licenseCode
) {}
