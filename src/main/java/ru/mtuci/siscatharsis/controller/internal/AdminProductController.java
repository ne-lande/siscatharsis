package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.ProductCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ResponseUtils responseUtils;

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = productFromDto(productRequest);

        productService.create(product);

        return responseUtils.success(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateUpdateRequest productRequest) {
        Product product = productFromDto(productRequest);

        productService.update(id, product);

        return responseUtils.success(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return responseUtils.success(id);
    }

    private Product productFromDto(ProductCreateUpdateRequest dto) {
        return Product.builder()
                .name(dto.name())
                .isBlocked(dto.isBlocked())
                .build();
    }
}
