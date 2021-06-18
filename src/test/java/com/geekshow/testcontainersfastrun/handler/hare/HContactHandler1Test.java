package com.geekshow.testcontainersfastrun.handler.hare;

import com.geekshow.testcontainersfastrun.domain.Contact;
import com.geekshow.testcontainersfastrun.dto.ContactDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@EnabledIf("com.geekshow.testcontainersfastrun.handler.TestEnabler#enableHare")
public class HContactHandler1Test extends AbstractIntegrationTests {

  private Contact contact;

  @BeforeEach
  public void setUp() {
    this.initSetup();
    contact = random.nextObject(Contact.class);
    contact.id(null);
  }

  @Test
  @DisplayName("Should find contact by Id")
  void getContactById_found() {
    repository.save(contact).block();
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/contacts/{id}").build(contact.getId()))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(contact.getId())
        .jsonPath("$.name").isEqualTo(contact.getName())
        .jsonPath("$.email").isEqualTo(contact.getEmail())
        .jsonPath("$.address").isEqualTo(contact.getAddress())
        .jsonPath("$.phone").isEqualTo(contact.getPhone());
  }

  @Test
  @DisplayName("Should not find contact by Id")
  void getContactById_notFound() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/contacts/{id}").build("ID"))
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  @DisplayName("Should save a contact")
  void saveContact() {
    var contactDTO = new ContactDTO(null, "John", "john@test", "111 street", "111-111-1111");
    StepVerifier.create(repository.findAll()).expectNextCount(0).verifyComplete();

    webTestClient
        .post()
        .uri("/contacts")
        .contentType(APPLICATION_JSON)
        .body(Mono.just(contactDTO), ContactDTO.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(ContactDTO.class)
        .value(createdContact -> {
          assertThat(createdContact.id).isNotNull();
          assertThat(createdContact.name).isEqualTo(contactDTO.name);
          assertThat(createdContact.email).isEqualTo(contactDTO.email);
          assertThat(createdContact.address).isEqualTo(contactDTO.address);
          assertThat(createdContact.phone).isEqualTo(contactDTO.phone);
        });

    StepVerifier.create(repository.findAll())
        .assertNext(savedContact -> {
          assertThat(savedContact.getId()).isNotNull();
          assertThat(savedContact.getName()).isEqualTo(contactDTO.name);
          assertThat(savedContact.getEmail()).isEqualTo(contactDTO.email);
          assertThat(savedContact.getAddress()).isEqualTo(contactDTO.address);
          assertThat(savedContact.getPhone()).isEqualTo(contactDTO.phone);
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("Save contact: should return a bad request when an Id is defined")
  void saveContact_badRequest() {
    var contactDTO = new ContactDTO("ID", "John", "john@test", "111 street", "111-111-1111");
    StepVerifier.create(repository.findAll()).expectNextCount(0).verifyComplete();

    webTestClient
        .post()
        .uri("/contacts")
        .contentType(APPLICATION_JSON)
        .body(Mono.just(contactDTO), ContactDTO.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();

    StepVerifier.create(repository.findAll()).expectNextCount(0).verifyComplete();
  }

  @Test
  void getAllContacts() {
    repository.save(contact).block();
    webTestClient
        .get()
        .uri("/contacts")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[*].id").isEqualTo(contact.getId())
        .jsonPath("$.[*].name").isEqualTo(contact.getName())
        .jsonPath("$.[*].email").isEqualTo(contact.getEmail())
        .jsonPath("$.[*].address").isEqualTo(contact.getAddress())
        .jsonPath("$.[*].phone").isEqualTo(contact.getPhone());
  }

}
