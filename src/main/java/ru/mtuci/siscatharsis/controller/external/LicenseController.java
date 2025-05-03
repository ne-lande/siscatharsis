package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.external.license.request.LicenseActivationRequest;
import ru.mtuci.siscatharsis.dto.external.license.request.LicenseInfoRequest;
import ru.mtuci.siscatharsis.dto.external.license.request.LicenseUpdateRequest;
import ru.mtuci.siscatharsis.dto.external.license.response.Ticket;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/license")
public class LicenseController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @Autowired
    public LicenseController(UserService userService, DeviceService deviceService, LicenseService licenseService) {
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseService = licenseService;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentLicense(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // TODO: yet to be implemented

        return ApiMessage.Secret("hoi");
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(Authentication authentication, @Valid @RequestBody LicenseInfoRequest licenseInfoRequest) throws Exception {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.findByMacAddressAndUser(licenseInfoRequest.getMacAddress(), user);

        List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device);
        List<Ticket> tickets = new ArrayList<>();

        for (License activeLicense : activeLicenses) {
            tickets.add(licenseService.generateTicket(activeLicense, device));
        }

        return ApiMessage.Success(tickets);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(Authentication authentication, @Valid @RequestBody LicenseActivationRequest licenseActivationRequest) throws Exception {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.registerOrUpdateDevice(licenseActivationRequest.getMacAddress(), user);

        Ticket ticket = licenseService.activateLicense(
                licenseActivationRequest.getActivationCode(),
                device,user
        );

        return ApiMessage.Success(ticket);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLicense(@Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest) throws Exception {
        Ticket ticket = licenseService.updateExistentLicense(
                licenseUpdateRequest.getLicenseCode(),
                licenseUpdateRequest.getLogin(),
                licenseUpdateRequest.getMacAddress()
        );

        return ApiMessage.Success(ticket);
    }
}
