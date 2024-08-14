package net.foodeals.product.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.product.domain.enums.ProductType;
import net.foodeals.common.valueOjects.Price;

@Entity
@Table(name = "products")

@Getter
@Setter
public class Product extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private String slug;

    private String title;

    private String barcode;

    @Column(name = "product_type")
    private ProductType type;

    @Embedded
    private Price price;

    @Column(name = "product_image_type")
    private String ProductImagePath;

    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;
}
