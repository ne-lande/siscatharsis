package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseTypeRequest {

    @NotBlank(message = "Name is empty")
    private String name;

    @NotNull
    private int defaultDuration;

    @NotBlank(message = "Description is empty")
    private String description;

    @NotNull
    private int defaultDeviceCount;
}
