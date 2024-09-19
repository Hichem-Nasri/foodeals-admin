package net.foodeals.organizationEntity.application.services;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import net.foodeals.common.services.EmailService;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.application.service.DeadlinesService;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.delivery.application.services.impl.CoveredZonesService;
import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.application.dtos.requests.CoveredZonesDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.Payment;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrganizationEntityService {

    private final OrganizationEntityRepository organizationEntityRepository;
    private final ContractService contractService;
    private final CityService cityService;
    private final RegionService regionService;
    private final ActivityService activityService;
    private final SolutionService solutionService;
    private final BankInformationService bankInformationService;
    private final AddressService addressService;
    private final ContactsService contactsService;
    private final UserService userService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final CoveredZonesService coveredZonesService;

    public OrganizationEntityService(OrganizationEntityRepository organizationEntityRepository, ContractService contractService, CityService cityService, RegionService regionService, ActivityService activityService, SolutionService solutionService, BankInformationService bankInformationService, AddressService addressService, ContactsService contactsService, UserService userService, RoleService roleService, EmailService emailService, DeadlinesService deadlinesService, CoveredZonesService coveredZonesService) {
        this.organizationEntityRepository = organizationEntityRepository;
        this.contractService = contractService;
        this.cityService = cityService;
        this.regionService = regionService;
        this.activityService = activityService;
        this.solutionService = solutionService;
        this.bankInformationService = bankInformationService;
        this.addressService = addressService;
        this.contactsService = contactsService;
        this.userService = userService;
        this.roleService = roleService;
        this.emailService = emailService;
        this.coveredZonesService = coveredZonesService;
    }

    public OrganizationEntity save(OrganizationEntity organizationEntity) {
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void delete(OrganizationEntity organizationEntity) {
        this.organizationEntityRepository.softDelete(organizationEntity.getId());
    }

    public UUID createAnewOrganizationEntity(CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws DocumentException, IOException {
        City city = this.cityService.findByName(createAnOrganizationEntityDto.getEntityAddressDto().getCity());
        Region region = this.regionService.findByName(createAnOrganizationEntityDto.getEntityAddressDto().getRegion());
        Address address = Address.builder().address(createAnOrganizationEntityDto.getEntityAddressDto().getAddress())
                .city(city)
                .region(region)
                .build();
        Contact contact = Contact.builder().name(createAnOrganizationEntityDto.getEntityContactDto().getName())
                .email(createAnOrganizationEntityDto.getEntityContactDto().getEmail())
                .phone(createAnOrganizationEntityDto.getEntityContactDto().getPhone())
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
            case EntityType.PARTNER :
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
                    Region region = this.regionService.findByName(regionName);
                    if (region == null) {
                        region = this.regionService.create(coveredZonesDto.getCity(), regionName);
                    }
                    coveredZone.setRegion(region);
                    this.coveredZonesService.save(coveredZone);
                    organizationEntity.getCoveredZones().add(coveredZone);
                });
            });
        }
        Contact managerContact = organizationEntity.getContacts().getFirst();

        Role role  = this.roleService.findByName("MANAGER");
        String pass = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), true, role.getId(), organizationEntity.getId());
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
        List<String> subActivitiesNames = createAnOrganizationEntityDto.getActivities().subList(1, createAnOrganizationEntityDto.getActivities().size());
        Set<Activity> subActivities = this.activityService.getActivitiesByName(subActivitiesNames);
        Activity mainActivity = this.activityService.getActivityByName(createAnOrganizationEntityDto.getActivities().get(0));
        organizationEntity.setSubActivities(subActivities);
        organizationEntity.setMainActivity(mainActivity);
        organizationEntity.setCommercialNumber(createAnOrganizationEntityDto.getCommercialNumber());
        mainActivity.getOrganizationEntities().add(organizationEntity);
        this.activityService.save(mainActivity);
        subActivities.forEach(activity -> {
            activity.getOrganizationEntities().add(organizationEntity);
            this.activityService.save(activity);
        });
        Contract contract = this.contractService.createANewContract(createAnOrganizationEntityDto, organizationEntity);
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
        if (organizationEntity.getMainActivity().getName() != updateOrganizationEntityDto.getActivities().get(0)) {
            Activity mainActivity = this.activityService.getActivityByName(updateOrganizationEntityDto.getActivities().get(0));
            organizationEntity.setMainActivity(mainActivity);
            mainActivity.getOrganizationEntities().add(organizationEntity);
            this.activityService.save(mainActivity);
        }
        List<String> subActivitiesNames = updateOrganizationEntityDto.getActivities().subList(1, updateOrganizationEntityDto.getActivities().size());
        Set<Activity> subActivities = this.activityService.getActivitiesByName(subActivitiesNames);
        Set<Activity> subActivitiesOfPartner = new HashSet<>(organizationEntity.getSubActivities());
        subActivitiesOfPartner.stream().map((Activity activity) -> {
            if (!subActivitiesNames.contains(activity.getName())) {
                activity.getOrganizationEntities().remove(organizationEntity);
                organizationEntity.getSubActivities().remove(activity);
                this.activityService.save(activity);
            }
            if (activity.getName().equals(organizationEntity.getMainActivity().getName())) {
                organizationEntity.getSubActivities().remove(activity);
            }
            return activity;
        }).toList();
        subActivities.stream().map(activity -> {
            activity.getOrganizationEntities().add(organizationEntity);
            organizationEntity.getSubActivities().add(activity);
            this.activityService.save(activity);
            return activity;
        }).toList();

        if (updateOrganizationEntityDto.getCommercialNumber() != null) {
            organizationEntity.setCommercialNumber(updateOrganizationEntityDto.getCommercialNumber());
        }
        Contact contact = organizationEntity.getContacts().getFirst();
        if (updateOrganizationEntityDto.getEntityContactDto() != null) {
            contact = this.contactsService.update(contact, updateOrganizationEntityDto.getEntityContactDto());
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

    public List<OrganizationEntity> getOrganizationEntities() {
        return this.organizationEntityRepository.findAll();
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
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), true, role.getId(), organizationEntity.getId());
        User manager = this.userService.create(userRequest);
        SimpleDateFormat formatter = new SimpleDateFormat("M/y");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        Payment payment = Payment.builder()
                .organizationEntity(organizationEntity)
                .partnerType(PartnerType.ORGANIZATION_ENTITY)
                .paymentStatus(PaymentStatus.IN_VALID)
                .date(formattedDate)
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
}
