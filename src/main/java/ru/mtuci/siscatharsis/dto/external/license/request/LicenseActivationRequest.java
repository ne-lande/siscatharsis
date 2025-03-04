package ru.mtuci.siscatharsis.dto.external.license.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class LicenseActivationRequest {

    private UUID activationCode;

    @NotBlank(message = "Mac address is empty")
    private String macAddress;
}
