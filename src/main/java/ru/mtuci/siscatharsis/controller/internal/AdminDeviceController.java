package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.device.DeviceCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.user.UserService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final ResponseUtils responseUtils;

        private Device deviceFromDto(DeviceCreateUpdateRequest dto) {
        User user = userService.requireById(dto.userId());

        return Device.builder()
                .user(user)
                .name(dto.deviceName())
                .macAddress(dto.macAddress())
                .build();
    }
}
