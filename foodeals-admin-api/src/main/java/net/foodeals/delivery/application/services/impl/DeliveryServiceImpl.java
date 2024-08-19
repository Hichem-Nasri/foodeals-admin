package net.foodeals.delivery.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.delivery.application.dtos.requests.delivery.DeliveryRequest;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.delivery.domain.exceptions.DeliveryNotFoundException;
import net.foodeals.delivery.domain.repositories.DeliveryRepository;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * DeliveryServiceImpl
 */
@Service
@Transactional
@RequiredArgsConstructor
class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Override
    public List<Delivery> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Delivery> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Delivery findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }

    @Override
    public Delivery create(DeliveryRequest request) {
        User deliveryBoy = userService.findById(request.deliveryBoId());
        Delivery delivery = mapper.map(request, Delivery.class);
        delivery.setDeliveryBoy(deliveryBoy);
        // TODO assign orders and delivery postions
        return repository.save(delivery);
    }

    @Override
    public Delivery update(UUID id, DeliveryRequest dto) {
        User deliveryBoy = userService.findById(dto.deliveryBoId());
        Delivery delivery = findById(id);
        mapper.map(dto, delivery);
        delivery.setDeliveryBoy(deliveryBoy);
        // TODO assign orders and delivery postions
        return repository.save(delivery);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new DeliveryNotFoundException(id);
        repository.softDelete(id);
    }

}
