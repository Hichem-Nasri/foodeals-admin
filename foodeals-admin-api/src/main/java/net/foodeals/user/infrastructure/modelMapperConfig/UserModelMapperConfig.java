package net.foodeals.user.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.user.application.dtos.responses.*;
import net.foodeals.user.domain.entities.Authority;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.valueObjects.Name;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.context.DelegatingApplicationListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        modelMapper.addConverter(context -> {
            final User user = context.getSource();
            System.out.println("SimpleUserDto converter is working");
            return new SimpleUserDto(
                    user.getId(),
                    new Name(user.getName().firstName(),
                    user.getName().lastName())
            );

        }, User.class, SimpleUserDto.class);

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

        modelMapper.addConverter(context -> {
            final User user = context.getSource();
            List<String> solutions = user.getOrganizationEntity().getSolutions().stream().map(solution -> solution.getName()).toList();
            String city = user.getAddress().getRegion().getCity().getName();
            String region = user.getAddress().getRegion().getName();
            UserInfoDto userInfoDto = new UserInfoDto(user.getName(), user.getAvatarPath(), user.getEmail(), user.getPhone());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/y");
            String createdAt = localDateTime.format(formatter);
            return new DeliveryPartnerUserDto(createdAt, user.getRole().getName(), "OFFLINE", city, region, solutions, userInfoDto);
        }, User.class, DeliveryPartnerUserDto.class);

        modelMapper.addConverter(context -> {
            final User user = context.getSource();
            String city = user.getAddress().getRegion().getCity().getName();
            String region = user.getAddress().getRegion().getName();
            UserInfoDto userInfoDto = new UserInfoDto(user.getName(), user.getAvatarPath(), user.getEmail(), user.getPhone());
            String roleName = user.getRole().getName();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/y");
            String createdAt = localDateTime.format(formatter);
            return new AssociationsUsersDto( user.getId(),createdAt, roleName, city, region, userInfoDto);
        }, User.class, AssociationsUsersDto.class);

        modelMapper.addMappings(new PropertyMap<Address, ClientAddressDto>() {
            @Override
            protected void configure() {
                map(source.getAddress(), destination.getAddress());
                map(source.getRegion().getCity().getName(), destination.getCity());
                map(source.getRegion().getName(), destination.getRegion());
                map(source.getRegion().getCity().getCountry().getName(), destination.getCountry());
            }
        });
    }
}
