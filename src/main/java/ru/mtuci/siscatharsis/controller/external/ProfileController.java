package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.device.request.DeviceCreateRequest;
import ru.mtuci.siscatharsis.dto.external.profile.request.ChangePasswordRequest;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileController(UserService userService, DeviceService deviceService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.deviceService = deviceService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/me")
    public ResponseEntity<?> myProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ApiMessage.Success(userDetails);
    }

    @PostMapping("/change-password")
    public void changePassword(Authentication authentication, @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = (User) authentication.getPrincipal();
        String newPassword = changePasswordRequest.getPassword();
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);

        // тут еще надо все сессии просрочить
        userService.save(user);
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

        Device device = deviceService.findById(id);
        if (!device.getUser().equals(user)) {
            return ApiMessage.BadRequest("Not yours");
        }

        device.setName(deviceRequest.getName());
        device.setMacAddress(deviceRequest.getMacAddress());

        deviceService.save(device);

        return ApiMessage.Secret("ggg");
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteMyDevice(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();

        List<Device> userDevices = deviceService.getByUserId(user.getId());

        if (userDevices.size() == 1) {
            return ApiMessage.BadRequest("You cant delete your last device");
        }

        Device device = userDevices.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        deviceService.delete(device);

        return ApiMessage.Success("success");
    }

    @PostMapping("/devices/add")
    public ResponseEntity<?> createDevice(Authentication authentication, @Valid @RequestBody DeviceCreateRequest deviceRequest) {
        User user = (User) authentication.getPrincipal();

        String macAddress = deviceRequest.getMacAddress();

        if (deviceService.existsUserDevice(macAddress, user)) {
            return ApiMessage.BadRequest("Such device already exists");
        }

        String name = deviceRequest.getName();

        Device device = Device.builder()
                .user(user)
                .name(name)
                .macAddress(macAddress)
                .build();

        deviceService.save(device);

        return ApiMessage.Success(device);
    }
}
