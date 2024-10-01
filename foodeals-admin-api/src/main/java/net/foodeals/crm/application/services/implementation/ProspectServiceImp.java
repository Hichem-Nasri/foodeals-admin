package net.foodeals.crm.application.services.implementation;

import lombok.AllArgsConstructor;
import net.foodeals.crm.application.dto.requests.ProspectPartialRequest;
import net.foodeals.crm.application.dto.requests.ProspectRequest;
import net.foodeals.crm.application.dto.responses.ProspectResponse;
import net.foodeals.crm.application.services.ProspectService;
import net.foodeals.crm.domain.entities.Prospect;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.repositories.ProspectRepository;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.application.services.CityService;
import net.foodeals.location.application.services.RegionService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Region;
import net.foodeals.organizationEntity.application.services.ActivityService;
import net.foodeals.organizationEntity.application.services.ContactsService;
import net.foodeals.organizationEntity.domain.entities.Activity;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public final class ProspectServiceImp implements ProspectService {

    private final ActivityService activityService;
    private final ProspectRepository prospectRepository;
    private final ContactsService contactsService;
    private final AddressService addressService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CityService cityService;
    private final RegionService regionService;

    @Override
    public List<ProspectResponse> findAll() {
        return List.of();
    }

    @Override
    public Page<ProspectResponse> findAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public Page<ProspectResponse> findAll(Pageable pageable) {
        return this.prospectRepository.findAll(pageable).map(prospect -> this.modelMapper.map(prospect, ProspectResponse.class));
    }

    @Override
    public ProspectResponse partialUpdate(UUID id, ProspectPartialRequest dto) {
            Prospect prospect = this.prospectRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));

            if (dto.companyName() != null) {
                prospect.setName(dto.companyName());
            }
            if (dto.status() != null) {
                prospect.setStatus(dto.status());
            }

            if (dto.responsible() != null) {
                Contact contact = prospect.getContacts().get(0);
                if (dto.responsible().getName() != null) {
                    contact.setName(dto.responsible().getName());
                }
                if (dto.responsible().getEmail() != null) {
                    contact.setEmail(dto.responsible().getEmail());
                }
                if (dto.responsible().getPhone() != null) {
                    contact.setPhone(dto.responsible().getPhone());
                }
            }

            if (dto.manager_id() != null) {
                User oldManager = prospect.getLead();
                if (oldManager != null) {
                    oldManager.getManagedProspects().remove(prospect);
                }

                User manager = this.userService.findById(dto.manager_id());
                manager.getManagedProspects().add(prospect);
                prospect.setLead(manager);
            }

            if (dto.activities() != null) {
                Set<Activity> activities = this.activityService.getActivitiesByName(dto.activities());
                prospect.getActivities().forEach((Activity activity) -> {
                    if (!activities.contains(activity)) {
                        activity.getProspects().remove(prospect);
                    }
                });
                prospect.setActivities(activities);
                activities.forEach(activity -> activity.getProspects().add(prospect));
                this.activityService.saveAll(activities);
            }

            if (dto.address() != null) {
                Address existingAddress = prospect.getAddress();
                if (dto.address().address() != null) {
                    existingAddress.setAddress(dto.address().address());
                }
                if (dto.address().city() != null) {
                    City city = this.cityService.findByName(dto.address().city());
                    existingAddress.setCity(city);
                }
                if (dto.address().region() != null) {
                    Region region = this.regionService.findByName(dto.address().region());
                    existingAddress.setRegion(region);
                }
            }

            Prospect updatedProspect = this.prospectRepository.save(prospect);

            return this.modelMapper.map(updatedProspect, ProspectResponse.class);
    }

    @Override
    public ProspectResponse findById(UUID id) {
        return this.modelMapper.map(this.prospectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("prospect not found")), ProspectResponse.class);
    }

    @Override
    public ProspectResponse create(ProspectRequest dto) {
        Prospect prospect = Prospect.builder().name(dto.companyName())
                .status(ProspectStatus.IN_PROGRESS)
                .build();
        final Contact contact = Contact.builder().name(dto.responsible().getName())
                .email(dto.responsible().getEmail())
                .phone(dto.responsible().getPhone())
                .build();

        final User manager = this.userService.findById(dto.manager_id());

        final User creator = this.userService.findById(dto.powered_by());

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
        prospect.getContacts().add(contact);

        AddressRequest addressRequest = new AddressRequest(dto.address().address(), "", "", dto.address().city(), dto.address().region(), "");
        Address address = this.addressService.create(addressRequest);

        prospect.setAddress(address);

        this.prospectRepository.save(prospect);
        this.activityService.saveAll(activities);

        return this.modelMapper.map(this.prospectRepository.save(prospect), ProspectResponse.class);
    }

    @Override
    public ProspectResponse update(UUID id, ProspectRequest dto) {
        Prospect prospect = this.prospectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect not found with id: " + id.toString()));

        prospect.setName(dto.companyName());
        prospect.setStatus(dto.status());

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

        Address existingAddress = prospect.getAddress();
        City city = this.cityService.findByName(dto.address().city());
        Region region =  this.regionService.findByName(dto.address().region());
        existingAddress.setAddress(dto.address().address());
        existingAddress.setCity(city);
        existingAddress.setRegion(region);

        Prospect updatedProspect = this.prospectRepository.save(prospect);
        this.activityService.saveAll(activities);

        return this.modelMapper.map(updatedProspect, ProspectResponse.class);
    }

    @Override
    public void delete(UUID id) {
        this.prospectRepository.softDelete(id);
    }
}
