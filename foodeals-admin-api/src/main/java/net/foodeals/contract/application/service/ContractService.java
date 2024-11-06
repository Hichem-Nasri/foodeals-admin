package net.foodeals.contract.application.service;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.SolutionContract;
import net.foodeals.contract.domain.entities.UserContract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.contract.domain.repositories.ContractRepository;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.impl.RegionServiceImpl;
import net.foodeals.location.domain.repositories.CityRepository;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.requests.DeliveryPartnerContract;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.*;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;
    private final CityService cityService;
    private final ActivityService activityService;
    private final SolutionService solutionService;
    private final SolutionContractService solutionContractService;
    private final AddressService addressService;
    private final ContactsService contactsService;
    private final CommissionService commissionService;
    private final SubscriptionService subscriptionService;
    private final BankInformationService bankInformationService;
    private final RegionServiceImpl regionServiceImpl;
    private final UserService userService;
    private final UserContractService userContractService;

    public ContractService(ContractRepository contractRepository, CityRepository cityRepository, CityService cityService, ActivityService activityService, SolutionService solutionService, SolutionContractService solutionContractService, AddressService addressService, ContactsService contactsService, CommissionService commissionService, SubscriptionService subscriptionService, BankInformationService bankInformationService, RegionServiceImpl regionServiceImpl, UserService userService, UserContractService userContractService) {
        this.contractRepository = contractRepository;
        this.cityService = cityService;
        this.activityService = activityService;
        this.solutionService = solutionService;
        this.solutionContractService = solutionContractService;
        this.addressService = addressService;
        this.contactsService = contactsService;
        this.commissionService = commissionService;
        this.subscriptionService = subscriptionService;
        this.bankInformationService = bankInformationService;
        this.regionServiceImpl = regionServiceImpl;
        this.userService = userService;
        this.userContractService = userContractService;
    }

        public Contract createPartnerContract(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        User user = this.userService.findById(createAnOrganizationEntityDto.getManagerId());
        UserContract userContract = UserContract.builder().user(user).build();
        Contract contract = Contract.builder().name(createAnOrganizationEntityDto.getEntityName())
                .maxNumberOfSubEntities(createAnOrganizationEntityDto.getMaxNumberOfSubEntities())
                .subscriptionPayedBySubEntities(createAnOrganizationEntityDto.getSubscriptionPayedBySubEntities())
                .minimumReduction(createAnOrganizationEntityDto.getMinimumReduction())
                .maxNumberOfAccounts(createAnOrganizationEntityDto.getMaxNumberOfAccounts())
                .contractStatus(ContractStatus.IN_PROGRESS)
                .singleSubscription(createAnOrganizationEntityDto.getOneSubscription())
                .commissionPayedBySubEntities(createAnOrganizationEntityDto.getCommissionPayedBySubEntities())
                .userContracts(userContract)
                .build();
        userContract.setContract(contract);
        this.userContractService.save(userContract);
        contract.setOrganizationEntity(organizationEntity);
        List<SolutionContract> solutionsContracts = this.solutionContractService.createManySolutionContracts(createAnOrganizationEntityDto.getSolutionsContractDto(), contract, createAnOrganizationEntityDto.getOneSubscription());
        contract.getSolutionContracts().addAll(solutionsContracts);
        byte[] document = this.generateContract(createAnOrganizationEntityDto);
        contract.setDocument(document);
        return contract;
    }

    public Contract updateDeliveryContract(Contract contract, CreateAnOrganizationEntityDto updateOrganizationEntityDto) {
        List<SolutionContract> solutionsContracts = this.solutionContractService.updateDeliveryContract(updateOrganizationEntityDto.getDeliveryPartnerContract(), contract);
//        byte[] document = this.getContractDocumentDelivery(createAnOrganizationEntityDto);
//        contract.setDocument(document);
        return this.contractRepository.save(contract);
    }

    public byte[] generateContract(CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws IOException, DocumentException {
        String templatePath = "resources/contract.html";
       String activities =  createAnOrganizationEntityDto.getActivities()
                  .stream().collect(Collectors.joining(" , "));
        String features = createAnOrganizationEntityDto.getFeatures().stream().filter(f -> f.equals("seller_pro") || f.equals("buyer_pro")).collect(Collectors.joining(" , "));
        SolutionsContractDto proMarket = createAnOrganizationEntityDto.getSolutionsContractDto().
                stream().filter(s -> s.getSolution().equals("pro_market"))
                .findFirst()
                .orElse(null);

        SolutionsContractDto proDonate = createAnOrganizationEntityDto.getSolutionsContractDto()
                .stream()
                .filter(s -> s.getSolution().equals("pro_donate"))
                .findFirst()
                .orElse(null);

        SolutionsContractDto proDlc = createAnOrganizationEntityDto.getSolutionsContractDto()
                .stream()
                .filter(s -> s.getSolution().equals("pro_dlc"))
                .findFirst()
                .orElse(null);



        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[raison sociale du partenaire]", createAnOrganizationEntityDto.getEntityName());
        placeholders.put("[Adresse du partenaire]", createAnOrganizationEntityDto.getEntityAddressDto().getAddress());
        placeholders.put("[numéro du registre de commerce]", createAnOrganizationEntityDto.getCommercialNumber());
        placeholders.put("[nom du gérant/responsable]", createAnOrganizationEntityDto.getContactDto().getName().firstName() + " " + createAnOrganizationEntityDto.getContactDto().getName().lastName());
        placeholders.put("[type]", activities);
        placeholders.put("[type du partenaire (Vendeur Pro / Acheteur pro)]", features);
        placeholders.put("[nombre de magasins]", createAnOrganizationEntityDto.getMaxNumberOfSubEntities().toString() + " magasin(s)");
        placeholders.put("[nombre maximum de comptes autorisés]", "avec un maximum de " + createAnOrganizationEntityDto.getMaxNumberOfAccounts().toString() + " compte(s)");
        placeholders.put("[taux de réduction]", createAnOrganizationEntityDto.getMinimumReduction().toString() + "%");
        placeholders.put("[Taux de commission/carte]", proMarket != null ? proMarket.getContractCommissionDto().getWithCard().toString() + "%" : "");
        placeholders.put("[Taux de commission/espèce]", proMarket != null ? proMarket.getContractCommissionDto().getWithCash().toString() + "%" : "");
        placeholders.put("[montant de l'abonnement pro_donate]", proDonate != null  ? proDonate.getContractSubscriptionDto().getAnnualPayment().toString()  + "dh": "");
        placeholders.put("[le nombre d’échéances pro_donate]", proDonate != null  ? proDonate.getContractSubscriptionDto().getNumberOfDueDates().toString() : "");
        placeholders.put("[Responsabilité de paiement commission]", !createAnOrganizationEntityDto.getCommissionPayedBySubEntities() ?
                "Le partenaire, en tant que détenteur du compte principal, sera responsable du\n" +
                        "\n" +
                        "paiement pour tous les sous-comptes liés, selon les modalités suivantes:" :
                "Chaque sous-compte est individuellement responsable de son propre paiement, selon\n" +
                        "\n" +
                        "les modalités suivantes:");
        placeholders.put("[Responsabilité de paiement subscription]", !createAnOrganizationEntityDto.getSubscriptionPayedBySubEntities() ?
                "Le partenaire, en tant que détenteur du compte principal, sera responsable du\n" +
                        "\n" +
                        "paiement pour tous les sous-comptes liés, selon les modalités suivantes:" :
                "Chaque sous-compte est individuellement responsable de son propre paiement, selon\n" +
                        "\n" +
                        "les modalités suivantes:");

        placeholders.put("[montant de l'abonnement pro_market]", proMarket != null ? proMarket.getContractSubscriptionDto().getAnnualPayment().toString() + "dh" : "");
        placeholders.put("[le nombre d’échéances pro_market]", proMarket != null ? proMarket.getContractSubscriptionDto().getNumberOfDueDates().toString() :  "");

        placeholders.put("[montant de l'abonnement pro_dlc]", proDlc != null  ? proDlc.getContractSubscriptionDto().getAnnualPayment().toString() + "dh" : "");
        placeholders.put("[le nombre d’échéances pro_dlc]", proDlc != null  ? proDlc.getContractSubscriptionDto().getNumberOfDueDates().toString()  : "");
        placeholders.put("[nom du bénéficiaire]", createAnOrganizationEntityDto.getEntityBankInformationDto().getBeneficiaryName());
        placeholders.put("[Banque]", createAnOrganizationEntityDto.getEntityBankInformationDto().getBankName());
        placeholders.put("[RIB]", createAnOrganizationEntityDto.getEntityBankInformationDto().getRib());
        placeholders.put("[date]", formattedDate);
        placeholders.put("[nom du signataire]", createAnOrganizationEntityDto.getContactDto().getName().firstName() + " " + createAnOrganizationEntityDto.getContactDto().getName().lastName());
        placeholders.put("[status]", "Manager");

        String template = new String(Files.readAllBytes(Paths.get(templatePath)));

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(template);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

//    public String deleteContract(UUID id) {
//        Contract contract = this.contractRepository.findById(id).orElse(null);
//
//        if (contract == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found");
//        }
//
//        OrganizationEntity organizationEntity = contract.getOrganizationEntity();
//
////        this.organizationEntityService.delete(organizationEntity);
//        List<Contact> contacts = organizationEntity.getContacts();
//        contacts.forEach(this.contactsService::delete);
//        Address address = organizationEntity.getAddress();
//        this.addressService.delete(address.getId());
//
//        List<SolutionContract> solutionContracts = contract.getSolutionContracts();
//        solutionContracts.forEach(solutionContract -> {
//            Commission commission = solutionContract.getCommission();
//            if (commission != null) {
//                this.commissionService.delete(commission);
//            }
//            Subscription subscription = solutionContract.getSubscription();
//            if (subscription != null) {
//                this.subscriptionService.delete(subscription);
//            }
//            this.solutionContractService.delete(solutionContract);
//        });
//
//        BankInformation bankInformation = organizationEntity.getBankInformation();
//        this.bankInformationService.delete(bankInformation);
//        this.contractRepository.softDelete(contract.getId());
//        return "Contract has been deleted";
//    }

    public List<Contract> getContractsByStatus(ContractStatus status) {
        return this.contractRepository.findByContractStatus(status);
    }

    @Transactional
    public Contract updateAssociationContract(CreateAssociationDto updateAssociationDto, OrganizationEntity organizationEntity) {
        Contract contract = organizationEntity.getContract();
        contract.setMaxNumberOfSubEntities(updateAssociationDto.numberOfPoints());
        UserContract userContract = contract.getUserContracts();
        if (userContract.getUser().getId() != updateAssociationDto.managerID()) {
            User manager = this.userService.findById(updateAssociationDto.managerID());
            User old = userContract.getUser();
            old.getUserContracts().remove(userContract);
            userContract.setUser(manager);
            this.userContractService.save(userContract);
        }
        return contract;
    }

    @Transactional
    public byte[] updateDocument(Contract contract) throws IOException, DocumentException {
        String templatePath = "resources/contract.html";
        String activities =  contract.getOrganizationEntity().getActivities().stream().map(f -> f.getName()).collect(Collectors.joining(" , "));
        String features = contract.getOrganizationEntity().getFeatures().stream().filter(f -> f.getName().equals("seller_pro") || f.getName().equals("buyer_pro")).map(f -> f.getName()).collect(Collectors.joining(" , "));

        SolutionContract proDonate = contract.getSolutionContracts()
                .stream()
                .filter(s -> s.getSolution().getName().equals("pro_donate"))
                .findFirst()
                .orElse(null);

        SolutionContract proDlc = contract.getSolutionContracts()
                .stream()
                .filter(s -> s.getSolution().getName().equals("pro_dlc"))
                .findFirst()
                .orElse(null);

        SolutionContract proMarket = contract.getSolutionContracts()
                .stream()
                .filter(s -> s.getSolution().getName().equals("pro_market"))
                .findFirst()
                .orElse(null);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[raison sociale du partenaire]", contract.getOrganizationEntity().getName());
        placeholders.put("[Adresse du partenaire]", contract.getOrganizationEntity().getAddress().getAddress());
        placeholders.put("[numéro du registre de commerce]", contract.getOrganizationEntity().getCommercialNumber());
        placeholders.put("[nom du gérant/responsable]", contract.getOrganizationEntity().getContacts().getFirst().getName().firstName() + " " + contract.getOrganizationEntity().getContacts().getFirst().getName().lastName());
        placeholders.put("[type]", activities);
        placeholders.put("[type du partenaire (Vendeur Pro / Acheteur pro)]", features);
        placeholders.put("[nombre de magasins]", contract.getMaxNumberOfSubEntities().toString() + " magasin(s)");
        placeholders.put("[nombre maximum de comptes autorisés]", "avec un maximum de " + contract.getMaxNumberOfAccounts().toString() + " compte(s)");
        placeholders.put("[taux de réduction]", contract.getMinimumReduction().toString() + "%");
        placeholders.put("[Taux de commission/carte]", proMarket != null ? proMarket.getCommission().getCard().toString() + "%" : "");
        placeholders.put("[Taux de commission/espèce]", proMarket != null ? proMarket.getCommission().getCash().toString() + "%" : "");
        placeholders.put("[montant de l'abonnement pro_donate]", proDonate != null  ? proDonate.getSubscription().getAmount().amount().toString()  + "dh": "");
        placeholders.put("[le nombre d’échéances pro_donate]", proDonate != null  ? proDonate.getSubscription().getNumberOfDueDates().toString() : "");

        placeholders.put("[Responsabilité de paiement commission]", !contract.isCommissionPayedBySubEntities() ?
                "Le partenaire, en tant que détenteur du compte principal, sera responsable du\n" +
                        "\n" +
                        "paiement pour tous les sous-comptes liés, selon les modalités suivantes:" :
                "Chaque sous-compte est individuellement responsable de son propre paiement, selon\n" +
                        "\n" +
                        "les modalités suivantes:");
        placeholders.put("[Responsabilité de paiement subscription]", !contract.isSubscriptionPayedBySubEntities() ?
                "Le partenaire, en tant que détenteur du compte principal, sera responsable du\n" +
                        "\n" +
                        "paiement pour tous les sous-comptes liés, selon les modalités suivantes:" :
                "Chaque sous-compte est individuellement responsable de son propre paiement, selon\n" +
                        "\n" +
                        "les modalités suivantes:");

        placeholders.put("[montant de l'abonnement pro_market]", proMarket != null ? proMarket.getSubscription().getAmount().amount().toString() + "dh" : "");
        placeholders.put("[le nombre d’échéances pro_market]", proMarket != null ? proMarket.getSubscription().getNumberOfDueDates().toString() :  "");

        placeholders.put("[montant de l'abonnement pro_dlc]", proDlc != null  ? proDlc.getSubscription().getAmount().amount().toString() + "dh" : "");
        placeholders.put("[le nombre d’échéances pro_dlc]", proDlc != null  ? proDlc.getSubscription().getNumberOfDueDates().toString()  : "");
        placeholders.put("[nom du bénéficiaire]", contract.getOrganizationEntity().getBankInformation().getBeneficiaryName());
        placeholders.put("[Banque]", contract.getOrganizationEntity().getBankInformation().getBankName());
        placeholders.put("[RIB]", contract.getOrganizationEntity().getBankInformation().getRib());
        placeholders.put("[date]", formattedDate);
        placeholders.put("[nom du signataire]", contract.getOrganizationEntity().getContacts().getFirst().getName().firstName() + " " + contract.getOrganizationEntity().getContacts().getFirst().getName().lastName());
        placeholders.put("[status]", "Manager");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        String template = new String(Files.readAllBytes(Paths.get(templatePath)));

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(template);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

    public Contract save(Contract contract) {
        return this.contractRepository.save(contract);
    }

    @Transactional
    public void update(Contract contract, CreateAnOrganizationEntityDto updateOrganizationEntityDto) throws DocumentException, IOException {
        if (updateOrganizationEntityDto.getMaxNumberOfSubEntities() != null) {
            contract.setMaxNumberOfSubEntities(updateOrganizationEntityDto.getMaxNumberOfSubEntities());
        }

        contract.setMaxNumberOfAccounts(updateOrganizationEntityDto.getMaxNumberOfAccounts());

        if (updateOrganizationEntityDto.getMinimumReduction() != null) {
            contract.setMinimumReduction(updateOrganizationEntityDto.getMinimumReduction());
        }

        contract.setCommissionPayedBySubEntities(updateOrganizationEntityDto.getCommissionPayedBySubEntities());

        contract.setSubscriptionPayedBySubEntities(updateOrganizationEntityDto.getSubscriptionPayedBySubEntities());

        if (updateOrganizationEntityDto.getManagerId() != null && updateOrganizationEntityDto.getManagerId() != contract.getUserContracts().getUser().getId()) {
            this.userContractService.updateUserContract(contract.getUserContracts(), updateOrganizationEntityDto);
        }

        this.solutionContractService.update(contract, updateOrganizationEntityDto);
        Contract finalContract = contract;
        this.contractRepository.saveAndFlush(contract);
        contract = this.contractRepository.findById(contract.getId()).orElseThrow(() -> new ResourceNotFoundException("contract not found " + finalContract.getId()));
        byte[] document = this.updateDocument(contract);
        // keep the previous version of the document.
        contract.setDocument(document);
        this.contractRepository.save(contract);
    }

    public byte[] getContractDocument(UUID id) {
        Contract contract = this.contractRepository.findById(id).orElse(null);

        if (contract == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found");
        }
        return contract.getDocument();
    }

    public void validateContract(Contract contract) {
        List<SolutionContract> solutionContracts = contract.getSolutionContracts();

        if (contract.isSingleSubscription()) {
            SolutionContract solutionContract = solutionContracts.stream().filter(solutionContract1 -> solutionContract1.getSubscription() != null).findFirst().get();
            this.subscriptionService.startSubscription(solutionContract.getSubscription());
        }
        else {
            solutionContracts.forEach(solutionContract -> {
                if (solutionContract.getSubscription() != null) {
                    this.subscriptionService.startSubscription(solutionContract.getSubscription());
                }
            });
        }
        this.contractRepository.save(contract);
    }

    public Contract createAssociationContract(Integer maxNumberOfPoints, OrganizationEntity organizationEntity, User manager) {
        Contract contract = Contract.builder().maxNumberOfSubEntities(maxNumberOfPoints)
                .organizationEntity(organizationEntity)
                .contractStatus(ContractStatus.IN_PROGRESS)
                .build();
        contract =  this.contractRepository.save(contract);
        UserContract userContract = UserContract.builder().user(manager).contract(contract).build();
        this.userContractService.save(userContract);
        manager.getUserContracts().add(userContract);
        this.userService.save(manager);
        return contract;
    }

    public Contract createDeliveryPartnerContract(OrganizationEntity organizationEntity, CreateAnOrganizationEntityDto createAnOrganizationEntityDto) {
        Contract contract = Contract.builder().name(createAnOrganizationEntityDto.getEntityName())
                .contractStatus(ContractStatus.IN_PROGRESS)
                .build();
        organizationEntity.setContract(contract);
        contract.setOrganizationEntity(organizationEntity);
        List<SolutionContract> solutionsContracts = this.solutionContractService.createDeliveryContracts(createAnOrganizationEntityDto.getDeliveryPartnerContract(), contract);
        contract.getSolutionContracts().addAll(solutionsContracts);
//        byte[] document = this.getContractDocumentDelivery(createAnOrganizationEntityDto);
//        contract.setDocument(document);
        return this.contractRepository.save(contract);
    }

    // to be implemented
//    private byte[] getContractDocumentDelivery(CreateAnOrganizationEntityDto createAnOrganizationEntityDto) {
//    }
}

