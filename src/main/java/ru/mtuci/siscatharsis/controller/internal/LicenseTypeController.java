package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.request.LicenseTypeRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/license-type")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;

    // don't need read endpoints because they are public available in InfoController

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody LicenseTypeRequest requestDTO) {
        LicenseType licenseType = LicenseType.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .duration(requestDTO.getDefaultDuration())
                .deviceCount(requestDTO.getDefaultDeviceCount())
                .build();

        licenseTypeService.create(licenseType);

        return ApiMessage.Success(licenseType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseTypeRequest requestDTO) {
        LicenseType licenseType = LicenseType.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .duration(requestDTO.getDefaultDuration())
                .deviceCount(requestDTO.getDefaultDeviceCount())
                .build();

        licenseTypeService.update(id, licenseType);

        return ApiMessage.Success(licenseType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseTypeService.delete(id);

        return ApiMessage.Success(id);
    }
}
