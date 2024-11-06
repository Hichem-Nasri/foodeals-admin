package net.foodeals.subEntitie.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.application.services.OfferService;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsSubEntitiesDto;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.application.dtos.responses.PartnerSubEntityDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
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
    private final OfferService offerService;

    @PostConstruct
    private void postConstruct() {

        mapper.addConverter(mappingContext -> {
            SubEntity subEntitie = mappingContext.getSource();

            OffsetDateTime dateTime = OffsetDateTime.parse(subEntitie.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(subEntitie.getId(),subEntitie.getName(), subEntitie.getAvatarPath());
            Optional<User> responsible = subEntitie.getUsers().stream().filter(user -> user.getRole().getName().equals("MANAGER")).findFirst();

            ResponsibleInfoDto responsibleInfoDto = ResponsibleInfoDto.builder().build();
            if (responsible.isPresent()) {
                User user = responsible.get();
                responsibleInfoDto.setName(user.getName());
                responsibleInfoDto.setAvatarPath(user.getAvatarPath());
                responsibleInfoDto.setPhone(user.getPhone());
                responsibleInfoDto.setEmail(user.getEmail());
            } else {
                Contact user = subEntitie.getContacts().getFirst();
                responsibleInfoDto.setName(user.getName());
                responsibleInfoDto.setAvatarPath("");
                responsibleInfoDto.setPhone(user.getPhone());
                responsibleInfoDto.setEmail(user.getEmail());
            }
            List<String> solutions = subEntitie.getSolutions().stream().map(solution -> solution.getName()).toList();
            String city = subEntitie.getAddress().getRegion().getCity().getName();
            Integer users = subEntitie.getUsers().size();
            SubEntityType subEntityType = subEntitie.getType();
            Integer donations = this.donationService.countByDonor_Id(subEntitie.getId());
            Integer recovered = this.donationService.countByReceiver_Id(subEntitie.getId());
            return new AssociationsSubEntitiesDto(date.toString(), partnerInfoDto, responsibleInfoDto, users, donations, recovered, city, solutions);
        }, SubEntity.class, AssociationsSubEntitiesDto.class);

        mapper.addConverter(mappingContext -> {
            SubEntity subEntity = mappingContext.getSource(); // Change OrganizationEntity to SubEntity
            PartnerSubEntityDto subEntityDto = new PartnerSubEntityDto(); // Change OrganizationEntityDto to PartnerSubEntityDto

            OffsetDateTime dateTime = OffsetDateTime.parse(subEntity.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            subEntityDto.setCreatedAt(date.toString());
            subEntityDto.setId(subEntity.getId());

            Optional<Contact> firstContact = Optional.ofNullable(subEntity.getContacts())
                    .flatMap(list -> list.stream().findFirst());
            firstContact.ifPresent(contact -> {
                ContactDto contactDto = new ContactDto(contact.getName(), contact.getEmail(), contact.getPhone());
                subEntityDto.setContactDto(contactDto);
            });

            PartnerInfoDto partnerInfoDto = new PartnerInfoDto(subEntity.getId(), subEntity.getName(), subEntity.getAvatarPath());
            subEntityDto.setPartnerInfoDto(partnerInfoDto);

            subEntityDto.setReference(subEntity.getId());

            Long offers = this.offerService.countByPublisherId(subEntity.getId());
            Long orders = this.offerService.countOrdersByPublisherInfoId(subEntity.getId());
            Long users = Long.valueOf(subEntity.getUsers().size());
            String city = subEntity.getAddress().getRegion().getCity().getName();
            List<String> solutions = subEntity.getSolutions().stream().map(s -> s.getName()).toList();

            subEntityDto.setOffers(offers);
            subEntityDto.setOrders(orders);
            subEntityDto.setUsers(users);
            subEntityDto.setCity(city);
            subEntityDto.setSolutions(solutions);

            return subEntityDto;
        }, SubEntity.class, PartnerSubEntityDto.class);
    }
}
