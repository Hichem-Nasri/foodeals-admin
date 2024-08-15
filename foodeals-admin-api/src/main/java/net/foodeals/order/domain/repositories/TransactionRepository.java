package net.foodeals.order.domain.repositories;

import net.foodeals.order.domain.entities.Transcation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transcation, UUID> {
}
