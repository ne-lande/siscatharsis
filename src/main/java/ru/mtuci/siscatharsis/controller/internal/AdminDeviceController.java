package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.request.DeviceRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> readAllDevice(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Device> devices = deviceService.getAllDevices(page, size);

        return ApiMessage.Success(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readDevice(@PathVariable Long id) {
        Device device = deviceService.requireById(id);

        return ApiMessage.Success(device);
    }

    @PostMapping("/")
    public ResponseEntity<?> createDevice(@RequestBody DeviceRequest deviceRequest) {
        User user = userService.requireById(deviceRequest.getUserId());

        Device device = Device.builder()
                .user(user)
                .name(deviceRequest.getDeviceName())
                .macAddress(deviceRequest.getMacAddress())
                .build();

        deviceService.create(device, user);

        return ApiMessage.Success(device);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody DeviceRequest deviceRequest) {
        User user = userService.requireById(deviceRequest.getUserId());

        Device device = Device.builder()
                .user(user)
                .name(deviceRequest.getDeviceName())
                .macAddress(deviceRequest.getMacAddress())
                .build();

        deviceService.update(id, device);

        return ApiMessage.Success(device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        deviceService.delete(id);

        return ApiMessage.Success(id);
    }
}
