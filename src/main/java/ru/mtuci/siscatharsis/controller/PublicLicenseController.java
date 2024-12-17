package ru.mtuci.siscatharsis.controller;

import jakarta.validation.Valid;
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

import java.util.Objects;

@RestController
@RequestMapping("/license")
public class PublicLicenseController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @Autowired
    public PublicLicenseController(UserService userService, DeviceService deviceService, LicenseService licenseService) {
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseService = licenseService;
    }

    @PostMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@Valid @RequestBody LicenseInfoRequest licenseInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Validation error: User is not authenticated");
        }

        try {
            User user = userService.findByLogin(authentication.getName());
            Device device = deviceService.findByMacAddressAndUser(licenseInfoRequest.getMacAddress(), user);
            License activeLicense = licenseService.getActiveLicenseForDevice(device, user, licenseInfoRequest.getLicenseCode());
            LicenseResponse ticket = licenseService.generateTicket(activeLicense, device);

            return ApiMessage.Success(ticket);
        } catch (IllegalArgumentException e) {
            return ApiMessage.ServerError(e.toString());
        } catch (Exception e) {
            return ApiMessage.ServerError(e.toString());
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody LicenseActivationRequest licenseActivationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiMessage.BadRequest("Validation error: User is not authenticated");
        }

        try {
            User user = userService.findByLogin(authentication.getName());
            LicenseResponse ticket = licenseService.activateLicense(
                licenseActivationRequest.getActivationCode(),
                deviceService.registerOrUpdateDevice(licenseActivationRequest, user),
                user.getLogin()
            );

            return ApiMessage.Success(ticket);
        } catch (LicenseException e) {
            return ApiMessage.BadRequest(e.toString());
        } catch (IllegalArgumentException e) {
            return ApiMessage.ServerError(e.toString());
        } catch (Exception e) {
            return ApiMessage.ServerError(e.toString());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLicense(@Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest) {
        try {
            LicenseResponse ticket = licenseService.updateExistentLicense(licenseUpdateRequest.getLicenseCode(), licenseUpdateRequest.getLogin(), licenseUpdateRequest.getMacAddress());

            return ApiMessage.Success(ticket);
        } catch (LicenseException e) {
            return ApiMessage.BadRequest(e.toString());
        } catch (IllegalArgumentException e) {
            return ApiMessage.ServerError(e.toString());
        } catch (Exception e) {
            return ApiMessage.ServerError(e.toString());
        }
    }
}
