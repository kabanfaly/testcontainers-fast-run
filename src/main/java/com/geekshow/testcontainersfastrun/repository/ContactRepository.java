package com.geekshow.testcontainersfastrun.repository;

import com.geekshow.testcontainersfastrun.domain.Contact;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ContactRepository extends ReactiveMongoRepository<Contact, String> {
}
