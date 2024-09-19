package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import net.foodeals.organizationEntity.application.dtos.responses.OrganizationEntityDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class OrganizationEntityModelMapper {

    @Autowired
    private ModelMapper mapper;

    @PostConstruct
    private void postConstruct() {
        // Configure ModelMapper for simple property mappings
        mapper.addMappings(new PropertyMap<OrganizationEntity, OrganizationEntityDto>() {
            @Override
            protected void configure() {
                map(source.getAvatarPath(), destination.getAvatarPath());
                map(source.getName(), destination.getName());
                map(source.getContract().getContractStatus(), destination.getContractStatus());
            }
        });
    }

    public OrganizationEntityDto mapOrganizationEntity(OrganizationEntity source) {
        OrganizationEntityDto destination = mapper.map(source, OrganizationEntityDto.class);

        OffsetDateTime dateTime = OffsetDateTime.parse(source.getCreatedAt().toString());
        LocalDate date = dateTime.toLocalDate();

        destination.setCreatedAt(date.toString());

        Optional<Contact> firstContact = Optional.ofNullable(source.getContacts())
                .flatMap(list -> list.stream().findFirst());
        Optional<User> manager = Optional.ofNullable(source.getContract().getUserContracts().getUser());

        manager.ifPresent(salesManger -> {
            destination.setManager(salesManger.getName().firstName() + " " + salesManger.getName().lastName());
        });
        firstContact.ifPresent(contact -> {
            destination.setEmail(contact.getEmail());
            destination.setPhone(contact.getPhone());
        });

        return destination;
    }
}

