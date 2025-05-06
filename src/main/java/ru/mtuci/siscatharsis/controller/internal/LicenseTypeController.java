package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.LicenseTypeCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

@RestController
@RequestMapping("/admin/license-type")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;
    private final ApiConstructor apiConstructor;

    // don't need read endpoints because they are public available in InfoController

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody LicenseTypeCreateUpdateRequest licenseTypeRequest) {
        LicenseType licenseType = licenseTypeFromDto(licenseTypeRequest);

        licenseTypeService.create(licenseType);

        return apiConstructor.success(licenseType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseTypeCreateUpdateRequest licenseTypeRequest) {
        LicenseType licenseType = licenseTypeFromDto(licenseTypeRequest);

        licenseTypeService.update(id, licenseType);

        return apiConstructor.success(licenseType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseTypeService.delete(id);

        return apiConstructor.success(id);
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
