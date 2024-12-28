package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.internal.ProductRequest;
import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.repositories.ProductRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductRepository> {

    @Autowired
    public ProductService(ProductRepository repository) {
        super(repository, Product.class);
    }

    public Product create(ProductRequest productRequest) {
        return repository.save(
            new Product(
                productRequest.getName(),
                productRequest.isBlocked()
            )
        );
    }

    public Product update(Long id, ProductRequest productRequest) {
        Product product = this.findById(id);

        product.setName(productRequest.getName());
        product.setBlocked(productRequest.isBlocked());
        return repository.save(product);
    }
}
