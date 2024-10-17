package net.foodeals.location.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.location.domain.exceptions.AddressNotFoundException;
import net.foodeals.location.domain.repositories.AddressRepository;
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

    @Override
    public Address create(AddressRequest request) {
        Country country = this.countryService.findByName(request.country().toLowerCase());
        City city = country.getCities().stream().filter(c -> c.getName().equals(request.cityName().toLowerCase())).findFirst().get();
        Region region = city.getRegions().stream().filter(r -> r.getName().equals(request.regionName().toLowerCase())).findFirst().get();
        Address address = modelMapper.map(request, Address.class);
        address.setRegion(region);
        return repository.save(address);
    }

    @Override
    public Address update(UUID id, AddressRequest request) {
        Address address = repository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
        Country country = this.countryService.findByName(request.country().toLowerCase());
        if (!country.getName().equals(address.getRegion().getCity().getCountry().getName())) {
            City city = country.getCities().stream().filter(c -> c.getName().equals(request.cityName().toLowerCase())).findFirst().get();
            Region region = city.getRegions().stream().filter(r -> r.getName().equals(request.regionName().toLowerCase())).findFirst().get();
            address.setRegion(region);
        } else if (!address.getRegion().getCity().getName().equals(request.cityName().toLowerCase())) {
            Country country1 = address.getRegion().getCity().getCountry();
            City city = country1.getCities().stream().filter(c -> c.getName().equals(request.cityName().toLowerCase())).findFirst().get();
            Region region = city.getRegions().stream().filter(r -> r.getName().equals(request.regionName().toLowerCase())).findFirst().get();
            address.setRegion(region);
        } else if (!address.getRegion().getName().equals(request.regionName().toLowerCase())) {
            City city = address.getRegion().getCity();
            Region region = city.getRegions().stream().filter(r -> r.getName().equals(request.regionName().toLowerCase())).findFirst().get();
            address.setRegion(region);
        }
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
            City city = country.getCities().stream().filter(c -> c.getName().equals(entityAddressDto.getCity().toLowerCase())).findFirst().get();
            Region region = city.getRegions().stream().filter(r -> r.getName().equals(entityAddressDto.getRegion().toLowerCase())).findFirst().get();
            address.setRegion(region);

        return this.repository.save(address);
    }

    @Override
    public Address createUserAddress(UserAddress userAddress) {
        Country country = this.countryService.findByName(userAddress.country().toLowerCase());
        City city = country.getCities().stream().filter(c -> c.getName().equals(userAddress.city().toLowerCase())).findFirst().get();
        Region region = city.getRegions().stream().filter(r -> r.getName().equals(userAddress.region().toLowerCase())).findFirst().get();

        Address address = Address.builder()
                .region(region)
                .build();
        return this.repository.save(address);
    }
}
