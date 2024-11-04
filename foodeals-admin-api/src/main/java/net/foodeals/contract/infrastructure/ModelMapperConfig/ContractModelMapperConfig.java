package net.foodeals.contract.infrastructure.ModelMapperConfig;

import jakarta.annotation.PostConstruct;
import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.payment.application.dto.response.CommissionPaymentDto;
import net.foodeals.payment.application.dto.response.DeadlinesDto;
import net.foodeals.payment.application.dto.response.SubscriptionPaymentDto;
import net.foodeals.payment.domain.entities.Payment;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.math.BigDecimal;

public class ContractModelMapperConfig {
    private final ModelMapper modelMapper;

    public ContractModelMapperConfig(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
