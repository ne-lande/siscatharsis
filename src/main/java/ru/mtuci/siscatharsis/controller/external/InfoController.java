package ru.mtuci.siscatharsis.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;
    private final ApiConstructor apiConstructor;

    @GetMapping("/license-type/{id}")
    public ResponseEntity<?> fetchLicenseTypeById(@PathVariable Long id) {
        return apiConstructor.success(licenseTypeService.requireById(id));
    }

    @GetMapping("/license-type/all")
    public ResponseEntity<?> fetchLicenseTypeAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return apiConstructor.success(licenseTypeService.getAllLicenseTypes(page, size));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> fetchProductById(@PathVariable Long id) {
        return apiConstructor.success(productService.requireById(id));
    }

    @GetMapping("/product/all")
    public ResponseEntity<?> fetchProductAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return apiConstructor.success(productService.getAllProducts(page, size));
    }
}
