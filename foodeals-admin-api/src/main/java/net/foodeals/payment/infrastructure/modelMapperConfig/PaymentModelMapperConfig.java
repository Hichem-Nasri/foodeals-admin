package net.foodeals.payment.infrastructure.modelMapperConfig;

import jakarta.annotation.PostConstruct;
import net.foodeals.payment.application.dto.response.PaymentDto;
import net.foodeals.payment.domain.Payment;
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
        modelMapper.addMappings(new PropertyMap<Payment, PaymentDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getId());
                map(source.getDate(), destination.getDate());
                map(source.getPartnerType(), destination.getPartnerType());
                map(source.getNumberOfOrders(), destination.getNumberOfOrders());
                map(source.getPaymentStatus(), destination.getPaymentStatus());
            }
        });
    }
}
