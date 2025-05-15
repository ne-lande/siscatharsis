package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record LicenseInfoRequest (
    @NotBlank(message = "MAC address cannot be empty")
    String macAddress,

    @NotBlank(message = "Code cannot be empty")
    UUID licenseCode
) {}
