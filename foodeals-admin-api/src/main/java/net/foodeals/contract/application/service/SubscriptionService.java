package net.foodeals.contract.application.service;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.DTo.upload.ContractSubscriptionDto;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.entities.enums.DeadlineStatus;
import net.foodeals.contract.domain.entities.enums.SubscriptionStatus;
import net.foodeals.contract.domain.repositories.SubscriptionRepository;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final DeadlinesService deadlinesService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, DeadlinesService deadlinesService) {
        this.subscriptionRepository = subscriptionRepository;
        this.deadlinesService = deadlinesService;
    }

    public Subscription findById(UUID id) {
        return this.subscriptionRepository.findById(id).orElse(null);
    }
    public Subscription createSubscription(ContractSubscriptionDto contractSubscriptionDto) {
        Subscription subscription = Subscription.builder().amount(new Price(new BigDecimal(contractSubscriptionDto.getAnnualPayment()), Currency.getInstance("MAD")))
                .numberOfDueDates(contractSubscriptionDto.getNumberOfDueDates())
                .duration(contractSubscriptionDto.getDuration())
                .subscriptionStatus(SubscriptionStatus.NOT_STARTED)
                .build();
        return subscription;
    }

    public void delete(Subscription subscription) {
        this.subscriptionRepository.delete(subscription);
    }

    public Subscription save(Subscription subscription) {
        return this.subscriptionRepository.save(subscription);
    }

    public Subscription update(Subscription subscription, ContractSubscriptionDto contractSubscriptionDto) {
        subscription.setAmount(new Price(new BigDecimal(contractSubscriptionDto.getAnnualPayment()), Currency.getInstance("MAD")));
        subscription.setNumberOfDueDates(contractSubscriptionDto.getNumberOfDueDates());
        return this.subscriptionRepository.save(subscription);
    }

    public void startSubscription(Subscription subscription) {
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
                    Deadlines deadline = Deadlines.builder().subscription(subscription)
                            .dueDate(dueDate)
                            .amount(new Price(amountPerDeadline, Currency.getInstance("MAD")))
                            .status(DeadlineStatus.PENDING)
                            .build();
                    deadlines.add(deadline);
                }
                this.deadlinesService.saveAll(deadlines);
            }

    public Page<Subscription> findAll(Pageable page) {
        return this.subscriptionRepository.findAll(page);
    }
}
