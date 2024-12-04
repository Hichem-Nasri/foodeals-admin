package net.foodeals.crm.infrastructure.modelMapperConfig;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.AddressDto;
import net.foodeals.crm.application.dto.requests.EventRequest;
import net.foodeals.crm.application.dto.responses.*;
import net.foodeals.crm.domain.entities.Event;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.location.application.dtos.responses.CityResponse;
import net.foodeals.location.application.dtos.responses.CountryResponse;
import net.foodeals.location.application.dtos.responses.RegionResponse;
import net.foodeals.location.application.dtos.responses.StateResponse;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;
import net.foodeals.user.domain.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProspectModelMapper {
    private final ModelMapper modelMapper;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void config() {

        modelMapper.addConverter(mappingContext -> {
            final User user = mappingContext.getSource();

            return new CreatorInfoDto(user.getName(), user.getAvatarPath(), user.getId());

        }, User.class, CreatorInfoDto.class);

        modelMapper.addConverter(mappingContext -> {
            final Prospect prospect = mappingContext.getSource();

            return new PartnerInfoDto(prospect.getId(), prospect.getName(), "", prospect.getAddress().getRegion().getCity().getName());
        }, Prospect.class, PartnerInfoDto.class);

        modelMapper.addConverter(mappingContext -> {
            final User user = mappingContext.getSource();

            return new ManagerInfoDto(user.getName(), user.getAvatarPath(), user.getId());

        }, User.class, ManagerInfoDto.class);

        this.modelMapper.addConverter(mappingContext -> {
            final Prospect prospect = mappingContext.getSource();
            OffsetDateTime dateTime = OffsetDateTime.parse(prospect.getCreatedAt().toString());
            LocalDate date = dateTime.toLocalDate();

            String category = prospect.getActivities().size() > 0 ? prospect.getActivities().iterator().next().getName() : "";

            Contact contact = prospect.getContacts().get(0);

            ContactDto contactInfo = new ContactDto(contact.getName(), contact.getEmail(), contact.getPhone());

            CityResponse cityResponse = this.modelMapper.map(prospect.getAddress().getRegion().getCity(), CityResponse.class);
            StateResponse stateResponse = this.modelMapper.map(prospect.getAddress().getRegion().getCity().getState(), StateResponse.class);
            CountryResponse countryResponse = this.modelMapper.map(prospect.getAddress().getRegion().getCity().getState().getCountry(), CountryResponse.class);
            RegionResponse regionResponse = this.modelMapper.map(prospect.getAddress().getRegion(), RegionResponse.class);
            String address = prospect.getAddress().getAddress();
            ProspectAddress addressDto = new ProspectAddress(address, countryResponse, cityResponse, stateResponse, regionResponse, null);

            final User creator = prospect.getCreator();
            final User lead = prospect.getLead();

            CreatorInfoDto creatorInfoDto = this.modelMapper.map(creator, CreatorInfoDto.class);
            ManagerInfoDto managerInfoDto = this.modelMapper.map(lead, ManagerInfoDto.class);

            List<String> solutionNames = prospect.getSolutions().stream()
                    .map(Solution::getName)
                    .collect(Collectors.toList());

            ProspectStatus status =  prospect.getStatus();
            List<EventResponse> eventResponses = prospect.getEvents().size() != 0
                    ? prospect.getEvents().stream()
                    .filter(event -> event.getDeletedAt() == null)
                    .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                    .map((Event event) -> this.modelMapper.map(event, EventResponse.class))
                    .toList()
                    : null;
            return new ProspectResponse(prospect.getId(), date.toString(), prospect.getName(), category, contactInfo, addressDto, creatorInfoDto, managerInfoDto, status, eventResponses, solutionNames, prospect.getType());
        }, Prospect.class, ProspectResponse.class);
    }
}
