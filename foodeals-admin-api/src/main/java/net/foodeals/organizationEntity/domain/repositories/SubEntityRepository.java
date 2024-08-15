package net.foodeals.organizationEntity.domain.repositories;

import net.foodeals.organizationEntity.domain.entities.SubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubEntityRepository extends JpaRepository<SubEntity, Long> {
}
