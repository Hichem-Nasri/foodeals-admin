package net.foodeals.contract.application.service;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.DTo.upload.ContractSubscriptionDto;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createSubscription(ContractSubscriptionDto contractSubscriptionDto) {
        Subscription subscription = Subscription.builder().amount(new Price(new BigDecimal(contractSubscriptionDto.getAnnualPayment()), Currency.getInstance("MAD")))
                .numberOfDueDates(contractSubscriptionDto.getNumberOfDueDates())
                .duration(contractSubscriptionDto.getDuration())
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
}
