package net.foodeals.organizationEntity.seeder;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.common.annotations.Seeder;
import net.foodeals.location.application.dtos.requests.AddressRequest;
import net.foodeals.location.application.services.AddressService;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.offer.application.services.DonationService;
import net.foodeals.offer.domain.entities.Donation;
import net.foodeals.offer.domain.entities.Donor;
import net.foodeals.offer.domain.entities.Receiver;
import net.foodeals.offer.domain.enums.DonationReceiverType;
import net.foodeals.offer.domain.enums.DonorType;
import net.foodeals.organizationEntity.application.dtos.requests.CreateAssociationDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityAddressDto;
import net.foodeals.organizationEntity.application.dtos.requests.EntityContactDto;
import net.foodeals.organizationEntity.application.dtos.requests.SubEntityRequest;
import net.foodeals.organizationEntity.application.dtos.responses.AssociationsDto;
import net.foodeals.organizationEntity.application.services.OrganizationEntityService;
import net.foodeals.organizationEntity.application.services.SolutionService;
import net.foodeals.organizationEntity.application.services.SubEntityService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.Solution;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;
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


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createAssociation() {

        if (this.donationService.count() == 0) {
            EntityContactDto entityContactDto1 = new EntityContactDto(new Name("test ", "test "), "t@gmail.com", "06412358976");
            EntityContactDto entityContactDto2 = new EntityContactDto(new Name("test 1 ", "test 1"), "t1@gmail.com", "06412358975");
            EntityAddressDto entityAddressDto = new EntityAddressDto("address", "Casablanca", "maarif", "");

            CreateAssociationDto associationsDto = new CreateAssociationDto("Test Company", List.of("Activity 1"), entityContactDto1, entityContactDto2, entityAddressDto, EntityType.FOOD_BANK, 10, List.of("pro_donate", "dlc"), "test ");

            UUID id = this.organizationEntityService.createAssociation(associationsDto, null, null);
            OrganizationEntity organizationEntity = this.organizationEntityService.getOrganizationEntityById(id);


            SubEntity subEntity2 = SubEntity.builder().name(null)
                    .organizationEntity(organizationEntity)
                    .type(SubEntityType.FOOD_BANK_SB)
                    .address(organizationEntity.getAddress())
                    .build();

            organizationEntity.getSubEntities().addAll(List.of(subEntity2));
            this.subEntityRepository.save(subEntity2);
            this.organizationEntityService.save(organizationEntity);

            Donation donation1 = Donation.builder().donor(new Donor(organizationEntity.getId(), DonorType.FOOD_BANK))
                    .build();
            Donation donation2 = Donation.builder().receiver(new Receiver(organizationEntity.getId(), DonationReceiverType.FOOD_BANK))
                    .build();

            Donation donation3 = Donation.builder().donor(new Donor(subEntity2.getId(), DonorType.FOOD_BANK_ASSOCIATION))
                    .build();
            Donation donation4 = Donation.builder().receiver(new Receiver(subEntity2.getId(), DonationReceiverType.FOOD_BANK_ASSOCIATION))
                    .build();

            this.donationService.saveAll(List.of(donation1, donation2, donation3, donation4));

            System.out.println("organization id -> " + organizationEntity.getId());
        } else {
            System.out.println("------------------------------------------------------");
            this.organizationEntityRepository.findAll().forEach(o -> System.out.println("organization id ->  " + o.getId()));
        }
    }
}
