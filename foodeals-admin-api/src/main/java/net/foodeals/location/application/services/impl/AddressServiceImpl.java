package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.*;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.domain.exceptions.AddressNotFoundException;
import net.foodeals.location.domain.repositories.AddressRepository;
import net.foodeals.organizationEntity.domain.exceptions.AssociationCreationException;
import net.foodeals.organizationEntity.domain.exceptions.AssociationUpdateException;
import net.foodeals.user.application.dtos.requests.UserAddress;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final CountryService countryService;
    private final CityService cityService;
    private final ModelMapper modelMapper;
    private final RegionServiceImpl regionServiceImpl;

    @Override
    public List<Address> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Address> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Address> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Address findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
    }

    @Transactional
    public Address create(AddressRequest request) {
        Country country = this.countryService.findByName(request.country().toLowerCase());
        if (country == null) {
            throw new AssociationCreationException("Country not found: " + request.country());
        }
        State state = country.getStates().stream()
                .filter(s -> s.getName().equals(request.stateName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationCreationException("State not found: " + request.stateName()));
        City city = state.getCities().stream()
                .filter(c -> c.getName().equals(request.cityName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationCreationException("City not found: " + request.cityName()));
        Region region = city.getRegions().stream()
                .filter(r -> r.getName().equals(request.regionName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationCreationException("Region not found: " + request.regionName()));
        Address address = modelMapper.map(request, Address.class);
        address.setRegion(region);
        return repository.save(address);
    }

    @Transactional
    @Override
    public Address update(UUID id, AddressRequest request) {
        Address address = repository.findById(id)
                .orElseThrow(() -> new AssociationUpdateException("Address not found"));
        Country country = this.countryService.findByName(request.country().toLowerCase());
        if (country == null) {
            throw new AssociationUpdateException("Country not found: " + request.country());
        }
        State state = country.getStates().stream()
                .filter(s -> s.getName().equals(request.stateName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationUpdateException("State not found: " + request.stateName()));
        City city = state.getCities().stream()
                .filter(c -> c.getName().equals(request.cityName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationUpdateException("City not found: " + request.cityName()));
        Region region = city.getRegions().stream()
                .filter(r -> r.getName().equals(request.regionName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AssociationUpdateException("Region not found: " + request.regionName()));
        address.setRegion(region);
        address.setAddress(request.address());
        address.setIframe(request.iframe());
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
            address.setAddress(entityAddressDto.getAddress());
            address.setIframe(entityAddressDto.getIframe());
            Country country = this.countryService.findByName(entityAddressDto.getCountry().toLowerCase());
            State state = country.getStates().stream().filter(s -> s.getName().equals(entityAddressDto.getState().toLowerCase())).findFirst().get();
            City city = state.getCities().stream().filter(c -> c.getName().equals(entityAddressDto.getCity().toLowerCase())).findFirst().get();
            Region region = city.getRegions().stream().filter(r -> r.getName().equals(entityAddressDto.getRegion().toLowerCase())).findFirst().get();
            address.setRegion(region);

        return this.repository.save(address);
    }

    @Override
    public Address createUserAddress(UserAddress userAddress) {
        Country country = this.countryService.findByName(userAddress.country().toLowerCase());
        State state = country.getStates().stream().filter(s -> s.getName().equals(userAddress.state().toLowerCase())).findFirst().get();
        City city = state.getCities().stream().filter(c -> c.getName().equals(userAddress.city().toLowerCase())).findFirst().get();
        Region region = city.getRegions().stream().filter(r -> r.getName().equals(userAddress.region().toLowerCase())).findFirst().get();

        Address address = Address.builder()
                .region(region)
                .build();
        return this.repository.save(address);
    }
}
