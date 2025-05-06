package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.ProductCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ApiConstructor;

@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ApiConstructor apiConstructor;

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = productFromDto(productRequest);

        productService.create(product);

        return apiConstructor.success(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = productFromDto(productRequest);

        productService.update(id, product);

        return apiConstructor.success(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return apiConstructor.success(id);
    }

    private Product productFromDto(ProductCreateUpdateRequest dto) {
        return Product.builder()
                .name(dto.name())
                .isBlocked(dto.isBlocked())
                .build();
    }
}
