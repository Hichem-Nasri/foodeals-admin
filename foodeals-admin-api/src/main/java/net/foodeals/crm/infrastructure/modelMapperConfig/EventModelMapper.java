package net.foodeals.crm.infrastructure.modelMapperConfig;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.AddressDto;
import net.foodeals.crm.application.dto.responses.*;
import net.foodeals.crm.domain.entities.Event;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
public class EventModelMapper {

    private final ModelMapper modelMapper;

        @Transactional
        @EventListener(ApplicationReadyEvent.class)
        public void config() {
            modelMapper.addConverter(mappingContext -> {
                final Event event = mappingContext.getSource();

                LeadDto leadDto = new LeadDto(event.getLead().getName(), event.getLead().getAvatarPath());
                OffsetDateTime dateTime = OffsetDateTime.parse(event.getCreatedAt().toString());
                LocalDate date = dateTime.toLocalDate();
                return new EventResponse(event.getId(), date.toString(), leadDto, event.getDateAndHour(), event.getObject(), event.getMessage());
            }, Event.class, EventResponse.class);
        }
}
