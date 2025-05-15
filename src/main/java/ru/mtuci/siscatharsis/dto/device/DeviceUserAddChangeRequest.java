package ru.mtuci.siscatharsis.dto.device;

import jakarta.validation.constraints.NotBlank;

public record DeviceUserAddChangeRequest(
    @NotBlank(message = "Device name is empty")
    String name,

    @NotBlank(message = "Mac address is empty")
    String macAddress
) {}
