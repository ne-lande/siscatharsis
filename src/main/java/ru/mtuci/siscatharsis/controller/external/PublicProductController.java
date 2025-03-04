package ru.mtuci.siscatharsis.controller.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/product")
public class PublicProductController {

    private final ProductService productService;

    @Autowired
    public PublicProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ApiMessage.Success(productService.findById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll() {
        return ApiMessage.Success(productService.findAll());
    }
}
