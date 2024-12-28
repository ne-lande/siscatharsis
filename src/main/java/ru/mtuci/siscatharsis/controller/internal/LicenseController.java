package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.license.request.*;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LicenseController extends AbstractCRUDController<License, LicenseRepository, LicenseService> {

    @Autowired
    public LicenseController(LicenseService service) {
        super(service);
    }

    @PostMapping("/create")
        public ResponseEntity<?> create(@RequestBody LicenseCreateRequest requestDTO) {
            return ApiMessage.Success(service.create(requestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseUpdateRequest requestDTO) {
        return ApiMessage.Success(service.update(id, requestDTO));
    }
}
