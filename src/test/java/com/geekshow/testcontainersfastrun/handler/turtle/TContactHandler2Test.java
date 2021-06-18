package com.geekshow.testcontainersfastrun.handler.turtle;

import com.geekshow.testcontainersfastrun.domain.Contact;
import com.geekshow.testcontainersfastrun.dto.ContactDTO;
import com.geekshow.testcontainersfastrun.repository.ContactRepository;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandomBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@EnabledIf("com.geekshow.testcontainersfastrun.handler.TestEnabler#enableTurtle")
public class TContactHandler2Test {

  @Container
  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

  @LocalServerPort
  private int port;

  @Autowired
  private ContactRepository repository;

  private static EnhancedRandom random = aNewEnhancedRandomBuilder().build();
  private WebTestClient webTestClient;
  private Contact contact;

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeEach
  public void setUp() {
    repository.deleteAll().block();
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api")
        .build();
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
