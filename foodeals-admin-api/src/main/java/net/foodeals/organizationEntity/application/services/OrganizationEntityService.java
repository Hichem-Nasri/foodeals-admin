package net.foodeals.organizationEntity.application.services;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.common.services.EmailService;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.application.service.DeadlinesService;
import net.foodeals.contract.domain.entities.Contract;
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
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.Payment;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.user.application.dtos.requests.UserAddress;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public OrganizationEntity save(OrganizationEntity organizationEntity) {
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void delete(OrganizationEntity organizationEntity) {
        this.organizationEntityRepository.softDelete(organizationEntity.getId());
    }

    public UUID createAnewOrganizationEntity(CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws DocumentException, IOException {
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
        return organizationEntity.getId();
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
        Contact managerContact = organizationEntity.getContacts().getFirst();

        Role role  = this.roleService.findByName("MANAGER");
        String pass = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), true, role.getId(), organizationEntity.getId(), userAddress);
        User manager = this.userService.create(userRequest);
        //        String receiver = manager.getEmail();
//        String subject = "Foodeals account validation";
//        String message = "You're account has been validated\n Your email : " + manager.getEmail() + " \n" + " Your password : " + pass;
//        this.emailService.sendEmail(receiver, subject, message);
        return this.organizationEntityRepository.save(organizationEntity);
    }

    private OrganizationEntity savePartner(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        BankInformation bankInformation = BankInformation.builder().beneficiaryName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBeneficiaryName())
                .bankName(createAnOrganizationEntityDto.getEntityBankInformationDto().getBankName())
                .rib(createAnOrganizationEntityDto.getEntityBankInformationDto().getRib())
                .build();
        organizationEntity.setBankInformation(bankInformation);
        Set<Activity> activities = this.activityService.getActivitiesByName(createAnOrganizationEntityDto.getActivities());
        organizationEntity.setActivities(activities);
        Set<Features> features = this.featureService.findFeaturesByNames(createAnOrganizationEntityDto.getFeatures());
        organizationEntity.setActivities(activities);
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
    public UUID updateOrganizationEntity(UUID id, UpdateOrganizationEntityDto updateOrganizationEntityDto) throws DocumentException, IOException {

        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "organization Entity not found");
        }

        Contract contract = organizationEntity.getContract();

        if (updateOrganizationEntityDto.getEntityName() != null) {
            organizationEntity.setName(updateOrganizationEntityDto.getEntityName());
        }
        Address address = organizationEntity.getAddress();
        if (updateOrganizationEntityDto.getEntityAddressDto() != null) {
            address = this.addressService.updateContractAddress(address, updateOrganizationEntityDto.getEntityAddressDto());
            organizationEntity.setAddress(address);
        }
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(updateOrganizationEntityDto.getSolutions());
        Set<Solution> partnerSolutions = new HashSet<>(organizationEntity.getSolutions());
        partnerSolutions.stream().map((Solution solution) -> {
                    if (!updateOrganizationEntityDto.getSolutions().contains(solution.getName())) {
                        solution.getOrganizationEntities().remove(organizationEntity);
                        organizationEntity.getSolutions().remove(solution);
                        this.solutionService.save(solution);
                    }
                    return solution;
                }).toList();
        solutions.stream().map(solution -> {
            solution.getOrganizationEntities().add(organizationEntity);
            organizationEntity.getSolutions().add(solution);
            this.solutionService.save(solution);
            return solution;
        }).toList();
        List<String> activitiesNames = updateOrganizationEntityDto.getActivities();
        Set<Activity> activities = this.activityService.getActivitiesByName(activitiesNames);
        Set<Activity> activitiesOfPartner = new HashSet<>(organizationEntity.getActivities());
        activitiesOfPartner.stream().map((Activity activity) -> {
            if (!activitiesNames.contains(activity.getName())) {
                activity.getOrganizationEntities().remove(organizationEntity);
                organizationEntity.getActivities().remove(activity);
                this.activityService.save(activity);
            }
            return activity;
        }).toList();
        activities.stream().map(activity -> {
            if (!activitiesOfPartner.contains(activity)) {
                activity.getOrganizationEntities().add(organizationEntity);
                organizationEntity.getActivities().add(activity);
                this.activityService.save(activity);
            }
            return activity;
        }).toList();

        if (updateOrganizationEntityDto.getCommercialNumber() != null) {
            organizationEntity.setCommercialNumber(updateOrganizationEntityDto.getCommercialNumber());
        }
        Contact contact = organizationEntity.getContacts().getFirst();
        if (updateOrganizationEntityDto.getContactDto() != null) {
            contact = this.contactsService.update(contact, updateOrganizationEntityDto.getContactDto());
            organizationEntity.getContacts().set(0, contact);
        }

        if (updateOrganizationEntityDto.getEntityBankInformationDto() != null) {
            BankInformation bankInformation = organizationEntity.getBankInformation();
            bankInformation = this.bankInformationService.update(bankInformation, updateOrganizationEntityDto.getEntityBankInformationDto());
            organizationEntity.setBankInformation(bankInformation);
        }
        this.organizationEntityRepository.save(organizationEntity);
        contact.setOrganizationEntity(organizationEntity);
        this.contractService.update(contract, updateOrganizationEntityDto);
        return organizationEntity.getId();
    }

    public OrganizationEntity getOrganizationEntityById(UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "organization Entity not found");
        }
        return organizationEntity;
    }

    public Page<OrganizationEntity> getOrganizationEntities(Pageable pageable) {
        return this.organizationEntityRepository.findAll(pageable);
    }

    @Transactional
    public String validateOrganizationEntity(UUID id) {
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(id).orElse(null);

        if (organizationEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization Entity not found");
        }
        Contact managerContact = organizationEntity.getContacts().getFirst();

        Role role  = this.roleService.findByName("MANAGER");
        String pass = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), true, role.getId(), organizationEntity.getId(), userAddress);
        User manager = this.userService.create(userRequest);
        SimpleDateFormat formatter = new SimpleDateFormat("M/y");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        Payment payment = Payment.builder()
                .organizationEntity(organizationEntity)
                .partnerType(PartnerType.PARTNER)
                .paymentStatus(PaymentStatus.IN_VALID)
                .date(formattedDate)
                .numberOfOrders(Long.valueOf(0))
                .paymentsWithCard(Double.valueOf(0))
                .paymentsWithCash(Double.valueOf(0))
                .build();
        organizationEntity.getPayments().add(payment);
        this.userService.save(manager);
        this.organizationEntityRepository.save(organizationEntity);
        this.contractService.validateContract(organizationEntity.getContract());
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

        Role role  = this.roleService.findByName("MANAGER");
        String pass1 = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress1 = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest1 = new UserRequest(manager1.getName(), manager1.getEmail(), manager1.getPhone(), pass1,  false, role.getId(), organizationEntity.getId(), userAddress1);
        this.userService.createOrganizationEntityUser(userRequest1);

        String pass2 = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserAddress userAddress2 = new UserAddress(organizationEntity.getAddress().getRegion().getCity().getCountry().getName(), organizationEntity.getAddress().getRegion().getCity().getName(), organizationEntity.getAddress().getRegion().getName());
        UserRequest userRequest2 = new UserRequest(manager2.getName(), manager2.getEmail(), manager2.getPhone(), pass2,  false, role.getId(), organizationEntity.getId(), userAddress2);
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
