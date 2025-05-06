package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.device.DeviceCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final ApiConstructor apiConstructor;

    @GetMapping("/")
    public ResponseEntity<?> readAllDevice(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Device> devices = deviceService.getAllDevices(page, size);

        return apiConstructor.success(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readDevice(@PathVariable Long id) {
        Device device = deviceService.requireById(id);

        return apiConstructor.success(device);
    }

    @PostMapping("/")
    public ResponseEntity<?> createDevice(@RequestBody DeviceCreateUpdateRequest deviceRequest) {
        Device device = deviceFromDto(deviceRequest);

        deviceService.create(device);

        return apiConstructor.success(device);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody DeviceCreateUpdateRequest deviceRequest) {
        Device device = deviceFromDto(deviceRequest);

        deviceService.update(id, device);

        return apiConstructor.success(device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        deviceService.delete(id);

        return apiConstructor.success(id);
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
