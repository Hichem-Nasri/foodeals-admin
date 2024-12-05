package net.foodeals.contract.application.service;

import jakarta.transaction.Transactional;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.SolutionContract;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.repositories.SolutionContractRepository;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAnOrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.requests.DeliveryPartnerContract;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolutionContractService {

    private final SolutionContractRepository solutionContractRepository;
    private final SolutionService solutionService;
    private final SubscriptionService subscriptionService;
    private final CommissionService comissionService;
    private final OrganizationEntityRepository organizationEntityRepository;



    public SolutionContractService(SolutionContractRepository solutionContractRepository, SolutionService solutionService, SubscriptionService subscriptionService, CommissionService comissionService, OrganizationEntityRepository organizationEntityRepository) {
        this.solutionContractRepository = solutionContractRepository;
        this.solutionService = solutionService;
        this.subscriptionService = subscriptionService;
        this.comissionService = comissionService;
        this.organizationEntityRepository = organizationEntityRepository;
    }

    @Transactional
    public List<SolutionContract> updateDeliveryContract(List<DeliveryPartnerContract> deliveryPartnerContracts, Contract contract) {
        List<SolutionContract> updatedContracts = new ArrayList<>();

        Set<String> newSolutionNames = deliveryPartnerContracts.stream()
                .map(DeliveryPartnerContract::solution)
                .collect(Collectors.toSet());

        Iterator<SolutionContract> iterator = contract.getSolutionContracts().iterator();
        while (iterator.hasNext()) {
            SolutionContract solutionContract = iterator.next();
            if (!newSolutionNames.contains(solutionContract.getSolution().getName())) {
                iterator.remove(); // Remove using iterator
                solutionContract.setSolution(null);
                solutionContract.setContract(null);
                Commission commission = solutionContract.getCommission();
                if (commission != null) {
                    commission.setSolutionContract(null);
                    this.comissionService.delete(commission);
                }
                this.solutionContractRepository.delete(solutionContract);
            }
        }
        for (DeliveryPartnerContract dc : deliveryPartnerContracts) {
            String solutionName = dc.solution();
            Solution solution = solutionService.findByName(solutionName);
            Optional<SolutionContract> existingContract = contract.getSolutionContracts().stream()
                    .filter(sc -> sc.getSolution().getName().equals(solutionName))
                    .findFirst();

            if (existingContract.isPresent()) {
                // Update existing contract
                SolutionContract solutionContract = existingContract.get();
                Commission commission = solutionContract.getCommission();
                commission.setDeliveryAmount(dc.amount());
                commission.setDeliveryCommission(dc.commission());
                comissionService.save(commission);
                updatedContracts.add(solutionContract);
            } else {
                // Create new contract
                Commission commission = new Commission();
                commission.setDeliveryAmount(dc.amount());
                commission.setDeliveryCommission(dc.commission());

                SolutionContract solutionContract = new SolutionContract();
                solutionContract.setSolution(solution);
                solutionContract.setContract(contract);
                solutionContract.setCommission(commission);

                commission.setSolutionContract(solutionContract);
                contract.getSolutionContracts().add(solutionContract);
                solutionContract = solutionContractRepository.save(solutionContract);
                updatedContracts.add(solutionContract);
            }
        }

        return updatedContracts;
    }

    @Transactional
    // solution contracts -> many sub -> single subs  | subscrption clone ->
    public List<SolutionContract> createDeliveryContracts(List<DeliveryPartnerContract> deliveryPartnerContracts, Contract contract) {
        List<SolutionContract> createdContracts = new ArrayList<>();

        for (DeliveryPartnerContract deliveryPartnerContract : deliveryPartnerContracts) {
            // Find or create the Solution
            Solution solution = solutionService.findByName(deliveryPartnerContract.solution());

            // Create Commission
            Commission commission = new Commission();
            commission.setDeliveryAmount(deliveryPartnerContract.amount());
            commission.setDeliveryCommission(deliveryPartnerContract.commission());
            // Set other commission fields if needed

            // Create SolutionContract
            SolutionContract solutionContract = new SolutionContract();
            solutionContract.setSolution(solution);
            solutionContract.setContract(contract);
            solutionContract.setCommission(commission);

            // Set up bidirectional relationships
            commission.setSolutionContract(solutionContract);
            contract.getSolutionContracts().add(solutionContract);
            this.comissionService.save(commission);

            // Save the SolutionContract (this will cascade save the Commission due to CascadeType.ALL)
           solutionContract = solutionContractRepository.save(solutionContract);
            createdContracts.add(solutionContract);
        }
        return createdContracts;
    }

    @Transactional
    public List<SolutionContract> createManySolutionContracts(List<SolutionsContractDto> solutionsContractsDto, Contract contract, Boolean oneSubscription) {
        Subscription globalSubscription;
        OrganizationEntity organizationEntity = contract.getOrganizationEntity();
        if (oneSubscription) {
            globalSubscription = this.subscriptionService.createSubscription(solutionsContractsDto.getFirst().getContractSubscriptionDto());
            organizationEntity.getSubscriptions().add(globalSubscription);
        } else {
            globalSubscription = null;
        }
        List<SolutionContract> solutionContracts =  solutionsContractsDto.stream().map(solutionsContractDto -> {
            Solution solution = this.solutionService.findByName(solutionsContractDto.getSolution());
            SolutionContract solutionContract = SolutionContract.builder().solution(solution)
                    .contract(contract)
                    .build();
            if (solutionsContractDto.getSolution().equals("pro_market") && solutionsContractDto.getContractCommissionDto() != null) {
                Commission commission = this.comissionService.createCommission(solutionsContractDto.getContractCommissionDto());
                commission.setSolutionContract(solutionContract);
                solutionContract.setCommission(commission);
            }
            if (oneSubscription) {
                List<SolutionContract> solutionContractsList = new ArrayList<>(globalSubscription.getSolutionContracts());
                globalSubscription.getSolutions().add(solution);
                globalSubscription.setPartner(new PartnerInfo(contract.getOrganizationEntity().getId(), contract.getOrganizationEntity().getId(), contract.getOrganizationEntity().getPartnerType(),  contract.getOrganizationEntity().getName()));
                solutionContract.setSubscription(globalSubscription);
                solutionContractsList.add(solutionContract);
                globalSubscription.setSolutionContracts(solutionContractsList);
            } else {
                Subscription subscription = this.subscriptionService.createSubscription(solutionsContractDto.getContractSubscriptionDto());
                subscription.getSolutions().add(solution);
                organizationEntity.getSubscriptions().add(subscription);
                subscription.setPartner(new PartnerInfo(contract.getOrganizationEntity().getId(), contract.getOrganizationEntity().getId(), contract.getOrganizationEntity().getPartnerType(), contract.getOrganizationEntity().getName()));
                List<SolutionContract> solutionContractList = new ArrayList<SolutionContract>();
                solutionContractList.add(solutionContract);
                subscription.setSolutionContracts(solutionContractList);
                solutionContract.setSubscription(subscription);
            }
            return this.solutionContractRepository.save(solutionContract);
        }).toList();
        this.organizationEntityRepository.save(organizationEntity);
        return solutionContracts;
    }

    @Transactional
    public void delete(SolutionContract solutionContract) {
        this.solutionContractRepository.softDelete(solutionContract.getId());
    }

    @Transactional
    public void update(Contract contract, CreateAnOrganizationEntityDto UpdateOrganizationEntityDto) {
        List<SolutionContract> solutionContracts = new ArrayList<>(contract.getSolutionContracts());
        solutionContracts.stream().map(solutionContract -> {
            if (!UpdateOrganizationEntityDto.getSolutions().contains(solutionContract.getSolution().getName())) {
                contract.getSolutionContracts().remove(solutionContract);
                solutionContract.setSolution(null);
                solutionContract.setContract(null);
                Commission commission = solutionContract.getCommission();
                if (commission != null) {
                    commission.setSolutionContract(null);
                    this.comissionService.delete(commission);
                }
                Subscription subscription = solutionContract.getSubscription();
                if (subscription.getSolutionContracts().size() != 1) {
                    subscription.getSolutionContracts().remove(solutionContract);
                    solutionContract.setSubscription(null);
                    this.subscriptionService.save(subscription);
                } else {
                    subscription.setSolutionContracts(null);
                    solutionContract.setSubscription(null);
                    contract.getOrganizationEntity().getSubscriptions().remove(subscription);
                    this.organizationEntityRepository.save(contract.getOrganizationEntity());
                    this.subscriptionService.delete(subscription);
                }
                this.solutionContractRepository.delete(solutionContract);
            } else {
                UpdateOrganizationEntityDto.getSolutionsContractDto().forEach(element -> {
                    if (element.getSolution().equals(solutionContract.getSolution().getName())) {
                        if (element.getContractSubscriptionDto() != null) {
                         Subscription subscription = solutionContract.getSubscription();
                         if (subscription != null) {
                             this.subscriptionService.update(subscription, element.getContractSubscriptionDto());
                         }
                        }
                        if (element.getContractCommissionDto() != null) {
                            Commission commission = solutionContract.getCommission();
                            if (commission != null) {
                                this.comissionService.update(commission, element.getContractCommissionDto());
                            }
                        }
                    }
                });
            }
            return solutionContract;
        }).toList();
        List<String> organizationSolutions = contract.getSolutionContracts().stream().map(solutionContract -> solutionContract.getSolution().getName()).toList();
        List<SolutionsContractDto> newSolutions = UpdateOrganizationEntityDto.getSolutionsContractDto().stream().filter(solutionContractDto -> !organizationSolutions.contains(solutionContractDto.getSolution())).toList();
        List<SolutionContract> newSolutionsContracts = newSolutions.stream().map(solutionsContractDto -> {
            Solution solution = this.solutionService.findByName(solutionsContractDto.getSolution());
            SolutionContract solutionContract = SolutionContract.builder().solution(solution)
                    .contract(contract)
                    .build();
            if (solutionsContractDto.getSolution().equals("pro_market") && solutionsContractDto.getContractCommissionDto() != null) {
                Commission commission = this.comissionService.createCommission(solutionsContractDto.getContractCommissionDto());
                commission.setSolutionContract(solutionContract);
                solutionContract.setCommission(commission);
            }
             if (solutionsContractDto.getContractSubscriptionDto() != null) {
                 Subscription subscription = this.subscriptionService.createSubscription(solutionsContractDto.getContractSubscriptionDto());
                List<SolutionContract> solutionContractList = new ArrayList<SolutionContract>();
                solutionContractList.add(solutionContract);
                subscription.setSolutionContracts(solutionContractList);
                solutionContract.setSubscription(subscription);
            }
             return this.solutionContractRepository.save(solutionContract);
        }).toList();
        contract.getSolutionContracts().addAll(newSolutionsContracts);
    }
}