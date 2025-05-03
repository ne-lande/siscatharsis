package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.device.request.DeviceCreateRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final DeviceService deviceService;

    @Autowired
    public ProfileController(UserService userService, DeviceService deviceService) {
        this.userService = userService;
        this.deviceService = deviceService;
    }
    @GetMapping("/me")
    public ResponseEntity<?> myProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ApiMessage.Success(userDetails);
    }

    @PostMapping("/change-password")
    public void changePassword(Authentication authentication) {

    }

    @GetMapping("/devices")
    public ResponseEntity<?> fetchMyDevices(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<Device> devices = deviceService.getByUserId(user.getId());

        return ApiMessage.Success(devices);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        if (deviceService.findById(id).getUser().getId() == user.getId()) {
            return ApiMessage.Success(deviceService.findById(id));
        }

        return ApiMessage.BadRequest("Not yours");
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> changeMyDevice(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeviceCreateRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        if (deviceService.findById(id).getUser().getId() == user.getId()) {
            return ApiMessage.Success(deviceService.findById(id));
        }

        //TODO: implement

        return ApiMessage.Secret("ggg");
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteMyDevice() {
        return ApiMessage.Success(null);
    }

    @PostMapping("/devices/add")
    public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody DeviceCreateRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        if (deviceService.findByMacAddressAndUser(deviceRequest.getMacAddress(), user) != null) {
            return ApiMessage.BadRequest("such device already exists");
        }

        String name = deviceRequest.getName();
        String macAddress = deviceRequest.getMacAddress();

        Device device = Device.builder()
                .user(user)
                .name(name)
                .macAddress(macAddress)
                .build();

        deviceService.save(device);

        return ApiMessage.Success(device);
    }
}
