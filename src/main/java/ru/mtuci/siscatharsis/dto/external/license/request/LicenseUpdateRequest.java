package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LicenseUpdateRequest {
    @NotBlank(message = "Login is empty")
    private String login;

    //@NotBlank(message = "License code is empty")
    private UUID licenseCode;

    @NotBlank(message = "MAC address is empty")
    private String macAddress;
}
