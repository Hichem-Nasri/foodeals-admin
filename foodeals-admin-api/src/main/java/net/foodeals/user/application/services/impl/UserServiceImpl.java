package net.foodeals.user.application.services.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.services.EmailService;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.repositories.AddressRepository;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.user.application.dtos.requests.UserAddress;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.dtos.responses.ClientDto;
import net.foodeals.user.application.dtos.responses.UserResponse;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.exceptions.UserNotFoundException;
import net.foodeals.user.domain.repositories.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final AddressService addressService;
    private final EmailService emailService;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<User> searchNonClientUsers(String query, Pageable pageable) {
        return repository.findByName_FirstNameContainingOrName_LastNameContainingAndRoleNameNot(
                query, query, "CLIENT", pageable);
    }


    @Override
    public Page<User> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public User findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public User create(UserRequest request) {
        final User user = mapRelationsAndEncodePassword(
                modelMapper.map(request, User.class),
                request
        );
        return this.repository.save(user);
    }

    @Override
    public User update(Integer id, UserRequest request) {
        final User existingUser = findById(id);
        modelMapper.map(request, existingUser);
        final User user = mapRelationsAndEncodePassword(
                existingUser, request
        );
        return repository.save(user);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id))
            throw new UserNotFoundException(id);
        repository.softDelete(id);
    }

    private User mapRelationsAndEncodePassword(User user, UserRequest request) {
        final Role role = roleService.findByName(request.roleName());
        user.setRole(role)
                .setPassword(passwordEncoder.encode(user.getPassword()));
        if (request.userAddress() != null) {
            Address address = this.addressService.createUserAddress(request.userAddress());
            user.setAddress(address);
        }
        user = this.repository.save(user);
        if (request.organizationEntityId() != null) {
            final OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(request.organizationEntityId()).orElse(null);
            if (organizationEntity != null) {
                user.setOrganizationEntity(organizationEntity);
                organizationEntity.getUsers().add(user);
                this.organizationEntityRepository.save(organizationEntity);
            }
        }
        return user;
    }

    @Override
    public User save(User user) {
        return this.repository.save(user);
    }

    @Override
    public Page<User> getClientsData(Pageable page) {
        String roleName = "CLIENT";
        return this.repository.findByRoleName(roleName, page);
    }

    public ClientDto toClientDto(User user) {
        ClientDto client = this.modelMapper.map(user, ClientDto.class);

        client.setNumberOfCommands(user.getOrders().size());
        return client;
    }

    @Override
    public Long countDeliveryUsersByOrganizationId(UUID id) {
        return this.repository.countDeliveryUsersByOrganizationId(id);
    }

    @Override
    public Page<User> getOrganizationUsers(Pageable pageable, UUID id) {
                return this.repository.findByOrganizationEntity_Id(id, pageable);
    }

    @Override
    public User createOrganizationEntityUser(UserRequest userRequest) {
        User user = this.create(userRequest);
//        String receiver = user.getEmail();
//        String subject = "Foodeals account validation";
//        String message = "You're account has been validated\n Your email : " + user.getEmail() + " \n" + " Your password : " + userRequest.password();
//        this.emailService.sendEmail(receiver, subject, message);
        return user;
    }

    @Override
    public Integer countByRole(Role role) {
        return this.repository.countByRoleAndDeletedAtIsNull(role);
    }
}
