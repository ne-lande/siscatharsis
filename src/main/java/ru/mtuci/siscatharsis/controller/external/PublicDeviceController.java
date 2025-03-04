package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.mtuci.siscatharsis.dto.external.device.request.DeviceCreateRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/device")
public class PublicDeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    @Autowired
    public PublicDeviceController(DeviceService deviceService, UserService userService) {
        this.deviceService = deviceService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody DeviceCreateRequest deviceRequest) {
        User user = userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        Device device;

        try {
            device = deviceService.findByMacAddressAndUser(deviceRequest.getMacAddress(), user);
            return ApiMessage.BadRequest("device already exists");
        } catch (Exception e) {
            device = new Device(deviceRequest.getName(), deviceRequest.getMacAddress(), user);
            deviceService.save(device);

            return ApiMessage.Success(device);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        User user = userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());

        if (deviceService.findById(id).getUser().getId() == user.getId()) {
            return ApiMessage.Success(deviceService.findById(id));
        }

        return ApiMessage.BadRequest("Not yours");
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll() {
        User user = userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());

        return ApiMessage.Success(deviceService.getByUserId(user.getId()));
    }
}
