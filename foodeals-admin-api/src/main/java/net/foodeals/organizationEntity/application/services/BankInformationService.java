package net.foodeals.organizationEntity.application.services;

import net.foodeals.organizationEntity.application.dto.upload.EntityBankInformationDto;
import net.foodeals.organizationEntity.domain.entities.BankInformation;
import net.foodeals.organizationEntity.domain.repositories.BankInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class BankInformationService {
    private final BankInformationRepository bankInformationRepository;


    public BankInformationService(BankInformationRepository bankInformationRepository) {
        this.bankInformationRepository = bankInformationRepository;
    }

    public  void delete(BankInformation bankInformation) {
        this.bankInformationRepository.softDelete(bankInformation.getId());
    }

    public BankInformation update(BankInformation bankInformation, EntityBankInformationDto entityBankInformationDto) {
        if (entityBankInformationDto.getBeneficiaryName() != null) {
            bankInformation.setBeneficiaryName(entityBankInformationDto.getBeneficiaryName());
        }
        if (entityBankInformationDto.getBankName() != null) {
            bankInformation.setBankName(entityBankInformationDto.getBankName());
        }
        if (entityBankInformationDto.getRib() != null) {
            bankInformation.setRib(entityBankInformationDto.getRib());
        }
        return this.bankInformationRepository.save(bankInformation);
    }
}
