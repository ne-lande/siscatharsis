package ru.mtuci.siscatharsis.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceCreateUpdateRequest(

    @NotBlank(message = "Device name is empty")
    String deviceName,

    @NotBlank(message = "Mac address is empty")
    String macAddress,

    @NotNull(message = "UserId cannot be null")
    Long userId
) {}
