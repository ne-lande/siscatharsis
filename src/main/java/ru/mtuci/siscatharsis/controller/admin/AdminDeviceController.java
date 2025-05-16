package ru.mtuci.siscatharsis.controller.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.device.DeviceCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.user.UserService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SecurityRequirement(name = "bearerAuth")
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final ResponseUtils responseUtils;

    @GetMapping("/all")
    public ResponseEntity<?> readAllDevices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Device> devices = deviceService.getAllDevices(page, size);

        return responseUtils.success(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readDevice(@PathVariable Long id) {
        Device device = deviceService.requireById(id);

        return responseUtils.success(device);
    }

    @PostMapping("/")
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceCreateUpdateRequest deviceRequest) {
        Device device = deviceFromDto(deviceRequest);

        deviceService.create(device);

        return responseUtils.success(device);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceCreateUpdateRequest deviceRequest) {
        Device device = deviceFromDto(deviceRequest);

        deviceService.update(id, device);

        return responseUtils.success(device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        deviceService.delete(id);

        return responseUtils.success(id);
    }

    private Device deviceFromDto(DeviceCreateUpdateRequest dto) {
        User user = userService.requireById(dto.userId());

        return Device.builder()
                .user(user)
                .name(dto.deviceName())
                .macAddress(dto.macAddress())
                .build();
    }
}
