package net.foodeals.crm.application.services.implementation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.common.entities.DeletionReason;
import net.foodeals.crm.application.dto.requests.*;
import net.foodeals.crm.application.dto.responses.EventResponse;
import net.foodeals.crm.application.dto.responses.ProspectFilter;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.dto.responses.ProspectStatisticDto;
import net.foodeals.crm.application.services.EventService;
import net.foodeals.crm.application.services.ProspectService;
import net.foodeals.crm.domain.entities.Event;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;
import net.foodeals.crm.domain.repositories.ProspectRepository;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.impl.RegionServiceImpl;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.application.services.ActivityService;
import net.foodeals.organizationEntity.application.services.ContactsService;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProspectServiceImp implements ProspectService {

    private final ActivityService activityService;
    private final ProspectRepository prospectRepository;
    private final ContactsService contactsService;
    private final AddressService addressService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CityService cityService;
    private final RegionServiceImpl regionServiceImpl;
    private final EventService eventService;
    private final SolutionService solutionService;
    private final RoleService roleService;

    @Override
    @Transactional
    public List<ProspectResponse> findAll() {
        return List.of();
    }

    @Override
    @Transactional
    public Page<ProspectResponse> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    @Transactional
    public Page<ProspectResponse> findAll(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public ProspectResponse partialUpdate(UUID id, PartialProspectRequest dto) {
        return null;
    }

    @Override
    @Transactional
    public Page<City> searchCitiesByProspectAddress(List<ProspectType> types, String cityName, String countryName, Pageable pageable) {
        return prospectRepository.findCitiesByProspectAddress(types, cityName, countryName, pageable);
    }

    @Override
    @Transactional
    public Page<Region> searchRegionsByProspectAddress(List<ProspectType> types, String regionName, String countryName, Pageable pageable) {
        return prospectRepository.findRegionsByProspectAddress(types, regionName, countryName, pageable);
    }

    @Override
    @Transactional
    public EventResponse createEvent(UUID id, EventRequest eventRequest) {
        Prospect prospect = this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));

        Event event = this.eventService.create(eventRequest);
        prospect.getEvents().add(event);
        this.prospectRepository.save(prospect);
        return this.modelMapper.map(event, EventResponse.class);
    }

    @Override
    @Transactional
    public Page<EventResponse> getEvents(UUID id, Pageable pageable) {
        Prospect prospect = this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));

        List<Event> nonDeletedEvents = prospect.getEvents() != null
                ? prospect.getEvents().stream()
                .filter(event -> event.getDeletedAt() == null)
                .collect(Collectors.toList())
                : new ArrayList<>();

        Page<Event> events = new PageImpl<>(nonDeletedEvents, pageable, nonDeletedEvents.size());
        return events.map(event -> this.modelMapper.map(event, EventResponse.class));
    }

    @Override
    @Transactional
    public EventResponse getEventById(UUID prospectId, UUID eventId) {
        Prospect prospect = this.prospectRepository.findById(prospectId).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + prospectId.toString()));
        Optional<Event> event = prospect.getEvents().stream().filter(eventItem -> eventItem.getId().equals(eventId)).findFirst();
        if (event.isEmpty()) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId.toString());
        }
        return this.modelMapper.map(event.get(), EventResponse.class);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(UUID prospectId, UUID eventId, EventRequest dto) {
        Prospect prospect = this.prospectRepository.findById(prospectId).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + prospectId.toString()));
        Optional<Event> event = prospect.getEvents().stream().filter(eventItem -> eventItem.getId().equals(eventId)).findFirst();
        if (event.isEmpty()) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId.toString());
        }

        Event updatedEvent = this.eventService.update(eventId, dto);
        return this.modelMapper.map(updatedEvent, EventResponse.class);
    }

    @Override
    @Transactional
    public EventResponse partialUpdateEvent(UUID prospectId, UUID eventId, PartialEventRequest dto) {
        Prospect prospect = this.prospectRepository.findById(prospectId).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + prospectId.toString()));
        Optional<Event> event = prospect.getEvents().stream().filter(eventItem -> eventItem.getId().equals(eventId)).findFirst();
        if (event.isEmpty()) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId.toString());
        }

        Event updatedEvent = this.eventService.partialUpdate(eventId, dto);
        return this.modelMapper.map(updatedEvent, EventResponse.class);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID prospectId, UUID eventId) {
        Prospect prospect = this.prospectRepository.findById(prospectId).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + prospectId.toString()));
        Optional<Event> event = prospect.getEvents().stream().filter(eventItem -> eventItem.getId().equals(eventId)).findFirst();
        if (event.isEmpty()) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId.toString());
        }

        this.eventService.delete(eventId);
    }

    @Override
    @Transactional
    public ProspectStatisticDto statistics(List<ProspectType> type) {

        Long inProgressCount = this.prospectRepository.countByStatusAndTypeInAndDeletedAtIsNull(ProspectStatus.IN_PROGRESS, type);
        Long totalCount = this.prospectRepository.countByTypeIn(type);
        Long canceledCount = this.prospectRepository.countByStatusAndTypeInAndDeletedAtIsNull(ProspectStatus.CANCELED, type);
        Long validCount = this.prospectRepository.countByStatusAndTypeInAndDeletedAtIsNull(ProspectStatus.VALID, type);

        return new ProspectStatisticDto(inProgressCount, totalCount, canceledCount, validCount);
    }

    @Transactional
    @Override
    public Page<UpdateDetails> getDeletionDetails(UUID uuid, Pageable page) {
        Prospect prospect = this.prospectRepository.getEntity(uuid).orElseThrow(() -> new EntityNotFoundException("prospect not found with uuid: " + uuid));

        List<DeletionReason> deletionReasons = prospect.getDeletionReasons();

        int start = (int)page.getOffset();
        int end = Math.min(start + page.getPageSize(), deletionReasons.size());
        List<DeletionReason> content = deletionReasons.subList(start, end);

        return new PageImpl<>(content, page, deletionReasons.size()).map(d -> new UpdateDetails(d.getType(), d.getDetails(), d.getReason(), Date.from(d.getCreatedAt())));
    }

    @Override
    @Transactional
    public String changeStatus(UUID id, ProspectStatusRequest dto) {
        Prospect prospect = this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("prospect not found with id " + id.toString()));
        prospect.setStatus(dto.status());
        if (dto.reason() != null) {
            DeletionReason deletionReason = DeletionReason.builder()
                    .details(dto.reason().details())
                    .reason(dto.reason().reason())
                    .type(dto.reason().action())
                    .build();
            prospect.getDeletionReasons().add(deletionReason);
        }
        this.prospectRepository.save(prospect);
        return "status changed successfully";
    }

    @Override
    @Transactional
    public Page<ProspectResponse> findAllWithFilters(ProspectFilter filter, Pageable pageable) {
        Page<Prospect> prospects = prospectRepository.findAllWithFilters(filter, pageable);
        return prospects.map(p -> this.modelMapper.map(p, ProspectResponse.class));
    }

    @Override
    @Transactional
    public ProspectResponse findById(UUID id) {
        return this.modelMapper.map(this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("prospect not found")), ProspectResponse.class);
    }

    @Transactional
    @Override
    public Page<Prospect> searchProspectsByName(String name, List<ProspectType> types, Pageable pageable, boolean includeDeleted) {
        Page<Prospect> entities;
        if (name != null && !name.isEmpty()) {
            entities = prospectRepository.findByNameContainingAndTypeInAndDeletedAtIs(name, types, includeDeleted, pageable);
        } else {
            entities = prospectRepository.findByTypeInAndDeletedAtIs(types, includeDeleted, pageable);
        }
        return entities;
    }

    @Override
    @Transactional
    public ProspectResponse create(ProspectRequest dto) {
        Prospect prospect = Prospect.builder().name(dto.companyName())
                .type(dto.type())
                .status(ProspectStatus.IN_PROGRESS)
                .build();
        final Contact contact = Contact.builder().name(dto.responsible().getName())
                .email(dto.responsible().getEmail())
                .phone(dto.responsible().getPhone())
                .build();

        final User manager = this.userService.findById(dto.manager_id());

        final String email = SecurityContextHolder.getContext().getAuthentication().getName();

        final User creator = this.userService.findByEmail(email);

        if (dto.event() != null) {
            Event event = this.eventService.create(dto.event());
            prospect.getEvents().add(event);
        }

        prospect.setLead(manager);
        prospect.setCreator(creator);

        if (manager.getManagedProspects() == null) {
            manager.setManagedProspects(new ArrayList<>(List.of(prospect)));
        } else {
            manager.getManagedProspects().add(prospect);
        }


        if (creator.getCreatedProspects() == null) {
            manager.setCreatedProspects(new ArrayList<>(List.of(prospect)));
        } else {
            manager.getCreatedProspects().add(prospect);
        }

        Set<Activity> activities = this.activityService.getActivitiesByName(dto.activities());

        prospect.setActivities(activities);
        activities.forEach(activity -> {
            activity.getProspects().add(prospect);
        });
        this.activityService.saveAll(activities);
        prospect.setActivities(activities);

        prospect.getContacts().add(contact);

        Set<Solution> solutions = this.solutionService.getSolutionsByNames(dto.solutions());

        for (Solution solution : solutions) {
            solution.getProspects().add(prospect);
        }
        this.solutionService.saveAll(solutions);
        prospect.setSolutions(solutions);

        AddressRequest addressRequest = new AddressRequest(dto.address().country(), dto.address().address(),  dto.address().state(), dto.address().city(), dto.address().region(), dto.address().iframe());
        Address address = this.addressService.create(addressRequest);

        prospect.setAddress(address);

        this.prospectRepository.save(prospect);
        this.activityService.saveAll(activities);
        this.solutionService.saveAll(solutions);

        return this.modelMapper.map(this.prospectRepository.save(prospect), ProspectResponse.class);
    }

    @Override
    @Transactional
    public ProspectResponse update(UUID id, ProspectRequest dto) {
        Prospect prospect = this.prospectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));

        prospect.setName(dto.companyName());
        prospect.setStatus(dto.status());
        prospect.setType(dto.type());

        Contact contact = prospect.getContacts().get(0);
        contact.setName(dto.responsible().getName());
        contact.setEmail(dto.responsible().getEmail());
        contact.setPhone(dto.responsible().getPhone());

        User oldManager = prospect.getLead();
        if (oldManager != null) {
            oldManager.getManagedProspects().remove(prospect);
        }

        User manager = this.userService.findById(dto.manager_id());
        manager.getManagedProspects().add(prospect);
        prospect.setLead(manager);

        Set<Activity> activities = this.activityService.getActivitiesByName(dto.activities());
        prospect.getActivities().forEach((Activity activity) -> {
            if (!activities.contains(activity)) {
                activity.getProspects().remove(prospect);
            }
        });
        prospect.setActivities(activities);
        activities.forEach(activity -> activity.getProspects().add(prospect));

// Solutions
        Set<Solution> solutions = this.solutionService.getSolutionsByNames(dto.solutions());
        prospect.getSolutions().forEach((Solution solution) -> {
            if (!solutions.contains(solution)) {
                solution.getProspects().remove(prospect);
            }
        });
        for (Solution solution : solutions) {
            solution.getProspects().add(prospect);
        }
        prospect.setSolutions(solutions);

        Address existingAddress = prospect.getAddress();
        AddressRequest addressRequest = new AddressRequest(dto.address().country(), dto.address().address(),  dto.address().state(), dto.address().city(),  dto.address().region(),  dto.address().iframe());
        this.addressService.update(existingAddress.getId(), addressRequest);

        Prospect updatedProspect = this.prospectRepository.save(prospect);
        this.activityService.saveAll(activities);
        this.solutionService.saveAll(solutions);

        return this.modelMapper.map(updatedProspect, ProspectResponse.class);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Prospect prospect = this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));
        this.prospectRepository.softDelete(id);
    }

    //    @Override
//    public ProspectResponse partialUpdate(UUID id, PartialProspectRequest dto) {
//            Prospect prospect = this.prospectRepository.findById(id)
//                    .orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));
//
//            if (dto.companyName() != null) {
//                prospect.setName(dto.companyName());
//            }
//            if (dto.status() != null) {
//                prospect.setStatus(dto.status());
//            }
//
//            if (dto.responsible() != null) {
//                Contact contact = prospect.getContacts().get(0);
//                if (dto.responsible().getName() != null) {
//                    contact.setName(dto.responsible().getName());
//                }
//                if (dto.responsible().getEmail() != null) {
//                    contact.setEmail(dto.responsible().getEmail());
//                }
//                if (dto.responsible().getPhone() != null) {
//                    contact.setPhone(dto.responsible().getPhone());
//                }
//            }
//
//            if (dto.manager_id() != null) {
//                User oldManager = prospect.getLead();
//                if (oldManager != null) {
//                    oldManager.getManagedProspects().remove(prospect);
//                }
//
//                User manager = this.userService.findById(dto.manager_id());
//                manager.getManagedProspects().add(prospect);
//                prospect.setLead(manager);
//            }
//
//            if (dto.activities() != null) {
//                Set<Activity> activities = this.activityService.getActivitiesByName(dto.activities());
//                prospect.getActivities().forEach((Activity activity) -> {
//                    if (!activities.contains(activity)) {
//                        activity.getProspects().remove(prospect);
//                    }
//                });
//                prospect.setActivities(activities);
//                activities.forEach(activity -> activity.getProspects().add(prospect));
//                this.activityService.saveAll(activities);
//            }
//
//        if (dto.solutions() != null) {
//            Set<Solution> solutions = this.solutionService.getSolutionsByNames(dto.solutions());
//            prospect.getSolutions().forEach((Solution solution) -> {
//                if (!solutions.contains(solution)) {
//                    solution.getProspects().remove(prospect);
//                }
//            });
//            prospect.setSolutions(solutions);
//            solutions.forEach(solution -> solution.getProspects().add(prospect));
//            this.solutionService.saveAll(solutions);
//        }
//
//            if (dto.address() != null) {
//                Address existingAddress = prospect.getAddress();
//                AddressRequest addressRequest = new AddressRequest(dto.address().country(), dto.address().address(),  dto.address().state(),dto.address().city(),  dto.address().region(),  dto.address().iframe());
//                this.addressService.update(existingAddress.getId(), addressRequest);
//            }
//
//            Prospect updatedProspect = this.prospectRepository.save(prospect);
//
//            return this.modelMapper.map(updatedProspect, ProspectResponse.class);
//    }
}