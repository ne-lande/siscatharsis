package ru.mtuci.siscatharsis.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.license.*;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.services.*;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.utils.LicenseException;

@RestController
@RequestMapping("/license")
public class PublicLicenseController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @Autowired
    public PublicLicenseController(
        UserService userService,
        DeviceService deviceService,
        LicenseService licenseService
    ) {
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseService = licenseService;
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(
        @Valid @RequestBody LicenseInfoRequest licenseInfoRequest
    ) throws LicenseException {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        User user = userService.findByLogin(authentication.getName());
        Device device = deviceService.findByMacAddressAndUser(
            licenseInfoRequest.getMacAddress(),
            user
        );
        License activeLicense = licenseService.getActiveLicenseForDevice(
            device,
            user,
            licenseInfoRequest.getLicenseCode()
        );
        LicenseResponse ticket = licenseService.generateLicenseResponse(
            activeLicense,
            device
        );

        return ApiMessage.Success(ticket);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(
        @Valid @RequestBody LicenseActivationRequest licenseActivationRequest
    ) throws LicenseException {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiMessage.BadRequest("User is not authenticated");
        }

        User user = userService.findByLogin(authentication.getName());
        LicenseResponse ticket = licenseService.activateLicense(
            licenseActivationRequest.getActivationCode(),
            deviceService.registerOrUpdateDevice(
                licenseActivationRequest,
                user
            ),
            user.getLogin()
        );

        return ApiMessage.Success(ticket);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLicense(
        @Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest
    ) throws LicenseException {
        LicenseResponse ticket = licenseService.updateExistentLicense(
            licenseUpdateRequest.getLicenseCode(),
            licenseUpdateRequest.getLogin(),
            licenseUpdateRequest.getMacAddress()
        );

        return ApiMessage.Success(ticket);
    }
}
