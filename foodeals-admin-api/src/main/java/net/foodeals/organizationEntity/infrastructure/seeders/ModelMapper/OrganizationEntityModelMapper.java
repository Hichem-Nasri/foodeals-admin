package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.organizationEntity.application.dtos.responses.DeliveryPartnerDto;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;
import net.foodeals.organizationEntity.application.dtos.responses.enums.DistributionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrganizationEntityModelMapper {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private CountryService countryService;

    @PostConstruct
    private void postConstruct() {
        // Configure ModelMapper for simple property mappings
        mapper.addMappings(new PropertyMap<OrganizationEntity, OrganizationEntityDto>() {
            @Override
            protected void configure() {
                map(source.getAvatarPath(), destination.getAvatarPath());
                map(source.getName(), destination.getName());
                map(source.getContract().getContractStatus(), destination.getContractStatus());
            }
        });
    }

    public OrganizationEntityDto mapOrganizationEntity(OrganizationEntity source) {
        OrganizationEntityDto destination = mapper.map(source, OrganizationEntityDto.class);

        OffsetDateTime dateTime = OffsetDateTime.parse(source.getCreatedAt().toString());
        LocalDate date = dateTime.toLocalDate();

        destination.setCreatedAt(date.toString());

        Optional<Contact> firstContact = Optional.ofNullable(source.getContacts())
                .flatMap(list -> list.stream().findFirst());
//        Optional<User> manager = Optional.ofNullable(source.getContract().getUserContracts().getUser());
//
//        manager.ifPresent(salesManger -> {
//            destination.setManager(salesManger.getName().firstName() + " " + salesManger.getName().lastName());
//        });
        firstContact.ifPresent(contact -> {
            destination.setEmail(contact.getEmail());
            destination.setPhone(contact.getPhone());
        });

        return destination;
    }

    public DeliveryPartnerDto mapDeliveryPartners(OrganizationEntity organizationEntity) {
        OffsetDateTime dateTime = OffsetDateTime.parse(organizationEntity.getCreatedAt().toString());
        LocalDate date = dateTime.toLocalDate();

        DeliveryPartnerDto deliveryPartnerDto = DeliveryPartnerDto.builder().createdAt(date.toString())
                .build();

        PartnerInfoDto  partnerInfoDto = PartnerInfoDto.builder().name(organizationEntity.getName())
                .avatarPath(organizationEntity.getAvatarPath())
                .build();
        deliveryPartnerDto.setEntityType(organizationEntity.getType());
        deliveryPartnerDto.setPartnerInfoDto(partnerInfoDto);

        User manager = organizationEntity.getUsers().stream().filter(user -> user.getRole().getName().equals("MANAGER")).findFirst().orElse(null);

        ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().name(manager.getName())
                .avatarPath(manager.getAvatarPath())
                .phone(manager.getPhone())
                .email(manager.getEmail())
                .build();
        deliveryPartnerDto.setResponsibleInfoDto(responsibleInfoDto);

        Long numberOfDeliveryPeople = this.userService.countDeliveryUsersByOrganizationId(organizationEntity.getId());

        Long numberOfDeliveries = this.deliveryService.countDeliveriesByDeliveryPartner(organizationEntity.getId());

        deliveryPartnerDto.setNumberOfDeliveries(numberOfDeliveries);
        deliveryPartnerDto.setNumberOfDeliveryPeople(numberOfDeliveryPeople);
        List<String> solutionsNames = organizationEntity.getSolutions().stream().map(solution -> solution.getName()).toList();
        deliveryPartnerDto.setSolutions(solutionsNames);
        int numberOfCoveredCities = organizationEntity.getCoveredZones().stream().map(coveredZone -> coveredZone.getRegion().getCity().getName()).collect(Collectors.toSet()).size();
        int totalNumberOfCities = this.countryService.countTotalCitiesByCountryName(organizationEntity.getAddress().getCity().getState().getCountry().getName());

        DistributionType distribution = totalNumberOfCities == numberOfCoveredCities ? DistributionType.EVERYWHERE : DistributionType.MULTI_CITY;
        deliveryPartnerDto.setDistribution(distribution);
        return deliveryPartnerDto;
    }
}

