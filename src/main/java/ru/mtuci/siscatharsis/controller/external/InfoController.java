package ru.mtuci.siscatharsis.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;

    @GetMapping("/license-type/{id}")
    public ResponseEntity<?> fetchLicenseTypeById(@PathVariable Long id) {
        return ApiMessage.Success(licenseTypeService.requireById(id));
    }

    @GetMapping("/license-type/all")
    public ResponseEntity<?> fetchLicenseTypeAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiMessage.Success(licenseTypeService.getAllLicenseTypes(page, size));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> fetchProductById(@PathVariable Long id) {
        return ApiMessage.Success(productService.requireById(id));
    }

    @GetMapping("/product/all")
    public ResponseEntity<?> fetchProductAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ApiMessage.Success(productService.getAllProducts(page, size));
    }
}
