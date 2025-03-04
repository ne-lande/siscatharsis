package ru.mtuci.siscatharsis.controller.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/license-type")
public class PublicLicenseTypeController {

    private final LicenseTypeService licenseTypeService;

    @Autowired
    public PublicLicenseTypeController(LicenseTypeService licenseTypeService) {
        this.licenseTypeService = licenseTypeService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ApiMessage.Success(licenseTypeService.findById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll() {
        return ApiMessage.Success(licenseTypeService.findAll());
    }
}
