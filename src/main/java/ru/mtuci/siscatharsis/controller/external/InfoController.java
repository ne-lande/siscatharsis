package ru.mtuci.siscatharsis.controller.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/info")
public class InfoController {

    private final LicenseTypeService licenseTypeService;
    private final ProductService productService;

    @Autowired
    public InfoController(LicenseTypeService licenseTypeService, ProductService productService) {
        this.licenseTypeService = licenseTypeService;
        this.productService = productService;
    }

    @GetMapping("/license-type/{id}")
    public ResponseEntity<?> fetchLicenseTypeById(@PathVariable Long id) {
        return ApiMessage.Success(licenseTypeService.findById(id));
    }

    @GetMapping("/license-type/all")
    public ResponseEntity<?> fetchLicenseTypeAll() {
        return ApiMessage.Success(licenseTypeService.findAll());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> fetchProductById(@PathVariable Long id) {
        return ApiMessage.Success(productService.findById(id));
    }

    @GetMapping("/product/all")
    public ResponseEntity<?> fetchProductAll() {
        return ApiMessage.Success(productService.findAll());
    }
}
