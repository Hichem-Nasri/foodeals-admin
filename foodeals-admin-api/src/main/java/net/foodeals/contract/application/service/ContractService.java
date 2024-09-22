package net.foodeals.contract.application.service;

import com.lowagie.text.DocumentException;
import jakarta.transaction.Transactional;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.SolutionContract;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.UserContract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.contract.domain.repositories.ContractRepository;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.repositories.CityRepository;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.*;
import net.foodeals.organizationEntity.domain.entities.*;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
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
    private final RegionService regionService;
    private final UserService userService;
    private final UserContractService userContractService;

    public ContractService(ContractRepository contractRepository, CityRepository cityRepository, CityService cityService, ActivityService activityService, SolutionService solutionService, SolutionContractService solutionContractService, AddressService addressService, ContactsService contactsService, CommissionService commissionService, SubscriptionService subscriptionService, BankInformationService bankInformationService, RegionService regionService, UserService userService, UserContractService userContractService) {
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
        this.regionService = regionService;
        this.userService = userService;
        this.userContractService = userContractService;
    }

        public Contract createANewContract(CreateAnOrganizationEntityDto createAnOrganizationEntityDto, OrganizationEntity organizationEntity) throws DocumentException, IOException {
        User user = this.userService.findById(createAnOrganizationEntityDto.getManagerId());
        UserContract userContract = UserContract.builder().user(user).build();
        Contract contract = Contract.builder().name(createAnOrganizationEntityDto.getEntityName())
                .maxNumberOfSubEntities(createAnOrganizationEntityDto.getMaxNumberOfSubEntities())
                .maxNumberOfAccounts(createAnOrganizationEntityDto.getMaxNumberOfAccounts())
                .minimumReduction(createAnOrganizationEntityDto.getMinimumReduction())
                .contractStatus(ContractStatus.IN_PROGRESS)
                .singleSubscription(createAnOrganizationEntityDto.getOneSubscription())
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

    public byte[] generateContract(CreateAnOrganizationEntityDto createAnOrganizationEntityDto) throws IOException, DocumentException {
        String templatePath = "contract.html";
        String subActivities =  createAnOrganizationEntityDto.getActivities().subList(1, createAnOrganizationEntityDto.getActivities().size())
                .stream().collect(Collectors.joining(" , "));

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[raison sociale du partenaire]", createAnOrganizationEntityDto.getEntityName());
        placeholders.put("[Adresse du partenaire]", createAnOrganizationEntityDto.getEntityAddressDto().getAddress());
        placeholders.put("[numéro du registre de commerce]", createAnOrganizationEntityDto.getCommercialNumber());
        placeholders.put("[nom du gérant/responsable]", createAnOrganizationEntityDto.getEntityContactDto().getName().firstName() + " " + createAnOrganizationEntityDto.getEntityContactDto().getName().lastName());
        placeholders.put("[Catégorie]", createAnOrganizationEntityDto.getActivities().get(0));
        placeholders.put("[sous-catégorie]", subActivities);
        placeholders.put("[nombre de points de vente]", createAnOrganizationEntityDto.getMaxNumberOfSubEntities().toString());
        placeholders.put("[nombre maximum de comptes autorisés]", createAnOrganizationEntityDto.getMaxNumberOfAccounts().toString());
        placeholders.put("[taux de réduction]", "15%");
        placeholders.put("[Taux de commission/carte]", "2.5%");
        placeholders.put("[Taux de commission/espèce]", "3%");
        placeholders.put("[montant de l'abonnement]", "500 MAD");
        placeholders.put("[le nombre d'échéances]", "12");
        placeholders.put("[nom du bénéficiaire]", createAnOrganizationEntityDto.getEntityBankInformationDto().getBeneficiaryName());
        placeholders.put("[Banque]", createAnOrganizationEntityDto.getEntityBankInformationDto().getBankName());
        placeholders.put("[RIB]", createAnOrganizationEntityDto.getEntityBankInformationDto().getRib());
        placeholders.put("[date]", formattedDate);
        placeholders.put("[nom du signataire]", createAnOrganizationEntityDto.getEntityContactDto().getName().firstName() + " " + createAnOrganizationEntityDto.getEntityContactDto().getName().lastName());
        placeholders.put("[responsibility]", "Manager");

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
    public byte[] updateDocument(Contract contract) throws IOException, DocumentException {
        String templatePath = "contract.html";
        String subActivities =  contract.getOrganizationEntity().getSubActivities().stream().map(activity -> activity.getName()).collect(Collectors.joining(" , "));

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("[raison sociale du partenaire]", contract.getOrganizationEntity().getName());
        placeholders.put("[Adresse du partenaire]", contract.getOrganizationEntity().getAddress().getAddress());
        placeholders.put("[numéro du registre de commerce]", contract.getOrganizationEntity().getCommercialNumber());
        placeholders.put("[nom du gérant/responsable]", contract.getOrganizationEntity().getContacts().get(0).getName().firstName() + " " + contract.getOrganizationEntity().getContacts().get(0).getName().lastName());
        placeholders.put("[Catégorie]", contract.getOrganizationEntity().getMainActivity().getName());
        placeholders.put("[sous-catégorie]", subActivities);
        placeholders.put("[nombre de points de vente]", contract.getMaxNumberOfSubEntities().toString());
        placeholders.put("[nombre maximum de comptes autorisés]", contract.getMaxNumberOfAccounts().toString());
        placeholders.put("[taux de réduction]", contract.getMinimumReduction().toString());
        placeholders.put("[Taux de commission/carte]", "2.5%");
        placeholders.put("[Taux de commission/espèce]", "3%");
        placeholders.put("[montant de l'abonnement]", "500 MAD");
        placeholders.put("[le nombre d'échéances]", "12");
        placeholders.put("[nom du bénéficiaire]", contract.getOrganizationEntity().getBankInformation().getBeneficiaryName());
        placeholders.put("[Banque]", contract.getOrganizationEntity().getBankInformation().getBankName());
        placeholders.put("[RIB]", contract.getOrganizationEntity().getBankInformation().getRib());
        placeholders.put("[date]", formattedDate);
        placeholders.put("[nom du signataire]", contract.getOrganizationEntity().getContacts().get(0).getName().firstName() + " " + contract.getOrganizationEntity().getContacts().get(0).getName().lastName());
        placeholders.put("[responsibility]", "Manager");

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
    public byte[] update(Contract contract, UpdateOrganizationEntityDto updateOrganizationEntityDto) throws DocumentException, IOException {
        if (updateOrganizationEntityDto.getMaxNumberOfSubEntities() != null) {
            contract.setMaxNumberOfSubEntities(updateOrganizationEntityDto.getMaxNumberOfSubEntities());
        }

        if (updateOrganizationEntityDto.getMaxNumberOfAccounts() != null) {
            contract.setMaxNumberOfAccounts(updateOrganizationEntityDto.getMaxNumberOfAccounts());
        }

        if (updateOrganizationEntityDto.getMinimumReduction() != null) {
            contract.setMinimumReduction(updateOrganizationEntityDto.getMinimumReduction());
        }

//        if (updateOrganizationEntityDto.getManagerId() != null && updateOrganizationEntityDto.getManagerId() != contract.getUserContracts().getUser().getId()) {
//            this.userContractService.updateUserContract(contract.getUserContracts(), updateOrganizationEntityDto);
//        }

        this.solutionContractService.update(contract, updateOrganizationEntityDto);
        byte[] document = this.updateDocument(contract);
        contract.setDocument(document);
        this.contractRepository.save(contract);
        return document;
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

    }
}

