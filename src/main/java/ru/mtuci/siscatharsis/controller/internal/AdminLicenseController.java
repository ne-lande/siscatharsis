package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.license.LicenseCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

@RestController
@RequestMapping("/admin/license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminLicenseController {

    private final LicenseService licenseService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;
    private final ApiConstructor apiConstructor;

    @PostMapping("/")
    public ResponseEntity<?> create(Authentication authentication, @RequestBody LicenseCreateUpdateRequest licenseRequest) {
        User issuer = (User) authentication.getPrincipal();

        License license = licenseFromDto(licenseRequest);

        licenseService.create(license, issuer);

        return apiConstructor.success(license);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        License license = licenseService.requireById(id);

        return apiConstructor.success(license);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Long id, @RequestBody LicenseCreateUpdateRequest licenseRequest) {
        User issuer = (User) authentication.getPrincipal();

        License license = licenseFromDto(licenseRequest);

        licenseService.update(id, license, issuer);

        return apiConstructor.success(license);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseService.delete(id);

        return apiConstructor.success(id);
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
