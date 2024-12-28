package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class LicenseActivationRequest {

    @NotBlank(message = "Activation code is empty")
    private UUID activationCode;

    @NotBlank(message = "Mac address is empty")
    private String macAddress;
}
