package net.foodeals.contract.application.service;

import jakarta.transaction.Transactional;
import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.DTo.upload.ContractCommissionDto;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.repositories.CommissionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Service
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public CommissionService(CommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

    @Transactional
    public Commission createCommission(ContractCommissionDto contractCommissionDto) {
        Commission commission = Commission.builder().build();
        if (contractCommissionDto.getWithCard() != null) {
            commission.setCard(contractCommissionDto.getWithCard());
        }
        if (contractCommissionDto.getWithCash() != null) {
            commission.setCash(contractCommissionDto.getWithCash());
        }
        return commission;
    }

    @Transactional
    public void delete(Commission commission) {
        this.commissionRepository.delete(commission);
    }

    @Transactional
    public Commission update(Commission commission, ContractCommissionDto contractCommissionDto) {
        if (contractCommissionDto.getWithCard() != null) {
            commission.setCard(contractCommissionDto.getWithCard());
        }
        if (contractCommissionDto.getWithCash() != null) {
            commission.setCash(contractCommissionDto.getWithCash());
        }
        return this.commissionRepository.save(commission);
    }

    @Transactional
    public Commission save(Commission commission) {
        return this.commissionRepository.saveAndFlush(commission);
    }

    @Transactional
    public Commission getCommissionByPartnerId(UUID id) {
        return this.commissionRepository.findCommissionByOrganizationId(id);
    }
}