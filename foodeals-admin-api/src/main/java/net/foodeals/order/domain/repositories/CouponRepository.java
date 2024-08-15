package net.foodeals.order.domain.repositories;

import net.foodeals.order.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
}
