package net.foodeals.subEntitie.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsSubEntitiesDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
@AllArgsConstructor
public class SubEntityModelMapperCnf {

    private final ModelMapper mapper;
    private final DonationService donationService;

    @PostConstruct
    private void postConstruct() {

        mapper.addConverter(mappingContext -> {
            SubEntity subEntitie = mappingContext.getSource();

            OffsetDateTime dateTime = OffsetDateTime.parse(subEntitie.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(subEntitie.getName(), subEntitie.getAvatarPath());
            Optional<User> responsible = subEntitie.getUsers().stream().filter(user -> user.getRole().getName().equals("MANAGER")).findFirst();

            ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().build();
            responsible.ifPresent(user -> {
                responsibleInfoDto.setName(user.getName());
                responsibleInfoDto.setAvatarPath(user.getAvatarPath());
                responsibleInfoDto.setPhone(user.getPhone());
                responsibleInfoDto.setEmail(user.getEmail());
            });
            List<String> solutions = subEntitie.getSolutions().stream().map(solution -> solution.getName()).toList();
            String city = subEntitie.getAddress().getRegion().getCity().getName();
//            ContractStatus contractStatus = subEntitie.getContract().getContractStatus();
            Integer users = subEntitie.getUsers().size();
            SubEntityType subEntityType = subEntitie.getType();
            Integer donations = this.donationService.countByDonor_Id(subEntitie.getId());
            Integer recovered = this.donationService.countByReceiver_Id(subEntitie.getId());
            return new AssociationsSubEntitiesDto(date.toString(), partnerInfoDto, responsibleInfoDto, users, donations, recovered, city, solutions, subEntityType);
        }, SubEntity.class, AssociationsSubEntitiesDto.class);
    }
}
