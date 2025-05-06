package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.Product;
import ru.mtuci.siscatharsis.repositories.ProductRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product requireById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found")
        );
    }

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product newProduct) {
        Product product = requireById(id);

        product.setName(newProduct.getName());
        product.setBlocked(newProduct.isBlocked());

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = requireById(id);

        productRepository.delete(product);
    }
}
