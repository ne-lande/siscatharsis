package ru.mtuci.siscatharsis.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.services.license.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;
    private final ResponseUtils responseUtils;

    @GetMapping("/license-type/{id}")
    public ResponseEntity<?> fetchLicenseTypeById(@PathVariable Long id) {
        return responseUtils.success(licenseTypeService.requireById(id));
    }

    @GetMapping("/license-type/all")
    public ResponseEntity<?> fetchLicenseTypeAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return responseUtils.success(licenseTypeService.getAllLicenseTypes(page, size));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> fetchProductById(@PathVariable Long id) {
        return responseUtils.success(productService.requireById(id));
    }

    @GetMapping("/product/all")
    public ResponseEntity<?> fetchProductAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return responseUtils.success(productService.getAllProducts(page, size));
    }
}
