package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.device.DeviceUserAddChangeRequest;
import ru.mtuci.siscatharsis.dto.user.PasswordChangeRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.SessionService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final SessionService sessionService;
    private final ApiConstructor apiConstructor;

    @GetMapping("/me")
    public ResponseEntity<?> myProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return apiConstructor.success(userDetails);
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

        return apiConstructor.success(devices);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.findById(id);
        if (device.getUser().equals(user)) {
            return apiConstructor.success(device);
        }

        return apiConstructor.badRequest("Not yours");
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> changeMyDevice(Authentication authentication, @PathVariable Long id, @Valid @RequestBody DeviceUserAddChangeRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.findById(id);
        if (!device.getUser().equals(user)) {
            return apiConstructor.badRequest("Not yours");
        }

        Device updateDevice = Device.builder()
                .name(deviceRequest.name())
                .macAddress(deviceRequest.macAddress())
                .build();

        deviceService.update(id, updateDevice);

        return apiConstructor.success(device);
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteMyDevice(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        List<Device> userDevices = deviceService.getByUserId(user.getId());

        if (userDevices.size() == 1) {
            return apiConstructor.badRequest("You cant delete your last device");
        }

        userDevices.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .ifPresent(d -> deviceService.delete(id));

        return apiConstructor.success(id);
    }

    @PostMapping("/devices/add")
    public ResponseEntity<?> createDevice(Authentication authentication, @Valid @RequestBody DeviceUserAddChangeRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        String macAddress = deviceRequest.macAddress();

        if (deviceService.existsUserDevice(macAddress, user)) {
            return apiConstructor.badRequest("Such device already exists");
        }

        Device device = Device.builder()
                .user(user)
                .name(deviceRequest.name())
                .macAddress(macAddress)
                .build();

        deviceService.create(device);

        return apiConstructor.success(device);
    }
}
