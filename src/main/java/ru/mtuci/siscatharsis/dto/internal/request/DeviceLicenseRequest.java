package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class DeviceLicenseRequest {
    @NotBlank(message = "License id is empty")
    private String licenseId;

    @NotBlank(message = "Device id is empty")
    private String deviceId;

    private Date activationDate;
}
