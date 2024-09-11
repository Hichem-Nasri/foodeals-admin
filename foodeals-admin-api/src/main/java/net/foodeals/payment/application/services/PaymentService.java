package net.foodeals.payment.application.services;

import jakarta.transaction.Transactional;
import net.foodeals.contract.application.service.CommissionService;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.payment.application.dto.response.PaymentDto;
import net.foodeals.payment.domain.Payment;
import net.foodeals.payment.domain.entities.Enum.PartnerType;
import net.foodeals.payment.domain.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final CommissionService commissionService;


    public PaymentService(PaymentRepository paymentRepository, ModelMapper modelMapper, CommissionService commissionService) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.commissionService = commissionService;
    }

    public Page<Payment> getPayments(Pageable page) {
        return this.paymentRepository.findAll(page);
    }

    @Transactional
    public PaymentDto toPaymentDto(Payment payment) {
        PaymentDto paymentDto = this.modelMapper.map(payment, PaymentDto.class);
        OrganizationEntity organizationEntity = payment.getPartnerType() == PartnerType.ORGANIZATION_ENTITY ? payment.getOrganizationEntity()
                    : payment.getSubEntity().getOrganizationEntity();

        Commission commission = this.commissionService.getCommissionByPartnerName(organizationEntity.getName());

        Double commissionTotal = ((Double)(commission.getCard().doubleValue() / 100)) * payment.getPaymentsWithCard() + ((Double)(commission.getCash().doubleValue() / 100)) * payment.getPaymentsWithCash();
        Double difference = payment.getPaymentsWithCard() - commissionTotal;
        Double toPay = difference < 0 ? 0 : difference;
        paymentDto.setToPay(toPay.toString());
        Double toReceive = difference < 0 ? difference : 0;
        paymentDto.setToReceive(toReceive.toString());
        paymentDto.setFoodealsCommission(commissionTotal.toString());
        PartnerInfoDto partnerInfoDto = new PartnerInfoDto(organizationEntity.getName(), organizationEntity.getAvatarPath());
        paymentDto.setPartnerInfoDto(partnerInfoDto);
        return paymentDto;
    }
}
