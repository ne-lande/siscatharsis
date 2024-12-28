package ru.mtuci.siscatharsis.controller;

import jakarta.validation.Valid;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.license.*;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.services.*;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import ru.mtuci.siscatharsis.utils.LicenseException;

//TODO: 1. Кажется, что есть лишние проверки аутентификации

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
    public ResponseEntity<?> getLicenseInfo(@Valid @RequestBody LicenseInfoRequest licenseInfoRequest) throws LicenseException, Exception {
        User user = userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());

        Device device = deviceService.findByMacAddressAndUser(licenseInfoRequest.getMacAddress(), user);

        List<License> activeLicenses = licenseService.getActiveLicensesForDevice(device);
        List<Ticket> tickets = new ArrayList<>();

        for (License activeLicense : activeLicenses) {
            tickets.add(licenseService.generateTicket(activeLicense, device));
        }

        return ApiMessage.Success(tickets);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody LicenseActivationRequest licenseActivationRequest) throws LicenseException, Exception {
        User user = userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());

        Device device = deviceService.registerOrUpdateDevice(licenseActivationRequest.getMacAddress(), user);

        Ticket ticket = licenseService.activateLicense(
                licenseActivationRequest.getActivationCode(),
                device,user
        );

        return ApiMessage.Success(ticket);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLicense(@Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest) throws LicenseException, Exception {
        Ticket ticket = licenseService.updateExistentLicense(
                licenseUpdateRequest.getLicenseCode(),
                licenseUpdateRequest.getLogin(),
                licenseUpdateRequest.getMacAddress()
        );

        return ApiMessage.Success(ticket);
    }
}
