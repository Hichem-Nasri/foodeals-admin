package net.foodeals.location.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.user.application.dtos.requests.UserAddress;

import java.util.UUID;

public interface AddressService extends CrudService<Address, UUID, AddressRequest> {
    Address updateContractAddress(Address address, EntityAddressDto entityAddressDto);

    Address createUserAddress(UserAddress userAddress);
}
