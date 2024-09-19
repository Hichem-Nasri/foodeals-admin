package net.foodeals.payment.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.domain.entities.Payment;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class PaymentModelMapperConfig {
    
    private final ModelMapper modelMapper;

    public PaymentModelMapperConfig(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void paymentModelMapperConfig() {
        modelMapper.addMappings(new PropertyMap<Payment, CommissionPaymentDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getId());
                map(source.getDate(), destination.getDate());
                map(source.getPartnerType(), destination.getPartnerType());
                map(source.getNumberOfOrders(), destination.getNumberOfOrders());
                map(source.getPaymentStatus(), destination.getPaymentStatus());
            }
        });
        modelMapper.addMappings(new PropertyMap<Subscription, SubscriptionPaymentDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getId());
                map(source.getSubscriptionStatus(), destination.getSubscriptionStatus());
            }
        });
    }
}
