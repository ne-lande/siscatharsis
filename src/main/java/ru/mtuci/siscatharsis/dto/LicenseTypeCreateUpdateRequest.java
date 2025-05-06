package ru.mtuci.siscatharsis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LicenseTypeCreateUpdateRequest(

    @NotBlank(message = "Name is empty")
    String name,

    @NotNull
    int defaultDuration,

    @NotBlank(message = "Description is empty")
    String description,

    @NotNull
    int defaultDeviceCount
) {}
