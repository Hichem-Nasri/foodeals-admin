package net.foodeals.delivery.application.services.impl;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import net.foodeals.common.annotations.UseCase;
import net.foodeals.delivery.application.dtos.requests.DeliveryPositionRequest;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.delivery.application.services.AddNewDeliveryPositionToDelivery;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.delivery.domain.entities.DeliveryPosition;
import net.foodeals.delivery.domain.repositories.DeliveryPositionRepository;

/**
 * AddNewDeliveryPositionToDeliveryUsecaseImpl
 */
@UseCase
@RequiredArgsConstructor
class AddNewDeliveryPositionToDeliveryImpl implements AddNewDeliveryPositionToDelivery {

    private final DeliveryPositionRepository repository;
    private final DeliveryService deliverService;
    private final ModelMapper mapper;

    @Override
    public DeliveryPosition execute(DeliveryPositionRequest request) {
        Delivery delivery = deliverService.findById(request.deliveryId());
        DeliveryPosition deliveryPosition = mapper.map(request, DeliveryPosition.class);
        deliveryPosition.setDelivery(delivery);
        return repository.save(deliveryPosition);
    }
}
