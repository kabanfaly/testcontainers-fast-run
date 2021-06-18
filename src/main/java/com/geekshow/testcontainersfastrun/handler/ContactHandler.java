package com.geekshow.testcontainersfastrun.handler;

import com.geekshow.testcontainersfastrun.dto.ContactDTO;
import com.geekshow.testcontainersfastrun.mapper.ContactMapper;
import com.geekshow.testcontainersfastrun.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
public class ContactHandler {
    public final ContactRepository repository;
    private final ContactMapper contactMapper;

  public ContactHandler(ContactRepository repository, ContactMapper contactMapper) {
    this.repository = repository;
    this.contactMapper = contactMapper;
  }

  public Mono<ServerResponse> getContactById(ServerRequest serverRequest) {
    return repository.findById(serverRequest.pathVariable("id"))
        .map(contactMapper::toContactDTO)
        .flatMap(contactDTO -> ok()
            .contentType(APPLICATION_JSON)
            .body(Mono.just(contactDTO), ContactDTO.class))
        .switchIfEmpty(notFound().build());
  }

  public Mono<ServerResponse> saveContact(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(ContactDTO.class)
        .flatMap(contactDTO -> {
          if (contactDTO.id != null) {
            return badRequest()
                .body(just(new Exception("A new contact cannot already have an ID")), Exception.class);
          }
          return Mono.just(contactMapper.toContact(contactDTO))
              .flatMap(repository::save)
              .flatMap(createdContact -> {
                try {
                  return created(new URI("/api/contacts/" + createdContact.getId()))
                      .contentType(APPLICATION_JSON)
                      .body(just(contactMapper.toContactDTO(createdContact)), ContactDTO.class);
                } catch (URISyntaxException e) {
                  return error(e);
                }
              });
        });
  }

  public Mono<ServerResponse> getAllContacts(ServerRequest serverRequest) {
    return ok().contentType(APPLICATION_JSON)
        .body(repository.findAll().map(contactMapper::toContactDTO), ContactDTO.class);
  }
}
