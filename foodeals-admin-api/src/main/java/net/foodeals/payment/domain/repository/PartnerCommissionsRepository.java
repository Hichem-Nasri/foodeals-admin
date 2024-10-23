package net.foodeals.payment.domain.repository;


import net.foodeals.payment.domain.entities.PartnerCommissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PartnerCommissionsRepository extends JpaRepository<PartnerCommissions, UUID> {
}
