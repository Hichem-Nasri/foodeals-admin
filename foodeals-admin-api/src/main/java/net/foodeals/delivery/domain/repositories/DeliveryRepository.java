package net.foodeals.delivery.domain.repositories;

import java.util.Date;
import java.util.UUID;

import net.foodeals.common.contracts.BaseRepository;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository extends BaseRepository<Delivery, UUID> {
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.deliveryBoy.organizationEntity.id = :organizationEntityId AND d.status = DELIVERED")
    Long countDeliveriesByDeliveryPartner(@Param("organizationEntityId") UUID organizationEntityId);


    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.deliveryBoy.organizationEntity.id = :organizationEntityId " +
            "AND d.status = DELIVERED " +
            "AND EXTRACT(MONTH FROM CAST(d.createdAt AS timestamp)) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) " +
            "AND EXTRACT(YEAR FROM CAST(d.createdAt AS timestamp)) = EXTRACT(YEAR FROM CAST(:date AS timestamp))")
    Long countDeliveriesByDeliveryPartnerAndDate(@Param("organizationEntityId") UUID organizationEntityId, @Param("date") Date date);


}



