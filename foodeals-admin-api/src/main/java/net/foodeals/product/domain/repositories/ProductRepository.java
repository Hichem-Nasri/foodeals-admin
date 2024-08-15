package net.foodeals.product.domain.repositories;

import net.foodeals.product.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
