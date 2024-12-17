package ru.mtuci.siscatharsis.dto.license;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfoRequest {

    @NotBlank(message = "MAC address cannot be empty")
    private String macAddress;

    @NotBlank(message = "Code cannot be empty")
    private String licenseCode;

}
