package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.DeviceLicenseRequest;
import ru.mtuci.siscatharsis.model.DeviceLicense;
import ru.mtuci.siscatharsis.services.DeviceLicenseService;
import ru.mtuci.siscatharsis.repositories.DeviceLicenseRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/device-license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DeviceLicenseController extends AbstractCRUDController<DeviceLicense, DeviceLicenseRepository, DeviceLicenseService>{

    @Autowired
    public DeviceLicenseController(DeviceLicenseService service) {
        super(service);
    }
}
