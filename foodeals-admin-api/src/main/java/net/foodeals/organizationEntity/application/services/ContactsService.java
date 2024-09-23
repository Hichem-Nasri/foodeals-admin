package net.foodeals.organizationEntity.application.services;

import net.foodeals.organizationEntity.application.dtos.requests.EntityContactDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.repositories.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContactsService {

    private final ContactRepository contactRepository;

    public ContactsService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public void delete(Contact contact) {
        this.contactRepository.softDelete(contact.getId());
    }

    public Contact update(Contact contact, EntityContactDto entityContactDto) {
        if (entityContactDto.getName() != null) {
            contact.setName(entityContactDto.getName());
        }
        if (entityContactDto.getEmail() != null) {
            contact.setEmail(entityContactDto.getEmail());
        }
        if (entityContactDto.getPhone() != null) {
            contact.setPhone(entityContactDto.getPhone());
        }
        return this.contactRepository.save(contact);
    }

    public Contact create(EntityContactDto entityContactDto, OrganizationEntity organizationEntity, Boolean isResponsible) {
        Contact contact = Contact.builder().name(entityContactDto.getName())
                .phone(entityContactDto.getPhone())
                .email(entityContactDto.getEmail())
                .isResponsible(isResponsible)
                .organizationEntity(organizationEntity)
                .build();
        return this.contactRepository.save(contact);
    }
}
