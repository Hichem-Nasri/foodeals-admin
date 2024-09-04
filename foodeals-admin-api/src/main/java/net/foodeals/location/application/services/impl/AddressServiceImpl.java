package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.organizationEntity.application.dto.upload.EntityAddressDto;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.exceptions.AddressNotFoundException;
import net.foodeals.location.domain.repositories.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final CityService cityService;
    private final ModelMapper modelMapper;
    private final RegionService regionService;

    @Override
    public List<Address> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Address> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Address findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
    }

    @Override
    public Address create(AddressRequest request) {
        City city = cityService.findById(request.cityId());
        Address address = modelMapper.map(request, Address.class);
        address.setCity(city);
        return repository.save(address);
    }

    @Override
    public Address update(UUID id, AddressRequest request) {
        City city = cityService.findById(request.cityId());
        Address address = findById(id);
        modelMapper.map(request, address);
        address.setCity(city);
        return repository.save(address);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new AddressNotFoundException(id);
        repository.softDelete(id);
    }

    @Transactional
    public Address updateContractAddress(Address address, EntityAddressDto entityAddressDto) {
        if (entityAddressDto.getAddress() != null) {
            address.setAddress(entityAddressDto.getAddress());
        }
        if (entityAddressDto.getCity() != null) {
            City city = this.cityService.findByName(entityAddressDto.getCity());
            address.setCity(city);
        }
        if (entityAddressDto.getRegion() != null) {
            Region region = this.regionService.findByName(entityAddressDto.getRegion());
            address.setRegion(region);
        }
        return this.repository.save(address);
    }
}
