package net.foodeals.user.infrastructure.interfaces.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.user.application.dtos.requests.UserFilter;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.dtos.responses.*;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.desktop.SystemEventListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final ModelMapper mapper;
    private final OrganizationEntityRepository organizationRepo;

    @GetMapping("/search")
    public ResponseEntity<Page<SimpleUserDto>> searchUsers(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "types") List<EntityType> types,
            Pageable pageable) {
        UserSearchFilter filter = new UserSearchFilter(name, types);
        Page<User> users = service.filterUsers(filter, pageable);
        Page<SimpleUserDto> userResponses = users.map(u -> this.mapper.map(u, SimpleUserDto.class));
        return ResponseEntity.ok(userResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer id,
            @RequestBody UpdateReason request) {
        service.deleteUser(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/deletion-details")
    public ResponseEntity<Page<UpdateDetails>> getDeletionDetails(@PathVariable Integer uuid, Pageable pageable) {
        Page<UpdateDetails> deletionDetails = service.getDeletionDetails(uuid, pageable);
        return ResponseEntity.ok(deletionDetails);
    }




        @GetMapping("/organizations/{organizationId}")
        public ResponseEntity<Page<UserInfoDto>> getUsersByOrganization(
                @PathVariable("organizationId") UUID organizationId,
                @PageableDefault(size = 10) Pageable pageable,

                @RequestParam(value = "names", required = false) List<String> names,
                @RequestParam(value = "phone", required = false) String phone,
                @RequestParam(value = "city", required = false) String city,
                @RequestParam(value = "region", required = false) String region,
                @RequestParam(value = "email", required = false) String email,
                @RequestParam(value = "roleName", required = false) String roleName,
                @RequestParam(value = "entityTypes", required = true) List<EntityType> entityTypes,
                @RequestParam(value = "solutions", required = false) List<String> solutions,

                @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                @RequestParam(value = "deletedAt", required = true)
                Boolean deletedAt
        ) {
            UserFilter filter = UserFilter.builder()
                    .names(names)
                    .phone(phone)
                    .city(city)
                    .region(region)
                    .email(email)
                    .roleName(roleName)
                    .entityTypes(entityTypes)
                    .solutions(solutions != null ? solutions : new ArrayList<String>())
                    .startDate(startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null)
                    .endDate(endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null)
                    .deletedAt(deletedAt)
                    .build();


            Page<UserInfoDto> userInfoPage = this.service.getUsersByOrganization(organizationId, filter, pageable);
            return ResponseEntity.ok(userInfoPage);
        }

    @GetMapping("/subentities/{subentityId}")
    public ResponseEntity<Page<UserInfoDto>> getSubentitiesUsers(
            @PathVariable("subentityId") UUID subentityId,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(value = "names", required = false) List<String> names,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "subEntityTypes", required = true) List<SubEntityType> entityTypes,
            @RequestParam(value = "solutions", required = false) List<String> solutions,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "deletedAt", required = true)
            Boolean deletedAt
    ) {
        UserFilter filter = UserFilter.builder()
                .names(names)
                .phone(phone)
                .city(city)
                .region(region)
                .email(email)
                .roleName(roleName)
                .subEntityTypes(entityTypes)
                .solutions(solutions != null ? solutions : new ArrayList<String>())
                .startDate(startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null)
                .endDate(endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null)
                .deletedAt(deletedAt)
                .build();


        Page<UserInfoDto> userInfoPage = this.service.getUsersBySubEntity(subentityId, filter, pageable);
        return ResponseEntity.ok(userInfoPage);
    }

    @GetMapping("/sells-managers")
    public ResponseEntity<Page<SimpleUserDto>> sellsManagers(
            @RequestParam(required = false, name = "name") String name,
            Pageable pageable) {
        Page<User> users = service.getSellsManagers(name, pageable);
        Page<SimpleUserDto> userResponses = users.map(u -> this.mapper.map(u, SimpleUserDto.class));
        return ResponseEntity.ok(userResponses);
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        final List<UserResponse> responses = service.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page/{pageNumber}/{pageSize}")
    public ResponseEntity<Page<UserResponse>> getAll(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        final Page<UserResponse> responses = service.findAll(pageNumber, pageSize)
                .map(user -> mapper.map(user, UserResponse.class));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        final UserResponse response = mapper.map(service.findById(id), UserResponse.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
        final UserResponse response = mapper.map(service.findByEmail(email), UserResponse.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest request) {
        final UserResponse response = mapper.map(service.create(request), UserResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody @Valid UserRequest request) {
        final UserResponse response = mapper.map(service.update(id, request), UserResponse.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clients")
    public ResponseEntity<Page<ClientDto>> getClientsData(Pageable page) {
        Page<User> clients = this.service.getClientsData(page);
        Page<ClientDto> clientDtos = clients.map(this.service::toClientDto);
        return new ResponseEntity<Page<ClientDto>>(clientDtos, HttpStatus.OK);
    }

    @GetMapping("/delivery-partners/{id}")
    public ResponseEntity<Page<DeliveryPartnerUserDto>> getCompanyUsers(Pageable pageable, @PathVariable("id") UUID id) {
        Page<User> users = this.service.getOrganizationUsers(pageable, id);
        Page<DeliveryPartnerUserDto> deliveyPartnerUsersDtos = users.map(user -> this.mapper.map(user, DeliveryPartnerUserDto.class));
        return new ResponseEntity<Page<DeliveryPartnerUserDto>>(deliveyPartnerUsersDtos, HttpStatus.OK);
    }

    @GetMapping("/associations/{id}")
    public ResponseEntity<Page<AssociationsUsersDto>> getAssociationUsers(Pageable pageable, @PathVariable("id") UUID id) {
        Page<User> users = this.service.getOrganizationUsers(pageable, id);
        Page<AssociationsUsersDto> associationsUsersDtoPage = users.map(user -> this.mapper.map(user, AssociationsUsersDto.class));
        return new ResponseEntity<Page<AssociationsUsersDto>>(associationsUsersDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Integer userId) {
        User user = service.findById(userId);
        return ResponseEntity.ok(this.service.mapUserToUserProfileDTO(user));
    }

}