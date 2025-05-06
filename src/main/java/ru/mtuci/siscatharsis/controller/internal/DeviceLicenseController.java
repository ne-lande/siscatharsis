package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.services.DeviceLicenseService;

// это точно нужно
// как по-моему, сервисные сущности не должны поддерживать CRUD
@RestController
@RequestMapping("/admin/device-license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class DeviceLicenseController {

    private final DeviceLicenseService deviceLicenseService;


}
