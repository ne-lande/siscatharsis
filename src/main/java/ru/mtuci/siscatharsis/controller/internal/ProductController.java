package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.request.ProductRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.repositories.ProductRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ProductController extends AbstractCRUDController<Product, ProductRepository, ProductService> {

    @Autowired
    public ProductController(ProductService service) {
        super(service);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductRequest requestDTO) {
        return ApiMessage.Success(service.create(requestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductRequest requestDTO) {
        return ApiMessage.Success(service.update(id, requestDTO));
    }
}
