package net.foodeals.user.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.common.entities.DeletionReason;
import net.foodeals.common.services.EmailService;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.application.dtos.requests.UserFilter;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.dtos.responses.*;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.entities.WorkingHours;
import net.foodeals.user.domain.exceptions.UserNotFoundException;
import net.foodeals.user.domain.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional
    public UserProfileDTO mapUserToUserProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setAvatarPath(user.getAvatarPath());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().getName());
        dto.setOrganization(user.getOrganizationEntity().getName());
        dto.setStatus(user.getStatus());
        dto.setGender(user.getGender());
        dto.setNationalId(user.getNationalId());
        dto.setNationality(user.getNationality());

        dto.setWorkingHours(mapWorkingHoursToDTO(user.getWorkingHours()));
        dto.setOrganizationInfo(mapUserToOrganizationInfo(user));

        return dto;
    }

    @Transactional
    public OrganizationInfo mapUserToOrganizationInfo(User user) {
        Contact responsibleContact = null;
        String phone = null;
        String email = null;

        PartnerInfoDto organizationPartner = user.getOrganizationEntity() != null
                ? new PartnerInfoDto(
                        user.getOrganizationEntity().getId(),
                        user.getOrganizationEntity().getName(),
                        user.getOrganizationEntity().getAvatarPath(),
                        user.getOrganizationEntity().getAddress().getRegion().getCity().getName()
                )
                : null;

        PartnerInfoDto subentityPartner = user.getSubEntity() != null
                ? new PartnerInfoDto(
                        user.getSubEntity().getId(),
                        user.getSubEntity().getName(),
                        user.getSubEntity().getAvatarPath(),
                        user.getSubEntity().getAddress().getRegion().getCity().getName()
                )
                : null;

/*         String rayon = user.getRayon() != null ? user.getRayon().getName() : null;
 */
        SimpleUserDto manager = user.getResponsible() != null
                ? new SimpleUserDto(
                        user.getResponsible().getId(),
                        user.getResponsible().getName(),
                        user.getResponsible().getAvatarPath()
                )
                : null;

        String city = user.getAddress() != null && user.getAddress().getRegion() != null
                ? user.getAddress().getRegion().getCity().getName()
                : null;

        String region = user.getAddress() != null && user.getAddress().getRegion() != null
                ? user.getAddress().getRegion().getName()
                : null;

        List<String> solutions = user.getSolutions() != null
                ? user.getSolutions().stream()
                        .map(Solution::getName)
                        .collect(Collectors.toList())
                : null;

        if (user.getSubEntity() != null && !user.getSubEntity().getContacts().isEmpty()) {
            responsibleContact = user.getSubEntity().getContacts().stream()
                    .filter(Contact::isResponsible)
                    .findFirst()
                    .orElse(user.getSubEntity().getContacts().get(0));
        } else if (user.getOrganizationEntity() != null && !user.getOrganizationEntity().getContacts().isEmpty()) {
            responsibleContact = user.getOrganizationEntity().getContacts().stream()
                    .filter(Contact::isResponsible)
                    .findFirst()
                    .orElse(user.getOrganizationEntity().getContacts().get(0));
        }

        if (responsibleContact != null) {
            phone = responsibleContact.getPhone();
            email = responsibleContact.getEmail();
        }

        return new OrganizationInfo(
                organizationPartner,
                subentityPartner,
                null,
                manager,
                city,
                region,
                solutions,
                phone,
                email
        );
    }

    @Transactional
    public List<WorkingHoursDTO> mapWorkingHoursToDTO(List<WorkingHours> workingHours) {
        return workingHours.stream()
                .map(wh -> {
                    WorkingHoursDTO dto = new WorkingHoursDTO();
                    dto.setDayOfWeek(wh.getDayOfWeek().name());
                    dto.setMorningStart(wh.getMorningStart());
                    dto.setMorningEnd(wh.getMorningEnd());
                    dto.setAfternoonStart(wh.getAfternoonStart());
                    dto.setAfternoonEnd(wh.getAfternoonEnd());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<Object> getUsersByOrganization(UUID organizationId, UserFilter filter, Pageable pageable) {
        Page<User> userPage = this.repository.findByOrganizationIdWithFilters(organizationId, filter, pageable);

        return userPage.map(user -> {
            if (filter.getEntityTypes() != null && filter.getEntityTypes().contains(EntityType.DELIVERY_PARTNER)) {
                return this.modelMapper.map(user, DeliveryPartnerUserDto.class);
            } else {
                LocalDateTime localDateTime = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/y");
                String createdAt = localDateTime.format(formatter);

                return new UserInfoDto(
                        createdAt,
                        user.getId(),
                        user.getRole().getName(),
                        user.getName(),
                        user.getAddress().getRegion().getCity().getName(),
                        user.getAddress().getRegion().getName(),
                        user.getAvatarPath(),
                        user.getEmail(),
                        user.getPhone());
            }
        });
    }

    @Override
    @Transactional
    public Page<UserInfoDto> getUsersBySubEntity(UUID subEntityId, UserFilter filter, Pageable pageable) {
        Page<User> userPage = this.repository.findBySubEntityIdWithFilters(subEntityId, filter, pageable);

        return userPage.map(user -> {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/y");
            String createdAt = localDateTime.format(formatter);

            return new UserInfoDto(
                    createdAt,
                    user.getId(),
                    user.getRole().getName(),
                    user.getName(),
                    user.getAddress().getRegion().getCity().getName(),
                    user.getAddress().getRegion().getName(),
                    user.getAvatarPath(),
                    user.getEmail(),
                    user.getPhone());
        });
    }

    @Override
    @Transactional
    public Page<User> getSellsManagers(String name, Pageable pageable) {
        // final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Optional<User> user = this.repository.findByEmail(email);
        // if (user.isPresent()) {
        //     return this.repository.getSellsManagers(user.get().getOrganizationEntity().getId(), name, "SALES_MANAGER", pageable);
        // } else {
        //     throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        // }
        return this.repository.findByRoleNameAndNameContaining("SALES_MANAGER", name, pageable);
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Page<User> filterUsersOrganization(UserSearchOrganizationFilter filter, Pageable pageable) {
        return repository.findWithFiltersOrganization(filter, pageable);
    }


    @Override
    @Transactional
    public Page<User> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    @Transactional
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public User findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional
    public User create(UserRequest request) {
        final User user = mapRelationsAndEncodePassword(
                modelMapper.map(request, User.class),
                request
        );
        return this.repository.save(user);
    }

    @Override
    @Transactional
    public User update(Integer id, UserRequest request) {
        final User existingUser = findById(id);
        modelMapper.map(request, existingUser);
        final User user = mapRelationsAndEncodePassword(
                existingUser, request
        );
        return repository.save(user);
    }

    @Override
    public void delete(Integer integer) {
    }
    @Override
    @Transactional
    public void delete(Integer id, UpdateReason reason) {
//        User user = findById(id);
//        user.markDeleted(reason);
//        this.repository.save(user);
    }

    @Transactional
    private User mapRelationsAndEncodePassword(User user, UserRequest request) {
        final Role role = roleService.findByName(request.roleName());
        user.setRole(role)
                .setPassword(passwordEncoder.encode(user.getPassword()));
        user = this.repository.save(user);
        if (request.organizationEntityId() != null) {
            final OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(request.organizationEntityId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found with id " + request.organizationEntityId()));
            if (organizationEntity != null) {
                user.setOrganizationEntity(organizationEntity);
                organizationEntity.getUsers().add(user);
                Address address = organizationEntity.getAddress();
                user.setAddress(address);
                this.organizationEntityRepository.save(organizationEntity);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Integer id, UpdateReason reason) {
        User user = this.repository.getEntity(id).orElseThrow(() -> new UserNotFoundException(id));
        DeletionReason deletionReason = DeletionReason.builder()
                .details(reason.details())
                .reason(reason.reason())
                .type(reason.action())
                .build();
        user.getDeletionReasons().add(deletionReason);
        user.markDeleted(reason.action());
        this.repository.save(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        return this.repository.save(user);
    }

    @Transactional
    @Override
    public Page<UpdateDetails> getDeletionDetails(Integer id, Pageable page) {
        User user = this.repository.getEntity(id).orElseThrow(() -> new UserNotFoundException(id));

        List<DeletionReason> deletionReasons = user.getDeletionReasons();

        int start = (int)page.getOffset();
        int end = Math.min(start + page.getPageSize(), deletionReasons.size());
        List<DeletionReason> content = deletionReasons.subList(start, end);

        return new PageImpl<>(content, page, deletionReasons.size()).map(d -> new UpdateDetails(d.getType(), d.getDetails(), d.getReason(), Date.from(d.getCreatedAt())));
    }

    @Override
    @Transactional
    public Page<City> findCitiesUsersByEntityTypeAndCityName(UUID id, String cityName, Pageable pageable) {
        return this.repository.findCitiesUsersByOrganizationIdAndCityName(id, cityName, pageable);
    }

    @Override
    @Transactional
    public Page<Region> findRegionsUsersByEntityTypeAndRegionName(UUID id, String regionName, Pageable pageable) {
        return this.repository.findRegionsUsersByOrganizationIdAndRegionName(id, regionName, pageable);
    }

    @Override
    @Transactional
    public Page<Region> findRegionsUsersBySubentityAndRegionName(UUID id, String regionName, Pageable pageable) {
        return this.repository.findRegionsUsersBySubentityIdAndRegionName(id, regionName, pageable);
    }

    @Override
    @Transactional
    public Page<User> filterUsersSubentity(UserSearchSubentityFilters filter, Pageable pageable) {
        return this.repository.findWithFiltersSubentity(filter, pageable);
    }

    @Override
    @Transactional
    public Page<City> findCitiesUsersBySubentityAndCityName(UUID id, String cityName, Pageable pageable) {
        return this.repository.findCitiesUsersBySubentityIdAndCityName(id, cityName, pageable);
    }

    @Override
    @Transactional
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
    @Transactional
    public Long countDeliveryUsersByOrganizationId(UUID id) {
        return this.repository.countDeliveryUsersByOrganizationId(id);
    }

    @Override
    @Transactional
    public Page<User> getOrganizationUsers(Pageable pageable, UUID id) {
                return this.repository.findByOrganizationEntity_Id(id, pageable);
    }

    @Override
    @Transactional
    public User createOrganizationEntityUser(UserRequest userRequest) {
        User user = this.create(userRequest);
//        String receiver = user.getEmail();
//        String subject = "Foodeals account validation";
//        String message = "You're account has been validated\n Your email : " + user.getEmail() + " \n" + " Your password : " + userRequest.password();
//        this.emailService.sendEmail(receiver, subject, message);
        return user;
    }

    @Override
    @Transactional
    public Integer countByRole(Role role) {
        return this.repository.countByRoleAndDeletedAtIsNull(role);
    }
}