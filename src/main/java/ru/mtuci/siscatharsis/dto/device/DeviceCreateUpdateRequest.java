package ru.mtuci.siscatharsis.dto.device;

import jakarta.validation.constraints.NotBlank;

public record DeviceCreateUpdateRequest(

    @NotBlank(message = "Device name is empty")
    String deviceName,

    @NotBlank(message = "Mac address is empty")
    String macAddress,

    Long userId
) {}
