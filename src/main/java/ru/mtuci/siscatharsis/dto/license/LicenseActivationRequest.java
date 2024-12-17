package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseActivationRequest {

    @NotBlank(message = "Activation code is empty")
    private String activationCode;

    @NotBlank(message = "Device name is empty")
    private String deviceName;

    @NotBlank(message = "Mac address is empty")
    private String macAddress;
}
