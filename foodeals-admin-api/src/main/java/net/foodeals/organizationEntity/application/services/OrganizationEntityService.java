package net.foodeals.organizationEntity.application.services;

import com.lowagie.text.DocumentException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.common.services.EmailService;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.delivery.application.services.impl.CoveredZonesService;
import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.impl.RegionServiceImpl;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.application.dtos.requests.CoveredZonesDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.responses.DeletionDetailsDTO;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerInfo;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.processors.classes.DtoProcessor;
import net.foodeals.user.application.dtos.requests.UserAddress;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.entities.enums.DeletionReason;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OrganizationEntityService {

    private final OrganizationEntityRepository organizationEntityRepository;
    private final ContractService contractService;
    private final CityService cityService;
    private final RegionServiceImpl regionServiceImpl;
    private final ActivityService activityService;
    private final SolutionService solutionService;
    private final BankInformationService bankInformationService;
    private final AddressService addressService;
    private final ContactsService contactsService;
    private final UserService userService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final CoveredZonesService coveredZonesService;
    private final CountryService countryService;
    private final FeatureService featureService;
    private final DtoProcessor dtoProcessor;

    public OrganizationEntity save(OrganizationEntity organizationEntity) {
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void delete(OrganizationEntity organizationEntity) {
        this.organizationEntityRepository.softDelete(organizationEntity.getId());
    }

    public OrganizationEntity createAnewOrganizationEntity(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, MultipartFile logo, MultipartFile cover) throws DocumentException, IOException {
        try {
            dtoProcessor.processDto(createAnOrganizationEntityDto);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error ");
        }

        AddressRequest addressRequest = new AddressRequest(createAnOrganizationEntityDto.getEntityAddressDto().getCountry(), createAnOrganizationEntityDto.getEntityAddressDto().getAddress(), createAnOrganizationEntityDto.getEntityAddressDto().getCity(), createAnOrganizationEntityDto.getEntityAddressDto().getRegion(), createAnOrganizationEntityDto.getEntityAddressDto().getIframe());
        Address address = this.addressService.create(addressRequest);
        Contact contact = Contact.builder().name(createAnOrganizationEntityDto.getContactDto().getName())
                .email(createAnOrganizationEntityDto.getContactDto().getEmail())
                .phone(createAnOrganizationEntityDto.getContactDto().getPhone())
                .isResponsible(true)
                .build();
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(createAnOrganizationEntityDto.getSolutions());
        OrganizationEntity organizationEntity = OrganizationEntity.builder().name(createAnOrganizationEntityDto.getEntityName())
                .type(createAnOrganizationEntityDto.getEntityType())
                .solutions(solutions)
                .address(address)
                .build();
        OrganizationEntity finalOrganizationEntity = organizationEntity;
        solutions.forEach(solution -> {
            solution.getOrganizationEntities().add(finalOrganizationEntity);
            this.solutionService.save(solution);
        });
        contact.setOrganizationEntity(organizationEntity);
        List<Contact> contacts = organizationEntity.getContacts();
        contacts.add(contact);
        organizationEntity.setContacts(contacts);
        organizationEntity = this.organizationEntityRepository.save(organizationEntity);
        switch (organizationEntity.getType()) {
            case EntityType.PARTNER_WITH_SB:
            case EntityType.NORMAL_PARTNER:
                organizationEntity = savePartner(createAnOrganizationEntityDto, organizationEntity);
                break;
            case EntityType.DELIVERY_PARTNER :
                organizationEntity = saveDeliveryPartner(createAnOrganizationEntityDto, organizationEntity);
                break;
            default:
        }
        return organizationEntity;
    }

    private OrganizationEntity saveDeliveryPartner(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) {
        if (createAnOrganizationEntityDto.getCoveredZonesDtos() != null) {
            List<CoveredZonesDto> coveredZonesDtos = createAnOrganizationEntityDto.getCoveredZonesDtos();
            coveredZonesDtos.forEach(coveredZonesDto -> {
                List<String> regionsNames = coveredZonesDto.getRegions();
                regionsNames.forEach(regionName -> {
                    CoveredZones coveredZone = CoveredZones.builder().organizationEntity(organizationEntity)
                            .build();
                    Country country = this.countryService.findByName(coveredZonesDto.getCountry());
                    City city = country.getCities().stream().filter(c -> c.getName().equals(coveredZonesDto.getCity().toLowerCase())).findFirst().get();
                    Region region = city.getRegions().stream().filter(r -> r.getName().equals(regionName.toLowerCase())).findFirst().get();
                    coveredZone.setRegion(region);
                    this.coveredZonesService.save(coveredZone);
                    organizationEntity.getCoveredZones().add(coveredZone);
                });
            });
        }
        Contract contract = this.contractService.createDeliveryPartnerContract(organizationEntity, createAnOrganizationEntityDto);
        BankInformation bankInformation = BankInformation.builder().beneficiaryName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBeneficiaryName())
                .bankName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBankName())
                .rib(createAnOrganizationEntityDto.getEntityBankInformationDto().getRib())
                .build();
        organizationEntity.setBankInformation(bankInformation);
        organizationEntity.setCommercialNumber(createAnOrganizationEntityDto.getCommercialNumber());
        return this.organizationEntityRepository.save(organizationEntity);
    }

    private OrganizationEntity savePartner(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        BankInformation bankInformation = BankInformation.builder().beneficiaryName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBeneficiaryName())
                .bankName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBankName())
                .rib(createAnOrganizationEntityDto.getEntityBankInformationDto().getRib())
                .build();
        organizationEntity.setBankInformation(bankInformation);
        Set<Activity> activities = this.activityService.getActivitiesByName(createAnOrganizationEntityDto.getActivities());
        Set<Features> features = this.featureService.findFeaturesByNames(createAnOrganizationEntityDto.getFeatures());
        organizationEntity.setActivities(activities);
        organizationEntity.setFeatures(features);
        organizationEntity.setCommercialNumber(createAnOrganizationEntityDto.getCommercialNumber());
        activities.forEach(activity -> {
            activity.getOrganizationEntities().add(organizationEntity);
            this.activityService.save(activity);
        });
        features.forEach(feature -> {
            feature.getOrganizationEntities().add(organizationEntity);
            this.featureService.save(feature);
        });
        Contract contract = this.contractService.createPartnerContract(createAnOrganizationEntityDto, organizationEntity);
        organizationEntity.setContract(contract);
        return this.organizationEntityRepository.save(organizationEntity);
    }

    @Transactional
    public OrganizationEntity updateOrganizationEntity(UUID id, CreateAnOrganizationEntityDto updateOrganizationEntityDto) throws DocumentException, IOException {
        try {
            dtoProcessor.processDto(updateOrganizationEntityDto);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error ");
        }

        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "organization Entity not found with id " + id.toString()));

        Contract contract = organizationEntity.getContract();

        Contact contact = organizationEntity.getContacts().getFirst();
        contact = this.contactsService.update(contact, updateOrganizationEntityDto.getContactDto());

        organizationEntity.setName(updateOrganizationEntityDto.getEntityName());
        Address address = organizationEntity.getAddress();
        address = this.addressService.updateContractAddress(address, updateOrganizationEntityDto.getEntityAddressDto());
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(updateOrganizationEntityDto.getSolutions());
        Set<Solution> partnerSolutions = new HashSet<>(organizationEntity.getSolutions());
        OrganizationEntity finalOrganizationEntity = organizationEntity;
        partnerSolutions.stream().map((Solution solution) -> {
                    if (!updateOrganizationEntityDto.getSolutions().contains(solution.getName())) {
                        solution.getOrganizationEntities().remove(finalOrganizationEntity);
                        finalOrganizationEntity.getSolutions().remove(solution);
                        this.solutionService.save(solution);
                    }
                    return solution;
                }).toList();
        organizationEntity.setType(updateOrganizationEntityDto.getEntityType());
        OrganizationEntity finalOrganizationEntity1 = organizationEntity;
        solutions.stream().map(solution -> {
            solution.getOrganizationEntities().add(finalOrganizationEntity1);
            finalOrganizationEntity1.getSolutions().add(solution);
            this.solutionService.save(solution);
            return solution;
        }).toList();
        switch (organizationEntity.getType()) {
            case EntityType.PARTNER_WITH_SB:
            case EntityType.NORMAL_PARTNER:
                organizationEntity = updatePartner(updateOrganizationEntityDto, organizationEntity);
                break;
            case EntityType.DELIVERY_PARTNER :
                organizationEntity = updateDeliveryPartner(updateOrganizationEntityDto, organizationEntity);
                break;
            default:
        }
        return organizationEntity;
    }

    @Transactional
    private OrganizationEntity updateDeliveryPartner(CreateAnOrganizationEntityDto updateOrganizationEntityDto, OrganizationEntity organizationEntity) {
        if (updateOrganizationEntityDto.getCoveredZonesDtos() != null) {
            organizationEntity.getCoveredZones().clear();
            for (CoveredZonesDto coveredZonesDto : updateOrganizationEntityDto.getCoveredZonesDtos()) {
                for (String regionName : coveredZonesDto.getRegions()) {
                    CoveredZones coveredZone = CoveredZones.builder().organizationEntity(organizationEntity).build();
                    Country country = this.countryService.findByName(coveredZonesDto.getCountry());
                    City city = country.getCities().stream().filter(c -> c.getName().equalsIgnoreCase(coveredZonesDto.getCity())).findFirst().orElseThrow(() -> new RuntimeException("City not found"));
                    Region region = city.getRegions().stream().filter(r -> r.getName().equalsIgnoreCase(regionName)).findFirst().orElseThrow(() -> new RuntimeException("Region not found"));
                    coveredZone.setRegion(region);
                    this.coveredZonesService.save(coveredZone);
                    organizationEntity.getCoveredZones().add(coveredZone);
                }
            }
        }
        organizationEntity.setCommercialNumber(updateOrganizationEntityDto.getCommercialNumber());
        if (updateOrganizationEntityDto.getEntityBankInformationDto() != null) {
            BankInformation bankInformation = organizationEntity.getBankInformation();
            bankInformation = this.bankInformationService.update(bankInformation, updateOrganizationEntityDto.getEntityBankInformationDto());
            organizationEntity.setBankInformation(bankInformation);
        }
        Contract contract = this.contractService.updateDeliveryContract(organizationEntity.getContract(), updateOrganizationEntityDto);
        organizationEntity.setContract(contract);
        organizationEntity = this.organizationEntityRepository.save(organizationEntity);
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void deleteOrganization(UUID uuid, DeletionReason reason, String details) {
        OrganizationEntity organization = organizationEntityRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Organization not found with uuid: " + uuid));
        organization.markDeleted(reason, details);
        organizationEntityRepository.save(organization);
    }


    public Page<OrganizationEntity> getDeletedOrganizationsPaginated(Pageable pageable, EntityType type) {
        if (type != null) {
            return organizationEntityRepository.findByDeletedAtIsNotNullAndType(pageable, type);
        } else {
            return organizationEntityRepository.findByDeletedAtIsNotNull(pageable);
        }
    }

    public DeletionDetailsDTO getDeletionDetails(UUID uuid) {
        OrganizationEntity organization = organizationEntityRepository.findByIdAndDeletedAtIsNotNull(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Deleted organization not found with uuid: " + uuid));

        return new DeletionDetailsDTO(
                organization.getDeletionReason(),
                organization.getDeletionDetails(),
                organization.getDeletedAt()
        );
    }

    private OrganizationEntity updatePartner(CreateAnOrganizationEntityDto updateOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        List<String> activitiesNames = updateOrganizationEntityDto.getActivities();
        Set<Activity> activities = this.activityService.getActivitiesByName(activitiesNames);

        Set<Activity> activitiesToRemove = organizationEntity.getActivities()
                .stream()
                .filter(activity -> !activitiesNames.contains(activity.getName()))
                .collect(Collectors.toSet());

        Set<Activity> activitiesToAdd = activities.stream()
                .filter(activity -> !organizationEntity.getActivities().contains(activity))
                .collect(Collectors.toSet());

        activitiesToRemove.forEach(activity -> {
            activity.getOrganizationEntities().remove(organizationEntity);
            organizationEntity.getActivities().remove(activity);
            this.activityService.save(activity);
        });
        activitiesToAdd.forEach(activity -> {
            activity.getOrganizationEntities().add(organizationEntity);
            organizationEntity.getActivities().add(activity);
            this.activityService.save(activity);
        });

        Set<Features> newFeatures = this.featureService.findFeaturesByNames(updateOrganizationEntityDto.getFeatures());
        Iterator<Features> iterator = organizationEntity.getFeatures().iterator();
        while (iterator.hasNext()) {
            Features feature = iterator.next();
            if (!newFeatures.contains(feature)) {
                iterator.remove();
                feature.getOrganizationEntities().removeIf(org -> org.getId().equals(organizationEntity.getId())); // Remove from Feature
                this.featureService.save(feature);
            }
        }
        for (Features feature : newFeatures) {
            if (!organizationEntity.getFeatures().contains(feature)) {
                organizationEntity.getFeatures().add(feature);
                feature.getOrganizationEntities().add(organizationEntity);
                this.featureService.save(feature);
            }
        }

        organizationEntity.setCommercialNumber(updateOrganizationEntityDto.getCommercialNumber());
        if (updateOrganizationEntityDto.getEntityBankInformationDto() != null) {
            BankInformation bankInformation = organizationEntity.getBankInformation();
            bankInformation = this.bankInformationService.update(bankInformation, updateOrganizationEntityDto.getEntityBankInformationDto());
            organizationEntity.setBankInformation(bankInformation);
        }
        this.organizationEntityRepository.save(organizationEntity);
        Contract contract = organizationEntity.getContract();
        this.contractService.update(contract, updateOrganizationEntityDto);
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public OrganizationEntity getOrganizationEntityById(UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "organization Entity not found");
        }
        return organizationEntity;
    }

    @Transactional
    public Page<OrganizationEntity> getOrganizationEntities(List<EntityType> entityTypes, Pageable pageable) {
        return this.organizationEntityRepository.findByTypeIn(entityTypes, pageable);
    }

    @Transactional
    public String validateOrganizationEntity(UUID id, MultipartFile document) {
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization Entity not found");
        }
        Contact managerContact = organizationEntity.getContacts().getFirst();

        Role role  = this.roleService.findByName("MANAGER");
        String pass = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), false, "MANAGER", organizationEntity.getId(), userAddress);
        User manager = this.userService.create(userRequest);
        if (!organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER)) {
            Solution pro_market = this.solutionService.findByName("pro_market");
            if (organizationEntity.getSolutions().contains(pro_market)) {
                Date date = new Date();
                PartnerCommissions partnerCommissions = PartnerCommissions.builder()
                        .partnerInfo(new PartnerInfo(organizationEntity.getId(), organizationEntity.getId(), organizationEntity.getPartnerType()))
                        .paymentStatus(PaymentStatus.IN_VALID)
                        .paymentResponsibility(organizationEntity.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                        .date(date)
                        .build();
                organizationEntity.getCommissions().add(partnerCommissions);
            }
            this.contractService.validateContract(organizationEntity.getContract());
        }
        organizationEntity.getContract().setContractStatus(ContractStatus.VALIDATED);
        this.organizationEntityRepository.save(organizationEntity);

//        String receiver = manager.getEmail();
//        String subject = "Foodeals account validation";
//        String message = "You're account has been validated\n Your email : " + manager.getEmail() + " \n" + " Your password : " + pass;
//        this.emailService.sendEmail(receiver, subject, message);
        return "Contract validated successfully";
    }

    public byte[] getContractDocument(UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "organization Entity not found");
        }

        return this.contractService.getContractDocument(organizationEntity.getContract().getId());
    }

    public OrganizationEntity findById(UUID id) {
        return this.organizationEntityRepository.findById(id).orElse(null);
    }

    public Page<OrganizationEntity> getDeliveryPartners(Pageable pageable) {
        return this.organizationEntityRepository.findByType(EntityType.DELIVERY_PARTNER, pageable);
    }

    public UUID createAssociation(CreateAssociationDto createAssociationDto, MultipartFile logo, MultipartFile cover) {
        AddressRequest addressRequest = new AddressRequest(createAssociationDto.associationAddress().getCountry(), createAssociationDto.associationAddress().getAddress(), createAssociationDto.associationAddress().getCity(), createAssociationDto.associationAddress().getRegion(), createAssociationDto.associationAddress().getIframe());
        Address address = this.addressService.create(addressRequest);
        Set<Activity> activities = this.activityService.getActivitiesByName(createAssociationDto.activities());
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(createAssociationDto.solutions());
        OrganizationEntity organizationEntity = OrganizationEntity.builder().name(createAssociationDto.companyName())
                .activities(activities)
                .address(address)
                .type(createAssociationDto.entityType())
                .solutions(solutions)
                .commercialNumber(createAssociationDto.pv())
                .build();
        organizationEntity = this.organizationEntityRepository.save(organizationEntity);
        Contact manager1 = this.contactsService.create(createAssociationDto.manager1(), organizationEntity, true);
        Contact manager2 = this.contactsService.create(createAssociationDto.manager2(), organizationEntity, true);
        organizationEntity.getContacts().add(manager1);
        organizationEntity.getContacts().add(manager2);

        String pass1 = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress1 = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest1 = new UserRequest(manager1.getName(), manager1.getEmail(), manager1.getPhone(), pass1,  false, "MANAGER", organizationEntity.getId(), userAddress1);
        this.userService.createOrganizationEntityUser(userRequest1);

        String pass2 = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress2 = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest2 = new UserRequest(manager2.getName(), manager2.getEmail(), manager2.getPhone(), pass2,  false, "MANAGER", organizationEntity.getId(), userAddress2);
        this.userService.createOrganizationEntityUser(userRequest2);

        OrganizationEntity finalOrganizationEntity = organizationEntity;
        activities.forEach(activity -> {
            activity.getOrganizationEntities().add(finalOrganizationEntity);
            this.activityService.save(activity);
        });
        solutions.forEach(solution -> {
            solution.getOrganizationEntities().add(finalOrganizationEntity);
            this.solutionService.save(solution);
        });
        Contract contract = this.contractService.createAssociationContract(createAssociationDto.numberOfPoints(), organizationEntity);
        organizationEntity.setContract(contract);
        return this.organizationEntityRepository.save(organizationEntity). getId();
    }


    public Page<OrganizationEntity> getAssociations(Pageable pageable) {
        return this.organizationEntityRepository.findByType(List.of(EntityType.ASSOCIATION, EntityType.FOOD_BANK), pageable);
    }
}

