package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.annotations.Seeder;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.application.services.OfferService;
import net.foodeals.offer.domain.entities.*;
import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.offer.domain.enums.DonorType;
import net.foodeals.offer.domain.repositories.OfferRepository;
import net.foodeals.order.domain.entities.Order;
import net.foodeals.order.domain.repositories.OrderRepository;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
import net.foodeals.organizationEntity.domain.repositories.ContactRepository;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.organizationEntity.domain.repositories.SubEntityRepository;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.valueObjects.Name;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.*;

@Seeder
@AllArgsConstructor
public class OrganizationEntitySeeder {

    private final OrganizationEntityService organizationEntityService;
    private final SubEntityRepository subEntityRepository;
    private final DonationService donationService;
    private final AddressService addressService;
    private final SolutionService solutionService;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final UserService user;
    private final OfferRepository offerService;
    private final OrderRepository orderRepository;
    private final ContactRepository contactRepository;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createAssociation() {
    }
}
