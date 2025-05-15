package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.LicenseTypeCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.license.LicenseType;
import ru.mtuci.siscatharsis.services.license.LicenseTypeService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/license-type")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminLicenseTypeController {

    private final LicenseTypeService licenseTypeService;
    private final ResponseUtils responseUtils;

    // don't need read endpoints because they are public available in InfoController

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody LicenseTypeCreateUpdateRequest licenseTypeRequest) {
        LicenseType licenseType = licenseTypeFromDto(licenseTypeRequest);

        licenseTypeService.create(licenseType);

        return responseUtils.success(licenseType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseTypeCreateUpdateRequest licenseTypeRequest) {
        LicenseType licenseType = licenseTypeFromDto(licenseTypeRequest);

        licenseTypeService.update(id, licenseType);

        return responseUtils.success(licenseType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseTypeService.delete(id);

        return responseUtils.success(id);
    }

    private LicenseType licenseTypeFromDto(LicenseTypeCreateUpdateRequest dto) {
        return LicenseType.builder()
                .name(dto.name())
                .description(dto.description())
                .duration(dto.defaultDuration())
                .deviceCount(dto.defaultDeviceCount())
                .build();
    }
}
