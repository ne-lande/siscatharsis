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
public class AdminLicenseController {

    private final LicenseService licenseService;

    @Autowired
    public AdminLicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody LicenseCreateRequest requestDTO) {
        return ApiMessage.Success(licenseService.create(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        License license = licenseService.findById(id);

        return ApiMessage.Success(license);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseUpdateRequest requestDTO) {
        return ApiMessage.Success(licenseService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseService.deleteById(id);

        return ApiMessage.Success(id);
    }
}
