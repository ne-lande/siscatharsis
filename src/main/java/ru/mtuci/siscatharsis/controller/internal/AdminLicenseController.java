package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.license.LicenseCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.model.license.License;
import ru.mtuci.siscatharsis.model.license.LicenseHistory;
import ru.mtuci.siscatharsis.model.license.LicenseType;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.*;
import ru.mtuci.siscatharsis.services.license.LicenseHistoryService;
import ru.mtuci.siscatharsis.services.license.LicenseService;
import ru.mtuci.siscatharsis.services.license.LicenseTypeService;
import ru.mtuci.siscatharsis.services.user.UserService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

import java.util.ArrayList;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminLicenseController {

    private final LicenseService licenseService;
    private final LicenseHistoryService licenseHistoryService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;
    private final ResponseUtils responseUtils;

    @PostMapping("/")
    public ResponseEntity<?> create(Authentication authentication, @RequestBody LicenseCreateUpdateRequest licenseRequest) {
        User issuer = (User) authentication.getPrincipal();

        License license = licenseFromDto(licenseRequest);

        licenseService.create(license, issuer);

        return responseUtils.success(license);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        License license = licenseService.requireById(id);

        return responseUtils.success(license);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Long id, @RequestBody LicenseCreateUpdateRequest licenseRequest) {
        User issuer = (User) authentication.getPrincipal();

        License license = licenseFromDto(licenseRequest);

        licenseService.update(id, license, issuer);

        return responseUtils.success(license);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseService.delete(id);

        return responseUtils.success(id);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> readHistory(@PathVariable Long id) {
        License license = licenseService.requireById(id);

        ArrayList<LicenseHistory> licenseHistory = licenseHistoryService.findForLicense(license);

        return responseUtils.success(licenseHistory);
    }

    private License licenseFromDto(LicenseCreateUpdateRequest dto) {
        User owner = userService.requireById(dto.ownerId());
        Product product = productService.requireById(dto.productId());
        LicenseType licenseType = licenseTypeService.requireById(dto.typeId());

        return License.builder()
                .owner(owner)
                .product(product)
                .type(licenseType)
                .endingDate(dto.endingDate())
                .devicesCount(licenseType.getDeviceCount())
                .duration(licenseType.getDuration())
                .description(licenseType.getDescription())
                .build();
    }
}
