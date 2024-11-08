package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import ch.qos.logback.core.net.AbstractSocketAppender;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import net.foodeals.contract.application.DTo.upload.ContractCommissionDto;
import net.foodeals.contract.application.DTo.upload.ContractSubscriptionDto;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.SolutionContract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.delivery.application.services.DeliveryService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.application.services.OfferService;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityBankInformationDto;
import net.foodeals.organizationEntity.application.dtos.responses.*;
import net.foodeals.organizationEntity.application.dtos.responses.enums.DistributionType;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.valueObjects.Name;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.synth.SynthTextAreaUI;
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
    @Transactional
    private void postConstruct() {
        // Configure ModelMapper for simple property mappings

        mapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();
            OrganizationEntityFormData formData = new OrganizationEntityFormData();

            formData.setEntityType(organizationEntity.getType());
            formData.setEntityName(organizationEntity.getName());
            formData.setCommissionPayedBySubEntities(organizationEntity.getContract().isCommissionPayedBySubEntities());
            formData.setSubscriptionPayedBySubEntities(organizationEntity.getContract().isSubscriptionPayedBySubEntities());

            formData.setSolutions(organizationEntity.getSolutions().stream().map(Solution::getName).collect(Collectors.toList()));
            formData.setFeatures(organizationEntity.getFeatures().stream().map(Features::getName).collect(Collectors.toList()));

            formData.setCommercialNumber(organizationEntity.getCommercialNumber());
            formData.setManagerId(organizationEntity.getContract().getUserContracts().getUser().getId());

            formData.setActivities(organizationEntity.getActivities().stream().map(Activity::getName).collect(Collectors.toList()));
            formData.setMaxNumberOfSubEntities(organizationEntity.getContract().getMaxNumberOfSubEntities());
            formData.setMaxNumberOfAccounts(organizationEntity.getContract().getMaxNumberOfAccounts());
            formData.setMinimumReduction(organizationEntity.getContract().getMinimumReduction());
            formData.setOneSubscription(organizationEntity.getContract().isSingleSubscription());
            formData.setStatus(organizationEntity.getContract().getContractStatus());
            // Map EntityAddressDto
            formData.setEntityAddressDto(mapper.map(organizationEntity.getAddress(), EntityAddressDto.class));

            // Map ContactDto (assuming the first contact is the main contact)
            if (!organizationEntity.getContacts().isEmpty()) {
                formData.setContactDto(mapper.map(organizationEntity.getContacts().get(0), ContactDto.class));
            }

            // Map EntityBankInformationDto
            formData.setEntityBankInformationDto(mapper.map(organizationEntity.getBankInformation(), EntityBankInformationDto.class));

            // Map SolutionsContractDto
            List<SolutionsContractDto> solutionsContractDtos = organizationEntity.getContract().getSolutionContracts().stream()
                    .map(sc -> {
                        SolutionsContractDto dto = new SolutionsContractDto();
                        dto.setSolution(sc.getSolution().getName());

                        ContractSubscriptionDto subDto = new ContractSubscriptionDto();
                        subDto.setDuration(sc.getSubscription().getDuration());
                        subDto.setAnnualPayment(sc.getSubscription().getAmount().amount().floatValue());
                        subDto.setNumberOfDueDates(sc.getSubscription().getNumberOfDueDates());
                        dto.setContractSubscriptionDto(subDto);

                        if (sc.getSolution().getName().equals("pro_market")) {
                            ContractCommissionDto comDto = new ContractCommissionDto();
                            comDto.setWithCard(sc.getCommission().getCard());
                            comDto.setWithCash(sc.getCommission().getCash());
                            dto.setContractCommissionDto(comDto);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            formData.setSolutionsContractDto(solutionsContractDtos);

            return formData;
        }, OrganizationEntity.class, OrganizationEntityFormData.class);

        mapper.addConverter(mappingContext -> {
            Address address = mappingContext.getSource();
            EntityAddressDto dto = new EntityAddressDto();

            dto.setAddress(address.getAddress());
            dto.setCountry(address.getRegion().getCity().getCountry().getName());
            dto.setCity(address.getRegion().getCity().getName());
            dto.setRegion(address.getRegion().getName());
            dto.setIframe(address.getIframe());

            return dto;
        }, Address.class, EntityAddressDto.class);

        mapper.addConverter(mappingContext -> {
            Contact contact = mappingContext.getSource();
            ContactDto dto = new ContactDto();

            dto.setName(contact.getName());
            dto.setEmail(contact.getEmail());
            dto.setPhone(contact.getPhone());

            return dto;
        }, Contact.class, ContactDto.class);

        mapper.addConverter(mappingContext -> {
            BankInformation bankInfo = mappingContext.getSource();
            EntityBankInformationDto dto = new EntityBankInformationDto();

            dto.setBeneficiaryName(bankInfo.getBeneficiaryName());
            dto.setBankName(bankInfo.getBankName());
            dto.setRib(bankInfo.getRib());

            return dto;
        }, BankInformation.class, EntityBankInformationDto.class);

// Additional converters for other complex mappings

        mapper.addConverter(mappingContext -> {
            Solution solution = mappingContext.getSource();
            return solution.getName();
        }, Solution.class, String.class);

        mapper.addConverter(mappingContext -> {
            Features feature = mappingContext.getSource();
            return feature.getName();
        }, Features.class, String.class);

        mapper.addConverter(mappingContext -> {
            Activity activity = mappingContext.getSource();
            return activity.getName();
        }, Activity.class, String.class);

// Converter for SolutionContract to SolutionsContractDto
        mapper.addConverter(mappingContext -> {
            SolutionContract sc = mappingContext.getSource();
            SolutionsContractDto dto = new SolutionsContractDto();

            dto.setSolution(sc.getSolution().getName());

            ContractSubscriptionDto subDto = new ContractSubscriptionDto();
            subDto.setDuration(sc.getSubscription().getDuration());
            subDto.setAnnualPayment(sc.getSubscription().getAmount().amount().floatValue());
            subDto.setNumberOfDueDates(sc.getSubscription().getNumberOfDueDates());
            dto.setContractSubscriptionDto(subDto);

            if (sc.getSolution().getName().equals("pro_market")) {
                ContractCommissionDto comDto = new ContractCommissionDto();
                comDto.setWithCard(sc.getCommission().getCard());
                comDto.setWithCash(sc.getCommission().getCash());
                dto.setContractCommissionDto(comDto);
            }

            return dto;
        }, SolutionContract.class, SolutionsContractDto.class);

// Additional converters for specific use cases

        mapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();

            OffsetDateTime dateTime = OffsetDateTime.parse(organizationEntity.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getId(), organizationEntity.getName(), organizationEntity.getAvatarPath());
            Optional<User> responsible = organizationEntity.getUsers().stream()
                    .filter(user -> user.getRole().getName().equals("MANAGER"))
                    .findFirst();

            ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().build();
            if (responsible.isPresent()) {
                User user = responsible.get();
                    responsibleInfoDto.setName(user.getName());
                    responsibleInfoDto.setAvatarPath(user.getAvatarPath());
                    responsibleInfoDto.setPhone(user.getPhone());
                    responsibleInfoDto.setEmail(user.getEmail());
            } else {
                Contact user = organizationEntity.getContacts().getFirst();
                responsibleInfoDto.setName(user.getName());
                responsibleInfoDto.setAvatarPath("");
                responsibleInfoDto.setPhone(user.getPhone());
                responsibleInfoDto.setEmail(user.getEmail());
            }


            List<String> solutions = organizationEntity.getSolutions().stream()
                    .map(solution -> solution.getName())
                    .collect(Collectors.toList());

            String city = organizationEntity.getAddress().getRegion().getCity().getName();
            ContractStatus contractStatus = organizationEntity.getContract().getContractStatus();
            Integer users = organizationEntity.getUsers().size();

            Integer subEntities = this.subEntityService.countByOrganizationEntity_IdAndType(
                    organizationEntity.getId(),
                    SubEntityType.FOOD_BANK_SB
            );

            EntityType entityType = organizationEntity.getType();
            Integer donations = this.donationService.countByDonor_Id(organizationEntity.getId());
            Integer recovered = this.donationService.countByReceiver_Id(organizationEntity.getId());

            Integer associations = this.subEntityService.countByOrganizationEntity_IdAndType(
                    organizationEntity.getId(),
                    SubEntityType.FOOD_BANK_ASSOCIATION
            );

            return new AssociationsDto(
                    date.toString(),
                    partnerInfoDto,
                    responsibleInfoDto,
                    users,
                    donations,
                    recovered,
                    subEntities,
                    associations,
                    contractStatus,
                    city,
                    solutions,
                    entityType
            );
        }, OrganizationEntity.class, AssociationsDto.class);

// You might need to add more converters for other specific mappings

// Don't forget to configure the mapper with these converters
        ModelMapper modelMapper = new ModelMapper();
// Add all the conver

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
            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getId(), organizationEntity.getName(), organizationEntity.getAvatarPath());
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

        mapper.addConverter(mappingContext -> {
            OrganizationEntity organizationEntity = mappingContext.getSource();
            DeliveryFormData formData = new DeliveryFormData();

            formData.setEntityType(organizationEntity.getType());
            formData.setEntityName(organizationEntity.getName());
            formData.setStatus(organizationEntity.getContract().getContractStatus());

            formData.setSolutions(organizationEntity.getSolutions().stream().map(Solution::getName).collect(Collectors.toList()));

            formData.setCommercialNumber(organizationEntity.getCommercialNumber());

            // Map EntityAddressDto
            formData.setEntityAddressDto(mapper.map(organizationEntity.getAddress(), EntityAddressDto.class));

            // Map ContactDto (assuming the first contact is the main contact)
            if (!organizationEntity.getContacts().isEmpty()) {
                formData.setContactDto(mapper.map(organizationEntity.getContacts().get(0), ContactDto.class));
            }

            // Map EntityBankInformationDto
            formData.setEntityBankInformationDto(mapper.map(organizationEntity.getBankInformation(), EntityBankInformationDto.class));

            // Map SolutionsContractDto
            List<SolutionsContractDto> solutionsContractDtos = organizationEntity.getContract().getSolutionContracts().stream()
                    .map(sc -> {
                        SolutionsContractDto dto = new SolutionsContractDto();
                        dto.setSolution(sc.getSolution().getName());

                        if (sc.getCommission() != null) {
                            ContractCommissionDto comDto = new ContractCommissionDto();
                            comDto.setDeliveryAmount(sc.getCommission().getDeliveryAmount());
                            comDto.setDeliveryCommission(sc.getCommission().getDeliveryCommission());
                            dto.setContractCommissionDto(comDto);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            formData.setSolutionsContractDto(solutionsContractDtos);
            formData.setCoveredZonesDtos(organizationEntity.getCoveredZonesDto());

            return formData;
        }, OrganizationEntity.class, DeliveryFormData.class);
    }

    @Transactional
    public OrganizationEntityDto mapOrganizationEntity(OrganizationEntity source) {
        return this.mapper.map(source, OrganizationEntityDto.class);
    }
    @Transactional
    public OrganizationEntityFormData convertToFormData(OrganizationEntity entity) {
        return mapper.map(entity, OrganizationEntityFormData.class);
    }

    @Transactional
    public DeliveryPartnerDto mapDeliveryPartners(OrganizationEntity organizationEntity) {
        OffsetDateTime dateTime = OffsetDateTime.parse(organizationEntity.getCreatedAt().toString());
        LocalDate date = dateTime.toLocalDate();

        DeliveryPartnerDto deliveryPartnerDto = DeliveryPartnerDto.builder().createdAt(date.toString())
                .build();

        PartnerInfoDto  partnerInfoDto = PartnerInfoDto.builder().id(organizationEntity.getId()).name(organizationEntity.getName())
                .avatarPath(organizationEntity.getAvatarPath())
                .build();
        deliveryPartnerDto.setId(organizationEntity.getId());
        deliveryPartnerDto.setEntityType(organizationEntity.getType());
        deliveryPartnerDto.setPartnerInfoDto(partnerInfoDto);

        User manager = organizationEntity.getUsers().stream().filter(user -> user.getRole().getName().equals("MANAGER")).findFirst().orElse(null);
        Contact contact = organizationEntity.getContacts().get(0);
        Name name = manager != null ? manager.getName() : contact.getName();
        String avatarPath = manager != null ? manager.getAvatarPath() : "";
        String phone = manager != null ? manager.getPhone() : contact.getPhone();
        String email = manager != null ? manager.getEmail() : contact.getEmail();

        ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().name(name)
                .avatarPath(avatarPath)
                .phone(phone)
                .email(email)
                .build();
        deliveryPartnerDto.setStatus(organizationEntity.getContract().getContractStatus());

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

    @Transactional
    public AssociationsDto mapToAssociation(OrganizationEntity organizationEntity) {
        return this.mapper.map(organizationEntity, AssociationsDto.class);
    }

    @Transactional
    public DeliveryFormData convertToDeliveryFormData(OrganizationEntity organizationEntity) {
        return  this.mapper.map(organizationEntity, DeliveryFormData.class);
    }
}