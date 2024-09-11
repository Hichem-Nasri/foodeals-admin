package net.foodeals.payment.domain.repository;


import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
