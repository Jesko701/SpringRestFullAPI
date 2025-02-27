package com.pzn.belajar_spring_boot_pzn.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pzn.belajar_spring_boot_pzn.Entity.Contact;
import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.ContactRepository;
import com.pzn.belajar_spring_boot_pzn.model.ContactResponse;
import com.pzn.belajar_spring_boot_pzn.model.CreateContactRequest;

import jakarta.transaction.Transactional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }
}
