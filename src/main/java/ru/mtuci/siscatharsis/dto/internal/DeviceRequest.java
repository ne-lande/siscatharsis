package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {

    @NotBlank(message = "Device name is empty")
    private String deviceName;

    @NotBlank(message = "Mac address is empty")
    private String macAddress;

    private Long userId;
}
