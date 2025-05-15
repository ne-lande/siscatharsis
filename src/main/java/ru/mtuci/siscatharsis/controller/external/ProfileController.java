package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.device.DeviceUserAddChangeRequest;
import ru.mtuci.siscatharsis.dto.user.PasswordChangeRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.user.SessionService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.user.UserService;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final SessionService sessionService;
    private final ResponseUtils responseUtils;

    @GetMapping("/me")
    public ResponseEntity<?> myProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return responseUtils.success(userDetails);
    }

    @PostMapping("/change-password")
    public void changePassword(Authentication authentication, @RequestBody PasswordChangeRequest changePasswordRequest) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        sessionService.blockActiveSessions(userId);

        userService.update(userId, user, changePasswordRequest.password());
    }

    @GetMapping("/devices")
    public ResponseEntity<?> fetchMyDevices(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<Device> devices = deviceService.getByUserId(user.getId());

        return responseUtils.success(devices);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.findById(id);
        if (device.getUser().equals(user)) {
            return responseUtils.success(device);
        }

        return responseUtils.badRequest("Not yours");
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> changeMyDevice(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeviceUserAddChangeRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.findById(id);
        if (!device.getUser().equals(user)) {
            return responseUtils.badRequest("Not yours");
        }

        Device updateDevice = Device.builder()
                .name(deviceRequest.name())
                .macAddress(deviceRequest.macAddress())
                .build();

        deviceService.update(id, updateDevice);

        return responseUtils.success(device);
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteMyDevice(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        List<Device> userDevices = deviceService.getByUserId(user.getId());

        if (userDevices.size() == 1) {
            return responseUtils.badRequest("You cant delete your last device");
        }

        userDevices.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .ifPresent(d -> deviceService.delete(id));

        return responseUtils.success(id);
    }

    @PostMapping("/devices/add")
    public ResponseEntity<?> createDevice(Authentication authentication, @Valid @RequestBody DeviceUserAddChangeRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        String macAddress = deviceRequest.macAddress();

        if (deviceService.existsUserDevice(macAddress, user)) {
            return responseUtils.badRequest("Such device already exists");
        }

        Device device = Device.builder()
                .user(user)
                .name(deviceRequest.name())
                .macAddress(macAddress)
                .build();

        deviceService.create(device);

        return responseUtils.success(device);
    }
}
