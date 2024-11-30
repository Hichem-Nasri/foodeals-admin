package net.foodeals.organizationEntity.application.services;

import jakarta.transaction.Transactional;
import net.foodeals.organizationEntity.application.dtos.requests.ContactDto;
import net.foodeals.organizationEntity.domain.entities.Contact;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.exceptions.AssociationCreationException;
import net.foodeals.organizationEntity.domain.exceptions.AssociationUpdateException;
import net.foodeals.organizationEntity.domain.repositories.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactsService {

    private final ContactRepository contactRepository;

    public ContactsService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public void delete(Contact contact) {
        this.contactRepository.softDelete(contact.getId());
    }

    @Transactional
    public Contact update(Contact contact, ContactDto contactDto) {
        if (contactDto.getName() != null) {
            contact.setName(contactDto.getName());
        }
        if (contactDto.getEmail() != null) {
            contact.setEmail(contactDto.getEmail());
        }
        if (contactDto.getPhone() != null) {
            contact.setPhone(contactDto.getPhone());
        }
        return Optional.ofNullable(this.contactRepository.save(contact))
                .orElseThrow(() -> new AssociationUpdateException("Failed to update contact"));
    }

    @Transactional
    public Contact create(ContactDto contactDto, OrganizationEntity organizationEntity, Boolean isResponsible) {
        Contact contact = Contact.builder()
                .name(contactDto.getName())
                .phone(contactDto.getPhone())
                .email(contactDto.getEmail())
                .isResponsible(isResponsible)
                .organizationEntity(organizationEntity)
                .build();
        return Optional.ofNullable(contactRepository.save(contact))
                .orElseThrow(() -> new AssociationCreationException("Failed to create contact"));
    }
}
