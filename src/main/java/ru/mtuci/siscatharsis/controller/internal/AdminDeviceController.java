package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AdminDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    @Autowired
    public AdminDeviceController(DeviceService deviceService, UserService userService) {
        this.deviceService = deviceService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody DeviceRequest deviceRequest) {
        User user = userService.findById(deviceRequest.getUserId());

        String macAddress = deviceRequest.getMacAddress();

        if (deviceService.existsUserDevice(macAddress, user)) {
            return ApiMessage.BadRequest("Already Exists");
        }

        String name = deviceRequest.getDeviceName();

        Device device = Device.builder()
                .name(name)
                .macAddress(macAddress)
                .user(user)
                .build();

        deviceService.save(device);

        return ApiMessage.Success(device);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        Device device = deviceService.findById(id);

        return ApiMessage.Success(device);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DeviceRequest deviceRequest) {
        Device device = deviceService.findById(id);

        String name = deviceRequest.getDeviceName();
        String macAddress = deviceRequest.getMacAddress();
        User user = userService.findById(deviceRequest.getUserId());

        device.setName(name);
        device.setMacAddress(macAddress);
        device.setUser(user);

        deviceService.save(device);

        return ApiMessage.Success(device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Device device = deviceService.findById(id);

        deviceService.delete(device);

        return ApiMessage.Success(id);
    }
}
