package com.geekshow.testcontainersfastrun.mapper;

import com.geekshow.testcontainersfastrun.domain.Contact;
import com.geekshow.testcontainersfastrun.dto.ContactDTO;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

  public Contact toContact(ContactDTO contactDTO) {
    return new Contact()
        .id(contactDTO.id)
        .name(contactDTO.name)
        .email(contactDTO.email)
        .address(contactDTO.address)
        .phone(contactDTO.phone);
  }

  public ContactDTO toContactDTO(Contact contact) {
    return new ContactDTO(contact.getId(), contact.getName(), contact.getEmail(), contact.getAddress(), contact.getPhone());
  }
}
