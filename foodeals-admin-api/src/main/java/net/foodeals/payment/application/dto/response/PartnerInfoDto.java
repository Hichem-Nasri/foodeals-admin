package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.domain.entities.Enum.PartnerType;

@Data
@AllArgsConstructor
@Builder
public class PartnerInfoDto {
    private String name;

    private String avatarPath;
}
