package net.foodeals.user.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.user.application.dtos.responses.*;
import net.foodeals.user.domain.entities.Authority;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.context.DelegatingApplicationListener;

@Configuration
@RequiredArgsConstructor
public class UserModelMapperConfig {

    private final ModelMapper modelMapper;
    private final DelegatingApplicationListener delegatingApplicationListener;

    @PostConstruct
    public void configureModelMapper() {
        modelMapper.addConverter(context -> {
            final Authority authority = context.getSource();
            return new AuthorityResponse(authority.getId(), authority.getName(), authority.getValue());
        }, Authority.class, AuthorityResponse.class);

        modelMapper.addConverter(context -> {
            final Role role = context.getSource();
            final var authorities = role.getAuthorities()
                    .stream()
                    .map(authority -> modelMapper.map(authority, AuthorityResponse.class))
                    .toList();
            return new RoleResponse(role.getId(), role.getName(), authorities);
        }, Role.class, RoleResponse.class);

        modelMapper.addConverter(context -> {
            final User user = context.getSource();
            final RoleResponse roleResponse = modelMapper.map(user.getRole(), RoleResponse.class);
            System.out.println("user converter is working");
            System.out.println(user.getRole());

            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), roleResponse, user.getOrganizationEntity().getId());
        }, User.class, UserResponse.class);

        modelMapper.addMappings(new PropertyMap<User, ClientDto>() {
            @Override
            protected void configure() {
                map(source.getEmail(), destination.getEmail());
                map(source.getAccount().getProvider(), destination.getAccountProvider());
                map(source.getPhone(), destination.getPhoneNumber());
                map(source.getIsEmailVerified(), destination.isAccountVerified());
                map(source.getStatus(), destination.getAccountStatus());
                map(source.getAddress(), destination.getClientAddressDto());
            }
        });

        modelMapper.addMappings(new PropertyMap<Address, ClientAddressDto>() {
            @Override
            protected void configure() {
                map(source.getAddress(), destination.getAddress());
                map(source.getCity().getName(), destination.getCity());
                map(source.getCity().getState().getName(), destination.getState());
                map(source.getCity().getState().getCountry().getName(), destination.getCountry());
            }
        });
    }
}
