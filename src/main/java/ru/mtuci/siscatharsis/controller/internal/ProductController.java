package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.internal.ProductRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.services.ProductService;
import ru.mtuci.siscatharsis.repositories.ProductRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ProductController extends AbstractCRUDController<Product, ProductRequest, ProductRepository, ProductService> {

    @Autowired
    public ProductController(ProductService service) {
        super(service);
    }
}
