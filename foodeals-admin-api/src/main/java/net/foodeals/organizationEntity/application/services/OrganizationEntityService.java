package net.foodeals.organizationEntity.application.services;

import com.lowagie.text.DocumentException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.common.entities.DeletionReason;
import net.foodeals.common.services.EmailService;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.delivery.application.services.impl.CoveredZonesService;
import net.foodeals.delivery.domain.entities.CoveredZones;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.delivery.domain.enums.DeliveryStatus;
import net.foodeals.delivery.domain.repositories.DeliveryRepository;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.CountryService;
import net.foodeals.location.application.services.impl.RegionServiceImpl;
import net.foodeals.location.domain.entities.*;
import net.foodeals.offer.domain.entities.Offer;
import net.foodeals.offer.domain.entities.PublisherInfo;
import net.foodeals.offer.domain.repositories.OfferRepository;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.enums.OrderStatus;
import net.foodeals.order.domain.enums.TransactionStatus;
import net.foodeals.order.domain.enums.TransactionType;
import net.foodeals.order.domain.repositories.OrderRepository;
import net.foodeals.organizationEntity.application.dtos.requests.CoveredZonesDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityFilter;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.PartnerCommissions;
import net.foodeals.payment.domain.entities.PartnerInfo;
import net.foodeals.payment.domain.entities.Enum.PaymentStatus;
import net.foodeals.processors.classes.DtoProcessor;
import net.foodeals.schedule.utils.PartnerCommissionsUtil;
import net.foodeals.user.application.dtos.requests.UserAddress;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.valueObjects.Name;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
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
    private final PartnerCommissionsUtil util;
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final SubEntityRepository subEntityRepository;
    private final DeliveryRepository deliveryRepository;

    public OrganizationEntity save(OrganizationEntity organizationEntity) {
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void delete(OrganizationEntity organizationEntity) {
        this.organizationEntityRepository.softDelete(organizationEntity.getId());
    }

    @Transactional
    public OrganizationEntity createAnewOrganizationEntity(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, MultipartFile logo, MultipartFile cover) throws DocumentException, IOException {
        try {
            dtoProcessor.processDto(createAnOrganizationEntityDto);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error ");
        }

        AddressRequest addressRequest = new AddressRequest(createAnOrganizationEntityDto.getEntityAddressDto().getCountry(), createAnOrganizationEntityDto.getEntityAddressDto().getAddress(), createAnOrganizationEntityDto.getEntityAddressDto().getState(), createAnOrganizationEntityDto.getEntityAddressDto().getCity(), createAnOrganizationEntityDto.getEntityAddressDto().getRegion(), createAnOrganizationEntityDto.getEntityAddressDto().getIframe());
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

    @Transactional
    private OrganizationEntity saveDeliveryPartner(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        if (createAnOrganizationEntityDto.getCoveredZonesDtos() != null) {
            List<CoveredZonesDto> coveredZonesDtos = createAnOrganizationEntityDto.getCoveredZonesDtos();
            coveredZonesDtos.forEach(coveredZonesDto -> {
                List<String> regionsNames = coveredZonesDto.getRegions();
                regionsNames.forEach(regionName -> {
                    CoveredZones coveredZone = CoveredZones.builder().organizationEntity(organizationEntity)
                            .build();
                    Country country = this.countryService.findByName(coveredZonesDto.getCountry());
                    State state = country.getStates().stream().filter(s -> s.getName().equals(coveredZonesDto.getState().toLowerCase())).findFirst().get();
                    City city = state.getCities().stream().filter(c -> c.getName().equals(coveredZonesDto.getCity().toLowerCase())).findFirst().get();
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

    @Transactional
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
    public OrganizationEntity updateOrganizationEntity(UUID id, CreateAnOrganizationEntityDto updateOrganizationEntityDto, MultipartFile logo, MultipartFile cover) throws DocumentException, IOException {
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
                    State state = country.getStates().stream().filter(s -> s.getName().equalsIgnoreCase(coveredZonesDto.getState())).findFirst().orElseThrow(() -> new RuntimeException("State not found"));
                    City city = state.getCities().stream().filter(c -> c.getName().equalsIgnoreCase(coveredZonesDto.getCity())).findFirst().orElseThrow(() -> new RuntimeException("City not found"));
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
        try {
            Contract contract = this.contractService.updateDeliveryContract(organizationEntity.getContract(), updateOrganizationEntityDto);
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return this.organizationEntityRepository.save(organizationEntity);
    }

    public void deleteOrganization(UUID uuid, UpdateReason reason) {
        OrganizationEntity organization = organizationEntityRepository.getEntity(uuid).orElseThrow(() -> new EntityNotFoundException("Organization not found with uuid: " + uuid));
        DeletionReason deletionReason = DeletionReason.builder()
                .details(reason.details())
                .reason(reason.reason())
                .type(reason.action())
                .build();
        organization.getDeletionReasons().add(deletionReason);
        organization.markDeleted(reason.action());
        organizationEntityRepository.save(organization);
    }


    @Transactional
    public Page<OrganizationEntity> getDeletedOrganizationsPaginated(Pageable pageable, List<EntityType> type) {
        return  this.organizationEntityRepository.findByDeletedAtIsNotNullAndTypeIn(pageable, type);
    }

    @Transactional
    public Page<UpdateDetails> getDeletionDetails(UUID uuid, Pageable page) {
        OrganizationEntity organization = this.organizationEntityRepository.getEntity(uuid).orElseThrow(() -> new EntityNotFoundException("Organization not found with uuid: " + uuid));

        List<DeletionReason> deletionReasons = organization.getDeletionReasons();

        int start = (int)page.getOffset();
        int end = Math.min(start + page.getPageSize(), deletionReasons.size());
        List<DeletionReason> content = deletionReasons.subList(start, end);

        return new PageImpl<>(content, page, deletionReasons.size()).map(d -> new UpdateDetails(d.getType(), d.getDetails(), d.getReason(), Date.from(d.getCreatedAt())));
    }
    @Transactional
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
        OrganizationEntity organizationEntity = this.organizationEntityRepository.getEntity(id).orElse(null);

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
    public Page<OrganizationEntity> getOrganizationEntitiesFilters(OrganizationEntityFilter filter, Pageable pageable) {
        return this.organizationEntityRepository.findWithFilters(filter, pageable);
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
        UserRequest userRequest = new UserRequest(managerContact.getName(), managerContact.getEmail(), managerContact.getPhone(), RandomStringUtils.random(12), false, "MANAGER", organizationEntity.getId());
        User manager = this.userService.create(userRequest);
        if (!organizationEntity.getType().equals(EntityType.FOOD_BANK) && !organizationEntity.getType().equals(EntityType.ASSOCIATION)) {
            Solution pro_market = this.solutionService.findByName("pro_market");
            if (organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER) || organizationEntity.getSolutions().contains(pro_market)) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                Date previousMonthDate = cal.getTime();
                Date date = new Date();
                PartnerCommissions partnerCommissions = PartnerCommissions.builder()
                        .partnerInfo(new PartnerInfo(organizationEntity.getId(), organizationEntity.getId(), organizationEntity.getPartnerType(), organizationEntity.getName()))
                        .paymentStatus(PaymentStatus.IN_VALID)
                        .paymentResponsibility(organizationEntity.commissionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                        .date(date)
                        .build();
                organizationEntity.getCommissions().add(partnerCommissions);
            }
            if (!organizationEntity.getType().equals(EntityType.DELIVERY_PARTNER)) {
                this.contractService.validateContract(organizationEntity.getContract());
            }
//            if (organizationEntity.getType().equals(EntityType.PARTNER_WITH_SB)) {
//                // TODO : should be removed after tests.
//                try {
//                    this.createSubentity(organizationEntity);
//                } catch (BadRequestException e) {
//                    System.out.println("Error creating subEntity : ");
//                    e.printStackTrace();
//                }
//            }
        }
        organizationEntity.getContract().setContractStatus(ContractStatus.VALIDATED);
        this.organizationEntityRepository.save(organizationEntity);

//        String receiver = manager.getEmail();
//        String subject = "Foodeals account validation";
//        String message = "You're account has been validated\n Your email : " + manager.getEmail() + " \n" + " Your password : " + pass;
//        this.emailService.sendEmail(receiver, subject, message);
        return "Contract validated successfully";
    }

    @Transactional
    private void createSubentity(OrganizationEntity organizationEntity) throws BadRequestException {
        List<SubEntity> subEntities = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Set<Solution> partnerSolutions = new HashSet<>(organizationEntity.getSolutions());
            AddressRequest addressRequest = new AddressRequest("morocco", "", "casablanca-settat", "casablanca", "maarif", "tes" + i);
            Contact contact = new Contact();
            contact.setName(new Name("John " + i, "Doe " + i));
            contact.setPhone("+212" + (int) (Math.random() * 1000000000));
            contact.setEmail("john.doe" + i + (int) (Math.random() * 1000000000) + "@example.com");
            Address address = this.addressService.create(addressRequest);

            SubEntity subEntity = new SubEntity();
            subEntity.setName("Marjan sbata " + i);
            subEntity.setType(SubEntityType.PARTNER_SB);
            subEntity.setOrganizationEntity(organizationEntity);
            subEntity.setAddress(address);
            subEntity.setAvatarPath("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlbgNRgwtiqyboN2mV8GPlGyE3beg3FqraOQ&s");

            subEntity.setContacts(new ArrayList<>(List.of(contact)));

            subEntity = this.subEntityRepository.save(subEntity);
            subEntity.setSolutions(partnerSolutions);

            SubEntity finalSubEntity = subEntity;
            partnerSolutions.forEach(solution -> {
                solution.getSubEntities().add(finalSubEntity);
                this.solutionService.save(solution);
            });

            organizationEntity.getSubEntities().add(subEntity);
            this.organizationEntityRepository.save(organizationEntity);
            this.subEntityRepository.saveAndFlush(subEntity);
            subEntities.add(subEntity);
        }

        this.util.createCommissionsForPartner(organizationEntity);
        this.createOffers(subEntities);
    }

    @Transactional
    private void createOffers(List<SubEntity> subEntities) throws BadRequestException {
        int i = 0;
        for (SubEntity subEntity : subEntities) {
            Offer offer = new Offer();
            offer.setPrice(new Price(BigDecimal.valueOf(100), Currency.getInstance("MAD")));
            offer.setSalePrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
            offer.setReduction(20);
            offer.setPublisherInfo(new PublisherInfo(subEntity.getId(), subEntity.getPublisherType()));
            offer.setImagePath("https://dynamic-media-cdn.tripadvisor.com/media/photo-o/23/71/2f/68/pizzas-and-panozzos.jpg");
            offer.setTitle("Pizza ");
            offer = this.offerRepository.save(offer);

            // Create Order 1
            Order order1 = new Order();
            order1.setPrice(new Price(BigDecimal.valueOf(50), Currency.getInstance("MAD")));
            order1.setQuantity(1);
            order1.setStatus(OrderStatus.COMPLETED);
            order1.setOffer(offer);  // Attach the order to the offer

            // Create Order 2
            Order order2 = new Order();
            order2.setPrice(new Price(BigDecimal.valueOf(30), Currency.getInstance("MAD")));
            order2.setQuantity(1);
            order2.setStatus(OrderStatus.COMPLETED);
            order2.setOffer(offer);  // Attach the order to the offer

            Order order3 = new Order();
            order3.setPrice(new Price(BigDecimal.valueOf(30), Currency.getInstance("MAD")));
            order3.setQuantity(1);
            order3.setStatus(OrderStatus.COMPLETED);
            order3.setOffer(offer);
            // Create Transaction for Order 1
            Transaction transaction1 = new Transaction();
            transaction1.setPaymentId("PAY123");
            transaction1.setReference("REF123");
            transaction1.setContext("Context1");
            transaction1.setPrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
            transaction1.setStatus(TransactionStatus.COMPLETED);
            transaction1.setType( i == 0 ? TransactionType.CASH : TransactionType.CARD);
            transaction1.setOrder(order1);

            Transaction transaction5 = new Transaction();
            transaction5.setPaymentId("PAY123");
            transaction5.setReference("REF123");
            transaction5.setContext("Context1");
            transaction5.setPrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
            transaction5.setStatus(TransactionStatus.COMPLETED);
            transaction5.setType( i == 0 ? TransactionType.CASH : TransactionType.CARD);
            transaction5.setOrder(order3); // Attach transaction to order


            order1.setTransaction(transaction1);

            order3.setTransaction(transaction5);

            // Create Transaction for Order 2
            Transaction transaction4 = new Transaction();
            transaction4.setPaymentId("PAY126");
            transaction4.setReference("REF126");
            transaction4.setContext("Context4");
            transaction4.setPrice(new Price(BigDecimal.valueOf(80), Currency.getInstance("MAD")));
            transaction4.setStatus(TransactionStatus.COMPLETED);
            transaction4.setType(i == 0 ? TransactionType.CASH : TransactionType.CARD);
            transaction4.setOrder(order2);

            order2.setTransaction(transaction4);

            // Save the orders
            order1 = this.orderRepository.saveAndFlush(order1);
            order2 = this.orderRepository.saveAndFlush(order2);
            order3 = this.orderRepository.saveAndFlush(order3);

            offer.getOrders().add(order1);
            offer.getOrders().add(order2);
            offer.getOrders().add(order3);

            this.offerRepository.save(offer);
            if (i == 0) {
                // Create a new Delivery instance
                User d = new User();
                OrganizationEntity o = this.organizationEntityRepository.findById(UUID.fromString("e6780181-a601-41bc-8544-1d74051b44d5")).orElse(null);
                d.setOrganizationEntity(o);
                d = this.userService.save(d);
                o.getUsers().add(d);
                this.organizationEntityRepository.saveAndFlush(o);
                Delivery delivery = new Delivery();
                Delivery delivery1 = new Delivery();
                delivery1.setStatus(DeliveryStatus.DELIVERED);
                delivery.setStatus(DeliveryStatus.DELIVERED);


                delivery.setDeliveryBoy(d);
                delivery1.setDeliveryBoy(d);

//



                // Set any necessary properties for the delivery here
                // e.g., delivery.setSomeProperty(value);

                // Save the delivery only once after setting its properties
                delivery = this.deliveryRepository.saveAndFlush(delivery);
                delivery1 = this.deliveryRepository.saveAndFlush(delivery1);
                d.getDeliveries().add(delivery);
                d.getDeliveries().add(delivery1);
                this.userService.save(d);

                // Set properties for order1 and order2 as needed
                // e.g., order1.setPrice(price1);
                // e.g., order2.setPrice(price2);

                // Set the delivery reference in each order
                order2.setDelivery(delivery);
                order1.setDelivery(delivery);
                order3.setDelivery(delivery1);

                // Save the orders, this will also update the delivery reference
                order2 = this.orderRepository.save(order2);
                order1 = this.orderRepository.save(order1);
                order3 = this.orderRepository.save(order3);

                // Add orders to the delivery
                delivery.getOrders().addAll(new ArrayList<>(List.of(order2, order1)));
                delivery1.getOrders().addAll(new ArrayList<>(List.of(order3)));


                // Optional: If you need to update the delivery after adding orders
                 delivery = this.deliveryRepository.save(delivery); // Usually not needed

                delivery1 = this.deliveryRepository.save(delivery1); // Usually not needed
            }

            i++;
        }
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

    @Transactional
    public UUID createAssociation(CreateAssociationDto createAssociationDto, MultipartFile logo, MultipartFile cover) {
            AddressRequest addressRequest = new AddressRequest(
                createAssociationDto.associationAddress().getCountry(),
                createAssociationDto.associationAddress().getAddress(),
                createAssociationDto.associationAddress().getState(), // Added state here
                createAssociationDto.associationAddress().getCity(),
                createAssociationDto.associationAddress().getRegion(),
                createAssociationDto.associationAddress().getIframe()
            );
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
        Contact manager1 = this.contactsService.create(createAssociationDto.responsible(), organizationEntity, true);
        organizationEntity.getContacts().add(manager1);
        User manager = this.userService.findById(createAssociationDto.managerID());


        OrganizationEntity finalOrganizationEntity = organizationEntity;
        activities.forEach(activity -> {
            activity.getOrganizationEntities().add(finalOrganizationEntity);
            this.activityService.save(activity);
        });
        solutions.forEach(solution -> {
            solution.getOrganizationEntities().add(finalOrganizationEntity);
            this.solutionService.save(solution);
        });
        Contract contract = this.contractService.createAssociationContract(createAssociationDto.numberOfPoints(), organizationEntity, manager);
        organizationEntity.setContract(contract);
        return this.organizationEntityRepository.save(organizationEntity).getId();
    }

    @Transactional
    public UUID updateAssociation(UUID organizationId, CreateAssociationDto updateAssociationDto, MultipartFile cover, MultipartFile logo) {

        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        // Update Address
        AddressRequest addressRequest = new AddressRequest(updateAssociationDto.associationAddress().getCountry(), updateAssociationDto.associationAddress().getAddress(), updateAssociationDto.associationAddress().getState(), updateAssociationDto.associationAddress().getCity(), updateAssociationDto.associationAddress().getRegion(), updateAssociationDto.associationAddress().getIframe());
        Address address = this.addressService.update(organizationEntity.getAddress().getId(), addressRequest);
        // Update Activities
        List<String> activitiesNames = updateAssociationDto.activities();
        Set<Activity> activities = this.activityService.getActivitiesByName(activitiesNames);

        Set<Activity> activitiesToRemove = organizationEntity.getActivities()
                .stream()
                .filter(activity -> !activitiesNames.contains(activity.getName()))
                .collect(Collectors.toSet());

        OrganizationEntity finalOrganizationEntity = organizationEntity;
        Set<Activity> activitiesToAdd = activities.stream()
                .filter(activity -> !finalOrganizationEntity.getActivities().contains(activity))
                .collect(Collectors.toSet());

        OrganizationEntity finalOrganizationEntity1 = organizationEntity;
        activitiesToRemove.forEach(activity -> {
            activity.getOrganizationEntities().remove(finalOrganizationEntity1);
            finalOrganizationEntity1.getActivities().remove(activity);
            this.activityService.save(activity);
        });
        OrganizationEntity finalOrganizationEntity2 = organizationEntity;
        activitiesToAdd.forEach(activity -> {
            activity.getOrganizationEntities().add(finalOrganizationEntity2);
            finalOrganizationEntity2.getActivities().add(activity);
            this.activityService.save(activity);
        });

        List<String> solutionsNames = updateAssociationDto.solutions();
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(solutionsNames);

        Set<Solution> solutionsToRemove = organizationEntity.getSolutions()
                .stream()
                .filter(solution -> !solutionsNames.contains(solution.getName()))
                .collect(Collectors.toSet());

        OrganizationEntity finalOrganizationEntity5 = organizationEntity;
        Set<Solution> solutionsToAdd = solutions.stream()
                .filter(solution -> !finalOrganizationEntity5.getSolutions().contains(solution))
                .collect(Collectors.toSet());

        OrganizationEntity finalOrganizationEntity3 = organizationEntity;
        solutionsToRemove.forEach(solution -> {
            solution.getOrganizationEntities().remove(finalOrganizationEntity3);
            finalOrganizationEntity3.getSolutions().remove(solution);
            this.solutionService.save(solution);
        });
        OrganizationEntity finalOrganizationEntity4 = organizationEntity;
        solutionsToAdd.forEach(solution -> {
            solution.getOrganizationEntities().add(finalOrganizationEntity4);
            finalOrganizationEntity4.getSolutions().add(solution);
            this.solutionService.save(solution);
        });
        Contact contact = organizationEntity.getContacts().getFirst();
        this.contactsService.update(contact, updateAssociationDto.responsible());
        

        // Update Company Name
        organizationEntity.setName(updateAssociationDto.companyName());

        // Update Entity Type
        organizationEntity.setType(updateAssociationDto.entityType());

        // Update Commercial Number
        organizationEntity.setCommercialNumber(updateAssociationDto.pv());

        // Update Contract
        Contract contract = this.contractService.updateAssociationContract(updateAssociationDto, organizationEntity);
        organizationEntity =  this.organizationEntityRepository.save(organizationEntity);

        return this.organizationEntityRepository.save(organizationEntity).getId();
    }

    public Page<OrganizationEntity> getAssociations(Pageable pageable) {
        return this.organizationEntityRepository.findByType(List.of(EntityType.ASSOCIATION, EntityType.FOOD_BANK, EntityType.FOOD_BANK_ASSO), pageable);
    }

public Page<OrganizationEntity> searchPartnersByName(UUID id, String name, List<EntityType> types, Pageable pageable, boolean includeDeleted) {
    if (id != null) {

        OrganizationEntity entity = organizationEntityRepository.getEntity(id).orElse(null);
        if (entity == null) {
            throw new ResourceNotFoundException("Entity not found with ID: " + id);
        }

        return new PageImpl<>(List.of(entity), pageable, 1);
    }

    Page<OrganizationEntity> entities;

    if (name != null && !name.isEmpty()) {
        entities = organizationEntityRepository.findByNameContainingAndTypeInAndDeletedAtIs(name, types, includeDeleted, pageable);
    } else {
        entities = organizationEntityRepository.findByTypeInAndDeletedAtIs(types, includeDeleted, pageable);
    }

    return entities;
}

    public Page<City> searchCitiesByOrganizationAddress(List<EntityType> types, String cityName, String countryName, Pageable pageable) {
        return organizationEntityRepository.findCitiesByOrganizationAddress(types, cityName, countryName, pageable);
    }
}

