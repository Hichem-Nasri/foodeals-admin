package net.foodeals.contract.application.service;

import jakarta.transaction.Transactional;
import net.foodeals.contract.application.DTo.upload.SolutionsContractDto;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.SolutionContract;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.repositories.SolutionContractRepository;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.domain.entities.Solution;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolutionContractService {

    private final SolutionContractRepository solutionContractRepository;
    private final SolutionService solutionService;
    private final SubscriptionService subscriptionService;
    private final CommissionService comissionService;


    public SolutionContractService(SolutionContractRepository solutionContractRepository, SolutionService solutionService, SubscriptionService subscriptionService, CommissionService comissionService) {
        this.solutionContractRepository = solutionContractRepository;
        this.solutionService = solutionService;
        this.subscriptionService = subscriptionService;
        this.comissionService = comissionService;
    }

    public List<SolutionContract> createManySolutionContracts(List<SolutionsContractDto> solutionsContractsDto, Contract contract, Boolean oneSubscription) {
        Subscription globalSubscription;
        if (oneSubscription) {
            globalSubscription = this.subscriptionService.createSubscription(solutionsContractsDto.getFirst().getContractSubscriptionDto());
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
                solutionContract.setSubscription(globalSubscription);
                solutionContractsList.add(solutionContract);
                globalSubscription.setSolutionContracts(solutionContractsList);
            } else {
                Subscription subscription = this.subscriptionService.createSubscription(solutionsContractDto.getContractSubscriptionDto());
                List<SolutionContract> solutionContractList = new ArrayList<SolutionContract>();
                solutionContractList.add(solutionContract);
                subscription.setSolutionContracts(solutionContractList);
                solutionContract.setSubscription(subscription);
            }
            return this.solutionContractRepository.save(solutionContract);
        }).toList();
        return solutionContracts;
    }

    public void delete(SolutionContract solutionContract) {
        this.solutionContractRepository.softDelete(solutionContract.getId());
    }

    @Transactional
    public void update(Contract contract, UpdateOrganizationEntityDto UpdateOrganizationEntityDto) {
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