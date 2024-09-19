package net.foodeals.organizationEntity.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankInformation extends AbstractEntity<UUID> {

    @Id
    @UuidGenerator
    private UUID id;

    private String beneficiaryName;

    private String bankName;

    private String rib;
}
