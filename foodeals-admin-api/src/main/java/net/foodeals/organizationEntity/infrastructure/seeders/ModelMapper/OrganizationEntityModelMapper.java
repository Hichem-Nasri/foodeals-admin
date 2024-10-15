package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import ch.qos.logback.core.net.AbstractSocketAppender;
import jakarta.annotation.PostConstruct;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.application.services.OfferService;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsDto;
import net.foodeals.organizationEntity.application.dtos.responses.DeliveryPartnerDto;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;
import net.foodeals.organizationEntity.application.dtos.responses.enums.DistributionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
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
    @Autowired
    private DonationService donationService;
    @Autowired
    private SubEntityService subEntityService;
    @Autowired
    private OfferService offerService;



    @PostConstruct
    private void postConstruct() {
        // Configure ModelMapper for simple property mappings

        mapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();

            OffsetDateTime dateTime = OffsetDateTime.parse(organizationEntity.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
            Optional<User> responsible = organizationEntity.getUsers().stream().filter(user -> user.getRole().getName().equals("MANAGER")).findFirst();

            ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().build();
            responsible.ifPresent(user -> {
                responsibleInfoDto.setName(user.getName());
                responsibleInfoDto.setAvatarPath(user.getAvatarPath());
                responsibleInfoDto.setPhone(user.getPhone());
                responsibleInfoDto.setEmail(user.getEmail());
            });
            List<String> solutions = organizationEntity.getSolutions().stream().map(solution -> solution.getName()).toList();
            String city = organizationEntity.getAddress().getRegion().getCity().getName();
            ContractStatus contractStatus = organizationEntity.getContract().getContractStatus();
            Integer users = organizationEntity.getUsers().size();
            Integer subEntities = this.subEntityService.countByOrganizationEntity_IdAndType(organizationEntity.getId(), SubEntityType.FOOD_BANK_SB);
            EntityType entityType = organizationEntity.getType();
            Integer donations = this.donationService.countByDonor_Id(organizationEntity.getId());
            Integer recovered = this.donationService.countByReceiver_Id(organizationEntity.getId());
            Integer associations = this.subEntityService.countByOrganizationEntity_IdAndType(organizationEntity.getId(), SubEntityType.FOOD_BANK_ASSOCIATION);
            return new AssociationsDto(date.toString(), partnerInfoDto, responsibleInfoDto, users, donations, recovered, subEntities, associations, contractStatus, city, solutions, entityType);
        }, OrganizationEntity.class, AssociationsDto.class);

        mapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();
            OrganizationEntityDto organizationEntityDto = new OrganizationEntityDto();
            organizationEntityDto.setContractStatus(organizationEntity.getContract().getContractStatus());

            OffsetDateTime dateTime = OffsetDateTime.parse(organizationEntity.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            organizationEntityDto.setCreatedAt(date.toString());
            organizationEntityDto.setId(organizationEntity.getId());
            Optional<Contact> firstContact = Optional.ofNullable(organizationEntity.getContacts())
                    .flatMap(list -> list.stream().findFirst());
            firstContact.ifPresent(contact -> {
                ContactDto contactDto = new ContactDto(contact.getName(), contact.getEmail(), contact.getPhone());
                organizationEntityDto.setContactDto(contactDto);
            });
            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
            organizationEntityDto.setPartnerInfoDto(partnerInfoDto);
            Long offers = this.offerService.countByPublisherId(organizationEntity.getId());
            Long orders = this.offerService.countOrdersByPublisherInfoId(organizationEntity.getId());
            Long users = Long.valueOf(organizationEntity.getUsers().size());
            Long subEntities = Long.valueOf(organizationEntity.getSubEntities().size());
            EntityType type = organizationEntity.getType();
            String city = organizationEntity.getAddress().getRegion().getCity().getName();
            List<String> solutions = organizationEntity.getSolutions().stream().map(s -> s.getName()).toList();
            organizationEntityDto.setOffers(offers);
            organizationEntityDto.setOrders(orders);
            organizationEntityDto.setUsers(users);
            organizationEntityDto.setSubEntities(subEntities);
            organizationEntityDto.setType(type);
            organizationEntityDto.setCity(city);
            organizationEntityDto.setSolutions(solutions);
            return  organizationEntityDto;
        }, OrganizationEntity.class, OrganizationEntityDto.class);
    }

    public OrganizationEntityDto mapOrganizationEntity(OrganizationEntity source) {
        return this.mapper.map(source, OrganizationEntityDto.class);
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
        int totalNumberOfCities = this.countryService.countTotalCitiesByCountryName(organizationEntity.getAddress().getRegion().getCity().getCountry().getName());

        DistributionType distribution = totalNumberOfCities == numberOfCoveredCities ? DistributionType.EVERYWHERE : DistributionType.MULTI_CITY;
        deliveryPartnerDto.setDistribution(distribution);
        return deliveryPartnerDto;
    }

    public AssociationsDto mapToAssociation(OrganizationEntity organizationEntity) {
        return this.mapper.map(organizationEntity, AssociationsDto.class);
    }
}

