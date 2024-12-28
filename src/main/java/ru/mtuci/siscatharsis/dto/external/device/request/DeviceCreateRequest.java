package ru.mtuci.siscatharsis.dto.external.device.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCreateRequest {
    @NotBlank(message = "Device name is empty")
    private String name;

    @NotBlank(message = "Mac address is empty")
    private String macAddress;
}
