package net.foodeals.offer.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.offer.domain.valueObject.Offerable;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.organizationEntity.domain.entities.Activity;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offers")

@Getter
@Setter
public class Offer extends AbstractEntity<UUID> {

        @Id
        @GeneratedValue
        @UuidGenerator
        private UUID id;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
                        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
        })
        private Price price;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "amount", column = @Column(name = "sale_price_amount")),
                        @AttributeOverride(name = "currency", column = @Column(name = "sale_price_currency"))
        })
        private Price salePrice;

        private Integer reduction;

        private String barcode;

        @OneToMany(fetch = FetchType.EAGER, mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<OpenTime> openTime;

        @ManyToOne(cascade = CascadeType.ALL)
        private Activity activity;

        @Embedded
        private Offerable offerable;

        @OneToMany(fetch = FetchType.EAGER, mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Order> orders;

        @Transient
        private OfferChoice offerChoice;
}
