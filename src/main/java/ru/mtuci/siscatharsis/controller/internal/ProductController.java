package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.ProductCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> readAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getAllProducts(page, size);

        return ApiMessage.Success(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readProduct(@PathVariable Long id) {
        Product product = productService.requireById(id);

        return ApiMessage.Success(product);
    }

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .isBlocked(productRequest.isBlocked())
                .build();

        productService.create(product);

        return ApiMessage.Success(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .isBlocked(productRequest.isBlocked())
                .build();

        productService.update(id, product);

        return ApiMessage.Success(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return ApiMessage.Success(id);
    }
}
