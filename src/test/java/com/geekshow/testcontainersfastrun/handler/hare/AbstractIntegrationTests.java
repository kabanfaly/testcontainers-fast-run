package com.geekshow.testcontainersfastrun.handler.hare;

import com.geekshow.testcontainersfastrun.repository.ContactRepository;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandomBuilder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class AbstractIntegrationTests {

  public static final String MONGO_IMAGE = "mongo:4.0.10";

  @LocalServerPort
  private int port;

  @Autowired
  protected ContactRepository repository;
  protected WebTestClient webTestClient;
  protected static EnhancedRandom random = aNewEnhancedRandomBuilder().build();

  protected static final MongoDBContainer mongoDBContainer;
  static {
    mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
        .withReuse(true)
        .withLabel("reuse.UUID", "fb36b8c0-e03f-4d30-ad3a-38f16a8e0078");
    mongoDBContainer.start();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  public void initSetup() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api")
        .build();
    repository.deleteAll().block();
  }
}
