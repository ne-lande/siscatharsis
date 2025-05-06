package ru.mtuci.siscatharsis.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.license.LicenseActivationRequest;
import ru.mtuci.siscatharsis.dto.license.LicenseInfoRequest;
import ru.mtuci.siscatharsis.dto.license.LicenseRenewalRequest;
import ru.mtuci.siscatharsis.dto.license.LicenseResponse;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentLicense(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // TODO: yet to be implemented

        return ApiMessage.Secret("hoi");
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(Authentication authentication, @Valid @RequestBody LicenseInfoRequest licenseInfoRequest) throws Exception {
        User user = (User) authentication.getPrincipal();

        Device device = deviceService.requireUserDevice(licenseInfoRequest.macAddress(), user);

        List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device);
        List<LicenseResponse> tickets = new ArrayList<>();

        for (License activeLicense : activeLicenses) {
            tickets.add(licenseService.generateTicket(activeLicense, device));
        }

        return ApiMessage.Success(tickets);
    }

    // TODO: extract ticket generation in controller
    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(Authentication authentication, @Valid @RequestBody LicenseActivationRequest licenseActivationRequest) throws Exception {
        UUID activationCode = licenseActivationRequest.activationCode();

        User user = (User) authentication.getPrincipal();

        String macAddress = licenseActivationRequest.macAddress();

        Device device = deviceService.registerOrUpdateDevice(licenseActivationRequest.macAddress(), user);

        LicenseResponse ticket = licenseService.activateLicense(
                licenseActivationRequest.activationCode(),
                device, user
        );

        return ApiMessage.Success(ticket);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLicense(Authentication authentication, @Valid @RequestBody LicenseRenewalRequest licenseUpdateRequest) throws Exception {
        User user = (User) authentication.getPrincipal();

        String macAddress = licenseUpdateRequest.macAddress();

        Device device = deviceService.requireUserDevice(macAddress, user);

        UUID licenseCode = licenseUpdateRequest.licenseCode();

        LicenseResponse ticket = licenseService.updateExistentLicense(licenseCode, user, device);

        return ApiMessage.Success(ticket);
    }
}
