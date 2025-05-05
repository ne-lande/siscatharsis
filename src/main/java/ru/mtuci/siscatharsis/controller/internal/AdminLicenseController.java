package ru.mtuci.siscatharsis.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.request.LicenseRequest;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminLicenseController {

    private final LicenseService licenseService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<?> create(Authentication authentication, @RequestBody LicenseRequest licenseCreateRequest) {
        User issuer = (User) authentication.getPrincipal();

        User owner = userService.requireById(licenseCreateRequest.getOwnerId());
        Product product = productService.requireById(licenseCreateRequest.getProductId());
        LicenseType licenseType = licenseTypeService.requireById(licenseCreateRequest.getTypeId());

        License license = License.builder()
                .owner(owner)
                .product(product)
                .type(licenseType)
                .endingDate(licenseCreateRequest.getEndingDate())
                .devicesCount(licenseType.getDeviceCount())
                .duration(licenseType.getDuration())
                .description(licenseType.getDescription())
                .build();

        licenseService.create(license, issuer);

        return ApiMessage.Success(license);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        License license = licenseService.requireById(id);

        return ApiMessage.Success(license);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Long id, @RequestBody LicenseRequest licenseUpdateRequest) {
        User issuer = (User) authentication.getPrincipal();

        User owner = userService.requireById(licenseUpdateRequest.getOwnerId());
        Product product = productService.requireById(licenseUpdateRequest.getProductId());
        LicenseType licenseType = licenseTypeService.requireById(licenseUpdateRequest.getTypeId());

        License license = License.builder()
                .owner(owner)
                .product(product)
                .type(licenseType)
                .endingDate(licenseUpdateRequest.getEndingDate())
                .devicesCount(licenseType.getDeviceCount())
                .duration(licenseType.getDuration())
                .description(licenseType.getDescription())
                .build();

        licenseService.update(id, license, issuer);

        return ApiMessage.Success(license);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        licenseService.delete(id);

        return ApiMessage.Success(id);
    }
}
