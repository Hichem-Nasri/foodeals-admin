package net.foodeals.crm.infrastructure.modelMapperConfig;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.AddressDto;
import net.foodeals.crm.application.dto.responses.CreatorInfoDto;
import net.foodeals.crm.application.dto.responses.ManagerInfoDto;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.services.ProspectService;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.dtos.responses.ResponsibleInfoDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.user.application.dtos.responses.UserInfoDto;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
public class modelMapperConf {
    private final ModelMapper modelMapper;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void config() {

        modelMapper.addConverter(mappingContext -> {
            final User user = mappingContext.getSource();

            return new CreatorInfoDto(user.getName(), user.getAvatarPath());

        }, User.class, CreatorInfoDto.class);

        modelMapper.addConverter(mappingContext -> {
            final User user = mappingContext.getSource();

            return new ManagerInfoDto(user.getName(), user.getAvatarPath());

        }, User.class, ManagerInfoDto.class);

        this.modelMapper.addConverter(mappingContext -> {
            final Prospect prospect = mappingContext.getSource();
            OffsetDateTime dateTime = OffsetDateTime.parse(prospect.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            String category = prospect.getActivities().iterator().next().getName();

            Contact contact = prospect.getContacts().get(0);

            ContactDto contactInfo = new ContactDto(contact.getName(), contact.getEmail(), contact.getPhone());

            String city = prospect.getAddress().getCity().getName();
            String region =  prospect.getAddress().getRegion().getName();
            String address =  prospect.getAddress().getAddress();
            AddressDto addressDto = new AddressDto(city, address, region);

            final User creator = prospect.getCreator();
            final User lead = prospect.getLead();

            CreatorInfoDto creatorInfoDto = this.modelMapper.map(creator, CreatorInfoDto.class);
            ManagerInfoDto managerInfoDto = this.modelMapper.map(lead, ManagerInfoDto.class);

            ProspectStatus status =  prospect.getStatus();


            return new ProspectResponse(prospect.getId(), date.toString(), prospect.getName(), category, contactInfo, addressDto, creatorInfoDto, managerInfoDto, "", status);
        }, Prospect.class, ProspectResponse.class);
    }
}
