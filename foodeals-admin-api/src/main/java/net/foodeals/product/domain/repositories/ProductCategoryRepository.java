package net.foodeals.product.domain.repositories;

import net.foodeals.product.domain.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
