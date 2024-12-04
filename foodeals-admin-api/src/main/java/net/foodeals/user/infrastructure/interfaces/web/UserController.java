package net.foodeals.user.infrastructure.interfaces.web;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.application.dtos.requests.SubentityUsersResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final ModelMapper mapper;
    private final OrganizationEntityRepository organizationRepository;
    private final SubEntityRepository subEntityRepository;

    @GetMapping("organizations/search")
    public ResponseEntity<Page<SimpleUserDto>> searchUsersOrganizations(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "types") List<EntityType> types,
            Pageable pageable) {
        UserSearchOrganizationFilter filter = new UserSearchOrganizationFilter(name, types);
        Page<User> users = service.filterUsersOrganization(filter, pageable);
        Page<SimpleUserDto> userResponses = users.map(u -> this.mapper.map(u, SimpleUserDto.class));
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/subentities/search")
    public ResponseEntity<Page<SimpleUserDto>> searchUsersSubentities(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = true, name = "organizationId") UUID organizationId,
            Pageable pageable) {
        UserSearchSubentityFilters filter = new UserSearchSubentityFilters(name, organizationId);
        Page<User> users = service.filterUsersSubentity(filter, pageable);
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
    public ResponseEntity<OrganizationUsersResponse> getUsersByOrganization(
            @PathVariable("organizationId") UUID organizationId,
            @PageableDefault(size = 10) Pageable pageable,

            @RequestParam(value = "names", required = false) String names,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "entityTypes", required = true) List<EntityType> entityTypes,
            @RequestParam(value = "solutions", required = false) List<String> solutions,

            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "deletedAt", required = true) Boolean deletedAt
    ) {

        List<String> namesList = names != null
                ? Arrays.stream(names.split(","))
                .collect(Collectors.toList())
                : null;

        // Retrieve organization
        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + organizationId));

        // Create PartnerInfoDto
        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .avatarPath(organization.getAvatarPath())
                .city(organization.getAddress().getRegion().getCity().getName())
                .build();

        // Create filter
        UserFilter filter = UserFilter.builder()
                .names(namesList)
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

        // Get users
        Page<Object> userInfoPage = this.service.getUsersByOrganization(organizationId, filter, pageable);

        // Create and return response
        return ResponseEntity.ok(
                new OrganizationUsersResponse(partnerInfoDto, userInfoPage)
        );
    }

    @GetMapping("/subentities/{subentityId}")
    public ResponseEntity<SubentityUsersResponse> getSubentitiesUsers(
            @PathVariable("subentityId") UUID subentityId,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(value = "names", required = false) String names,
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

        SubEntity subEntity = subEntityRepository.findById(subentityId)
                .orElseThrow(() -> new EntityNotFoundException("Subentity not found with ID: " + subentityId));

        List<String> namesList = names != null
                ? Arrays.stream(names.split(","))
                .collect(Collectors.toList())
                : null;

        // Create PartnerInfoDto
        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder()
                .id(subEntity.getId())
                .name(subEntity.getName())
                .avatarPath(subEntity.getAvatarPath())
                .city(subEntity.getAddress().getRegion().getCity().getName())
                .build();
        UserFilter filter = UserFilter.builder()
                .names(namesList)
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
        return ResponseEntity.ok(new SubentityUsersResponse(partnerInfoDto, userInfoPage));
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
    public ResponseEntity<DeliveryUsersResponse> getDeliveryUsers(Pageable pageable, @PathVariable("id") UUID id) {

        // Retrieve organization
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with ID: " + id));

        // Create PartnerInfoDto
        PartnerInfoDto partnerInfoDto = PartnerInfoDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .avatarPath(organization.getAvatarPath())
                .city(organization.getAddress().getRegion().getCity().getName())
                .build();

        Page<User> users = this.service.getOrganizationUsers(pageable, id);
        Page<DeliveryPartnerUserDto> deliveyPartnerUsersDtos = users.map(user -> this.mapper.map(user, DeliveryPartnerUserDto.class));
        return new ResponseEntity<DeliveryUsersResponse>(new DeliveryUsersResponse(partnerInfoDto, deliveyPartnerUsersDtos), HttpStatus.OK);
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


    @GetMapping("/cities/organizations/search")
    public ResponseEntity<Page<CityResponse>> searchCitiesOrganizations(
            @RequestParam(name = "city") String cityName,
            @RequestParam(name = "organizationId") UUID organizationId,

            Pageable pageable) {
        return ResponseEntity.ok(service.findCitiesUsersByEntityTypeAndCityName(organizationId, cityName, pageable).map(city -> this.mapper.map(city, CityResponse.class)));
    }

    @GetMapping("/regions/organizations/search")
    public ResponseEntity<Page<RegionResponse>> searchRegionsOrganizations(
            @RequestParam(name = "region") String regionName,
            @RequestParam(name = "organizationId") UUID organizationId,

            Pageable pageable) {
        return ResponseEntity.ok(service.findRegionsUsersByEntityTypeAndRegionName(organizationId, regionName, pageable).map(region -> this.mapper.map(region, RegionResponse.class)));
    }

    @GetMapping("/cities/subentities/search")
    public ResponseEntity<Page<CityResponse>> SubentityUsersSearchCities(
            @RequestParam(name = "city") String cityName,
            @RequestParam(name = "subentityId") UUID subentityId,

            Pageable pageable) {
        return ResponseEntity.ok(service.findCitiesUsersBySubentityAndCityName(subentityId, cityName, pageable).map(city -> this.mapper.map(city, CityResponse.class)));
    }

    @GetMapping("/regions/subentities/search")
    public ResponseEntity<Page<RegionResponse>> searchRegionsSubentities(
            @RequestParam(name = "region") String regionName,
            @RequestParam(name = "subentityId") UUID subentityId,

            Pageable pageable) {
        return ResponseEntity.ok(service.findRegionsUsersBySubentityAndRegionName(subentityId, regionName, pageable).map(region -> this.mapper.map(region, RegionResponse.class)));
    }
}