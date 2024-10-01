package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Builder;
import lombok.Data;
import net.foodeals.organizationEntity.application.dtos.responses.enums.DistributionType;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;

@Data
@Builder
public class DeliveryPartnerDto {
    private String createdAt;

    private PartnerInfoDto partnerInfoDto;

    private EntityType entityType;

    private ResponsibleInfoDto responsibleInfoDto;

    private Long numberOfDeliveries;

    private Long numberOfDeliveryPeople;

    private DistributionType distribution;

    private List<String> solutions;
}
