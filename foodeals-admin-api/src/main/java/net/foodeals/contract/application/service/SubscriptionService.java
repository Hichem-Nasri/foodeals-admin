package net.foodeals.contract.application.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.DTo.upload.ContractSubscriptionDto;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.contract.domain.repositories.SubscriptionRepository;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.entities.Enum.PaymentResponsibility;
import net.foodeals.payment.domain.entities.PartnerInfo;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final DeadlinesService deadlinesService;
    private final OrganizationEntityRepository organizationEntityRepository;

    @Transactional
    public Subscription findById(UUID id) {
        return this.subscriptionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Subscription createSubscription(ContractSubscriptionDto contractSubscriptionDto) {
        Subscription subscription = Subscription.builder()
                .amount(new Price(new BigDecimal(contractSubscriptionDto.getAnnualPayment()), Currency.getInstance("MAD")))
                .numberOfDueDates(contractSubscriptionDto.getNumberOfDueDates())
                .duration(contractSubscriptionDto.getDuration())
                .subscriptionStatus(SubscriptionStatus.NOT_STARTED)
                .build();
        return subscription;
    }

    @Transactional
    public void delete(Subscription subscription) {
        this.subscriptionRepository.delete(subscription);
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        return this.subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription update(Subscription subscription, ContractSubscriptionDto contractSubscriptionDto) {
        subscription.setAmount(new Price(new BigDecimal(contractSubscriptionDto.getAnnualPayment()), Currency.getInstance("MAD")));
        subscription.setNumberOfDueDates(contractSubscriptionDto.getNumberOfDueDates());
        OrganizationEntity organizationEntity = this.organizationEntityRepository.findById(subscription.getPartner().id())
                .orElseThrow(() -> new ResourceNotFoundException("organization not found"));
        System.out.println(subscription.getPartner());
        subscription.setPartnerI(organizationEntity);
        subscription.setPartner(new PartnerInfo(organizationEntity.getId(), organizationEntity.getId(), organizationEntity.getPartnerType(), organizationEntity.getName()));
        return this.subscriptionRepository.save(subscription);
    }

    @Transactional
    public void startSubscription(Subscription subscription) {
        OrganizationEntity organization = this.organizationEntityRepository.findById(subscription.getPartner().id())
                .orElseThrow(() -> new ResourceNotFoundException("organization entity not found"));
        subscription.setPartnerI(organization);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(subscription.getDuration());
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        Integer numberOfDueDates = subscription.getNumberOfDueDates();
        subscription.setSubscriptionStatus(SubscriptionStatus.IN_PROGRESS);
        List<Deadlines> deadlines = new ArrayList<>();

        BigDecimal totalAmount = subscription.getAmount().amount();
        BigDecimal amountPerDeadline = totalAmount.divide(BigDecimal.valueOf(numberOfDueDates), RoundingMode.CEILING);
        Long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Long daysPerDeadline = daysBetween / numberOfDueDates;

        LocalDate firstDueDate = startDate.plusDays(daysPerDeadline);

        for (int i = 0; i < numberOfDueDates; i++) {
            LocalDate dueDate = firstDueDate.plusDays(i * daysPerDeadline);
            Deadlines deadline = Deadlines.builder()
                    .subscription(subscription)
                    .dueDate(dueDate)
                    .amount(new Price(amountPerDeadline, Currency.getInstance("MAD")))
                    .status(DeadlineStatus.IN_VALID)
                    .paymentResponsibility(subscription.getPartnerI().subscriptionPayedBySubEntities() ? PaymentResponsibility.PAYED_BY_SUB_ENTITIES : PaymentResponsibility.PAYED_BY_PARTNER)
                    .build();
            deadlines.add(deadline);
        }
        this.deadlinesService.saveAll(deadlines);
    }

    @Transactional
    public Page<Subscription> findAll(Pageable page) {
        return this.subscriptionRepository.findAll(page);
    }

    @Transactional
    public List<Subscription> findByStartDateBetweenAndSubscriptionStatusNot(LocalDate startDate, LocalDate end, SubscriptionStatus status, UUID id) {
        return id == null ? this.subscriptionRepository.findByStartDateBetweenAndSubscriptionStatusNot(startDate, end, status)
                : this.subscriptionRepository.findByPartner_OrganizationIdAndStartDateBetweenAndSubscriptionStatusNot(id, startDate, end, status);
    }

    @Transactional
    public List<Subscription> findByStartDateBetweenAndSubscriptionStatusNotAndId(LocalDate startDate, LocalDate end, SubscriptionStatus status, UUID id) {
        return this.subscriptionRepository.findByPartner_IdAndStartDateBetweenAndSubscriptionStatusNot(id, startDate, end, status);
    }

    @Transactional
    public List<Integer> findDistinctYears() {
        return this.subscriptionRepository.findAvailableYears();
    }

    @Transactional
    public Optional<Subscription> findSubscriptionByPartnerInfoId(UUID id, List<PartnerType> types) {
        return this.subscriptionRepository.findByPartnerIdAndTypeIn(id, types);
    }

    @Transactional
    public Page<Subscription> findByPartnerNameAndTypeIn(String name, List<PartnerType> types, Pageable pageable) {
        return this.subscriptionRepository.findByPartnerNameAndTypeIn(name, types, pageable);
    }
}
