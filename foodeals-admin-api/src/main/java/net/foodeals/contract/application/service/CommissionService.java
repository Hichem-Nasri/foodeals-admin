package net.foodeals.contract.application.service;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.contract.application.DTo.upload.ContractCommissionDto;
import net.foodeals.contract.domain.entities.Commission;
import net.foodeals.contract.domain.repositories.CommissionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;

@Service
public class CommissionService {

    private final CommissionRepository commissionRepository;


    public CommissionService(CommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

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

    public void delete(Commission commission) {
        this.commissionRepository.delete(commission);
    }

    public Commission update(Commission commission, ContractCommissionDto contractCommissionDto) {
        if (contractCommissionDto.getWithCard() != null) {
            commission.setCard(contractCommissionDto.getWithCard());
        }
        if (contractCommissionDto.getWithCash() != null) {
            commission.setCash(contractCommissionDto.getWithCash());
        }
        return this.commissionRepository.save(commission);
    }
}
