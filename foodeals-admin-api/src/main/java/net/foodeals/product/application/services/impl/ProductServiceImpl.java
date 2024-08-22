package net.foodeals.product.application.services.impl;

import net.foodeals.product.application.dtos.requests.ProductRequest;
import net.foodeals.product.application.services.ProductService;
import net.foodeals.product.domain.entities.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public class ProductServiceImpl implements ProductService {
    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public Page<Product> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Product findById(UUID uuid) {
        return null;
    }

    @Override
    public Product create(ProductRequest dto) {
        return null;
    }

    @Override
    public Product update(UUID uuid, ProductRequest dto) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {

    }
}
